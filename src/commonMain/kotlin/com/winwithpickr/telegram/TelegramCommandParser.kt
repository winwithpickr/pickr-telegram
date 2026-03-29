package com.winwithpickr.telegram

import com.winwithpickr.core.models.TriggerMode
import com.winwithpickr.telegram.models.TelegramEntryConditions
import com.winwithpickr.telegram.models.TelegramParsedCommand

/**
 * Parses Telegram `/pick` commands into structured commands.
 *
 * Supported formats:
 * - `/pick` — pick 1 winner immediately
 * - `/pick 3` — pick 3 winners
 * - `/pick setup` — watch mode (wait for trigger)
 * - `/pick in 2h` — scheduled pick
 * - `/pick members` — require host channel membership
 * - `/pick members @channel1 @channel2` — require multi-channel membership
 * - `/pick 3 members @channel1` — combined
 */
object TelegramCommandParser {

    private val PICK_REGEX = Regex(
        """^/pick(?:@(\w+))?(.*)$""",
        RegexOption.IGNORE_CASE,
    )

    private val SCHEDULE_REGEX = Regex(
        """in\s+(\d+)\s*(h|d|hr|hrs|hour|hours|day|days)""",
        RegexOption.IGNORE_CASE,
    )

    private val WINNER_COUNT_REGEX = Regex("""(\d+)""")

    /**
     * Parse a `/pick` command text.
     *
     * @param text The full message text
     * @param botUsername The bot's username (without @) — commands addressed to other bots are rejected
     * @return Parsed command, or null if text is not a valid `/pick` command
     */
    fun parse(text: String, botUsername: String = "winwithpickr_bot"): TelegramParsedCommand? {
        val match = PICK_REGEX.matchEntire(text.trim()) ?: return null

        // If command is addressed to a specific bot, it must be ours
        val addressedTo = match.groupValues[1]
        if (addressedTo.isNotEmpty() && !addressedTo.equals(botUsername, ignoreCase = true)) {
            return null
        }

        val args = match.groupValues[2].trim()
        if (args.isEmpty()) {
            return TelegramParsedCommand()
        }

        // Check for setup/watch mode
        if (args.equals("setup", ignoreCase = true) || args.equals("watch", ignoreCase = true)) {
            return TelegramParsedCommand(triggerMode = TriggerMode.WATCH)
        }

        var winners = 1
        var triggerMode = TriggerMode.IMMEDIATE
        var scheduledDelayMs: Long? = null
        var memberOfHost = false
        val memberOfChannels = mutableListOf<String>()

        // Parse scheduled delay
        val scheduleMatch = SCHEDULE_REGEX.find(args)
        if (scheduleMatch != null) {
            val amount = scheduleMatch.groupValues[1].toLongOrNull() ?: return null
            val unit = scheduleMatch.groupValues[2].lowercase()
            scheduledDelayMs = when {
                unit.startsWith("h") -> amount * 3_600_000
                unit.startsWith("d") -> amount * 86_400_000
                else -> return null
            }
            triggerMode = TriggerMode.SCHEDULED
        }

        // Parse remaining args (remove schedule portion)
        val remaining = if (scheduleMatch != null) {
            args.removeRange(scheduleMatch.range).trim()
        } else {
            args
        }

        val tokens = remaining.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            when {
                token.equals("members", ignoreCase = true) || token.equals("member", ignoreCase = true) -> {
                    memberOfHost = true
                    // Consume following @channel tokens
                    while (i + 1 < tokens.size && tokens[i + 1].startsWith("@")) {
                        memberOfChannels.add(tokens[i + 1].removePrefix("@"))
                        i++
                    }
                }
                token.toIntOrNull() != null -> {
                    winners = token.toInt().coerceIn(1, 100)
                }
            }
            i++
        }

        return TelegramParsedCommand(
            winners = winners,
            conditions = TelegramEntryConditions(
                memberOfHost = memberOfHost,
                memberOfChannels = memberOfChannels,
            ),
            triggerMode = triggerMode,
            scheduledDelayMs = scheduledDelayMs,
        )
    }
}
