package dev.jordanadams.safelogin.config

import dev.jordanadams.safelogin.protection.type.ProtectionType

data class SafeLoginConfig(val version: Int, val protectionType: ProtectionType, val delayJoinMessage: Boolean, val command: String)