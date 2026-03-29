package com.winwithpickr.telegram.models

import kotlinx.serialization.Serializable

/**
 * Entry conditions parsed from a Telegram `/pick` command.
 */
@Serializable
data class TelegramEntryConditions(
    /** Include comment authors in the entry pool. */
    val comments: Boolean = true,
    /** Include button-press entries in the entry pool. */
    val buttons: Boolean = true,
    /** Require entrants to be members of the host channel. */
    val memberOfHost: Boolean = false,
    /** Require entrants to be members of these additional channels (usernames without @). */
    val memberOfChannels: List<String> = emptyList(),
)
