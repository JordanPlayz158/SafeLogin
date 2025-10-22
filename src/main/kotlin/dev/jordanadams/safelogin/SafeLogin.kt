package dev.jordanadams.safelogin

import com.google.gson.GsonBuilder
import com.mojang.brigadier.Command
import dev.jordanadams.safelogin.command.ReloadCommand
import dev.jordanadams.safelogin.config.SafeLoginConfig
import dev.jordanadams.safelogin.protection.ProtectionEvaluator
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.UUID

const val MOD_ID = "safelogin"

object SafeLogin : ModInitializer {
  private val logger: Logger = LoggerFactory.getLogger(MOD_ID)

  private val gson = GsonBuilder().setLenient().create()

  private val configDirectory = File("config/$MOD_ID")
  private val configFile = File(configDirectory, "config.json5")

	// Player UUIDs invulnerable due to SafeLogin, to be removed on input packet
	private val playersInvulnerable = HashMap<UUID, BlockPos>()
  private val playerCachedNames = HashMap<UUID, String>()

  private lateinit var config: SafeLoginConfig
  private lateinit var evaluator: ProtectionEvaluator

	override fun onInitialize() {
    createConfig()
    loadConfig()

    registerCommands()

		ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
			setInvulnerability(handler.player, true)
		}

    // Cleanup any players
    ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
      val player = handler.player
      setInvulnerability(player, false)
      evaluator.onPlayerDisconnect(player.uuid)
    }
  }

  private fun createConfig() {
    if (configDirectory.mkdir() || !configFile.exists()) {
      SafeLogin::class.java.getResourceAsStream('/' + configFile.name)!!.use { configInputStream ->
        configFile.outputStream().use { configOutputStream ->
          configInputStream.copyTo(configOutputStream)
        }
      }
    }
  }

  private fun loadConfig() {
    config = gson.fromJson(configFile.reader(), SafeLoginConfig::class.java)
    evaluator = config.protectionType.getEvaluator()
  }

  private fun registerCommands() {
    val protectionDisableCommand = Command<ServerCommandSource> { context ->
      if (!context.source.isExecutedByPlayer) {
        return@Command 0
      }

      setInvulnerability(context.source.player!!, false)
      return@Command 1
    }

    CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
      dispatcher.register(CommandManager.literal("safelogin")
        .then(CommandManager.literal("reload")
            .executes(ReloadCommand()))
        .then(CommandManager.literal(config.command).executes(protectionDisableCommand)))

      dispatcher.register(CommandManager.literal(config.command).executes(protectionDisableCommand))
    }
  }


	fun invulnerabilityCheck(player: ServerPlayerEntity, proposedPosition: BlockPos, acceptedPosition: BlockPos) {
    if (evaluator.evaluate(player, proposedPosition, acceptedPosition)) {
      setInvulnerability(player, false)
    }
	}

  fun isPlayerInvulnerable(uuid: UUID) = getInvulnerablePlayerPosition(uuid) !== null

  fun getInvulnerablePlayerPosition(uuid: UUID) =
    playersInvulnerable[uuid]

  fun setInvulnerability(player: ServerPlayerEntity, invulnerable: Boolean) {
		player.abilities.invulnerable = invulnerable
		// Ensure hostile mobs won't track to player and huddle around them
		//   and not sure how creepers would react to invulnerable player
		player.isInvisible = invulnerable
    // Prevents fluids from pushing you
    player.abilities.flying = invulnerable
    player.sendAbilitiesUpdate()

    val gameProfile = player.gameProfile
    val playerName = gameProfile.name

    if (!player.isDisconnected) {
      if (invulnerable) {
        logger.debug("Player '{}' is invulnerable due to login", playerName)
      } else {
        logger.debug("Player '{}' is no longer invulnerable due to {}", playerName, evaluator)

        if (config.delayJoinMessage) {
          val cachedPlayerName = playerCachedNames[player.uuid]
          val playerDisplayName = player.displayName

          val joinText = if (playerName.equals(cachedPlayerName, true)) {
            Text.translatable("multiplayer.player.joined", playerDisplayName)
          } else {
            Text.translatable("multiplayer.player.joined.renamed", playerDisplayName, cachedPlayerName)
          }
          player.server.playerManager.broadcast(joinText.formatted(Formatting.YELLOW), false)
        }

        player.sendMessage(Text.translatable("message.safelogin.disable_protection"), false)
      }
    } else {
      logger.debug("Player '{}' is no longer invulnerable due to disconnecting", playerName)
    }

		if (invulnerable) {
			playersInvulnerable[player.uuid] = player.blockPos
		} else {
			playersInvulnerable.remove(player.uuid)
		}
	}

  fun setCachedPlayerName(uuid: UUID, name: String) {
    playerCachedNames[uuid] = name
  }

  fun getProtectionDisableCommand() = config.command

  fun getEvaluator() = evaluator

  fun reload() = loadConfig()

  fun getLogger() = logger
}