package com.winwithpickr.telegram.models

import kotlinx.serialization.Serializable

/**
 * Telegram user identity — maps to the Bot API `User` object.
 * Implements Candidate-compatible interface for the engine pipeline.
 */
@Serializable
data class TelegramUser(
    val id: Long,
    val username: String? = null,
    val firstName: String = "",
    val lastName: String? = null,
    val isPremium: Boolean = false,
) {
    /** Stable string ID for engine pipeline (prefixed to avoid collision with X numeric IDs). */
    val candidateId: String get() = "tg:$id"

    /** Display name: @username if available, otherwise first+last name. */
    val displayName: String
        get() = username?.let { "@$it" } ?: listOfNotNull(firstName, lastName).joinToString(" ")
}
