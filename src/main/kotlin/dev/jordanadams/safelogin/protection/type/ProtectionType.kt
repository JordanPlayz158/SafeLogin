package dev.jordanadams.safelogin.protection.type

import dev.jordanadams.safelogin.protection.ProtectionEvaluator

enum class ProtectionType {
  MOVE,
  COMMAND,
  GUI;

  fun getEvaluator(): ProtectionEvaluator {
    return when (this) {
      MOVE -> MoveProtectionEvaluator()
      COMMAND -> CommandProtectionEvaluator()
      GUI -> GuiProtectionEvaluator()
    }
  }
}