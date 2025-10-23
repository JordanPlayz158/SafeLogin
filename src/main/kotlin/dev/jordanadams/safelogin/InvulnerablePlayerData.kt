package dev.jordanadams.safelogin

import net.minecraft.util.math.BlockPos

data class InvulnerablePlayerData(val position: BlockPos, val invulnerability: Boolean, val invisibility: Boolean, val flying: Boolean)
