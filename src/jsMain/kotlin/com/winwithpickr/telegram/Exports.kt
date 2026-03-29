@file:OptIn(ExperimentalJsExport::class)

package com.winwithpickr.telegram

import com.winwithpickr.core.verifyPick as engineVerifyPick

// ── Verify (delegating to engine) ───────────────────────────────────────────

@JsExport
data class VerifyResult(
    val winners: Array<String>,
    val poolHash: String,
)

@JsExport
fun verifyPick(seed: String, poolIds: Array<String>, winnerCount: Int): VerifyResult {
    val result = engineVerifyPick(seed, poolIds, winnerCount)
    return VerifyResult(winners = result.winners, poolHash = result.poolHash)
}

// ── Parser ──────────────────────────────────────────────────────────────────

@JsExport
data class ParseResult(
    val valid: Boolean,
    val winners: Int = 1,
    val mode: String = "IMMEDIATE",
    val comments: Boolean = true,
    val buttons: Boolean = true,
    val memberOfHost: Boolean = false,
    val memberOfChannels: Array<String> = emptyArray(),
    val scheduledDelay: String? = null,
)

@JsExport
fun parseCommand(text: String, botUsername: String = "winwithpickr_bot"): ParseResult {
    val cmd = TelegramCommandParser.parse(text, botUsername)
        ?: return ParseResult(valid = false)

    val delay = cmd.scheduledDelayMs?.let { ms ->
        if (ms < 86_400_000) "${ms / 3_600_000}h" else "${ms / 86_400_000}d"
    }

    return ParseResult(
        valid = true,
        winners = cmd.winners,
        mode = cmd.triggerMode.name,
        comments = cmd.conditions.comments,
        buttons = cmd.conditions.buttons,
        memberOfHost = cmd.conditions.memberOfHost,
        memberOfChannels = cmd.conditions.memberOfChannels.toTypedArray(),
        scheduledDelay = delay,
    )
}
