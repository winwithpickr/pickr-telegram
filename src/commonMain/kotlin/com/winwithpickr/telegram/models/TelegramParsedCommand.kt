package com.winwithpickr.telegram.models

import com.winwithpickr.core.models.TriggerMode
import kotlinx.serialization.Serializable

/**
 * Parsed result of a Telegram `/pick` command.
 */
@Serializable
data class TelegramParsedCommand(
    val winners: Int = 1,
    val conditions: TelegramEntryConditions = TelegramEntryConditions(),
    val triggerMode: TriggerMode = TriggerMode.IMMEDIATE,
    val scheduledDelayMs: Long? = null,
)
