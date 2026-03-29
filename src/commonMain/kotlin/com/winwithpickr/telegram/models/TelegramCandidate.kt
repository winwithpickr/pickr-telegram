package com.winwithpickr.telegram.models

import com.winwithpickr.core.models.Candidate

/**
 * Telegram-specific candidate in a giveaway pool.
 */
data class TelegramCandidate(
    override val id: String,
    override val displayName: String,
    val isPremium: Boolean = false,
) : Candidate
