package com.winwithpickr.telegram

import com.winwithpickr.core.models.TriggerMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TelegramCommandParserTest {

    @Test
    fun basicPick() {
        val cmd = TelegramCommandParser.parse("/pick")
        assertNotNull(cmd)
        assertEquals(1, cmd.winners)
        assertEquals(TriggerMode.IMMEDIATE, cmd.triggerMode)
    }

    @Test
    fun pickWithWinnerCount() {
        val cmd = TelegramCommandParser.parse("/pick 3")
        assertNotNull(cmd)
        assertEquals(3, cmd.winners)
    }

    @Test
    fun pickSetup() {
        val cmd = TelegramCommandParser.parse("/pick setup")
        assertNotNull(cmd)
        assertEquals(TriggerMode.WATCH, cmd.triggerMode)
    }

    @Test
    fun pickStart() {
        val cmd = TelegramCommandParser.parse("/pick start")
        assertNotNull(cmd)
        assertEquals(TriggerMode.WATCH, cmd.triggerMode)
    }

    @Test
    fun pickScheduledHours() {
        val cmd = TelegramCommandParser.parse("/pick in 2h")
        assertNotNull(cmd)
        assertEquals(TriggerMode.SCHEDULED, cmd.triggerMode)
        assertEquals(7_200_000L, cmd.scheduledDelayMs)
    }

    @Test
    fun pickScheduledDays() {
        val cmd = TelegramCommandParser.parse("/pick in 1d")
        assertNotNull(cmd)
        assertEquals(TriggerMode.SCHEDULED, cmd.triggerMode)
        assertEquals(86_400_000L, cmd.scheduledDelayMs)
    }

    @Test
    fun pickMembers() {
        val cmd = TelegramCommandParser.parse("/pick members")
        assertNotNull(cmd)
        assertTrue(cmd.conditions.memberOfHost)
        assertTrue(cmd.conditions.memberOfChannels.isEmpty())
    }

    @Test
    fun pickMembersWithChannels() {
        val cmd = TelegramCommandParser.parse("/pick members @channel1 @channel2")
        assertNotNull(cmd)
        assertTrue(cmd.conditions.memberOfHost)
        assertEquals(listOf("channel1", "channel2"), cmd.conditions.memberOfChannels)
    }

    @Test
    fun pickCombined() {
        val cmd = TelegramCommandParser.parse("/pick 5 members @mychannel")
        assertNotNull(cmd)
        assertEquals(5, cmd.winners)
        assertTrue(cmd.conditions.memberOfHost)
        assertEquals(listOf("mychannel"), cmd.conditions.memberOfChannels)
    }

    @Test
    fun pickAddressedToOurBot() {
        val cmd = TelegramCommandParser.parse("/pick@winwithpickr_bot 3")
        assertNotNull(cmd)
        assertEquals(3, cmd.winners)
    }

    @Test
    fun pickAddressedToOtherBot() {
        val cmd = TelegramCommandParser.parse("/pick@otherbot 3")
        assertNull(cmd)
    }

    @Test
    fun notAPickCommand() {
        assertNull(TelegramCommandParser.parse("hello"))
        assertNull(TelegramCommandParser.parse("/start"))
        assertNull(TelegramCommandParser.parse("pick 3"))
    }

    @Test
    fun winnerCountCappedAt100() {
        val cmd = TelegramCommandParser.parse("/pick 999")
        assertNotNull(cmd)
        assertEquals(100, cmd.winners)
    }

    @Test
    fun extraWhitespace() {
        val cmd = TelegramCommandParser.parse("  /pick   3   members   @ch1   ")
        assertNotNull(cmd)
        assertEquals(3, cmd.winners)
        assertTrue(cmd.conditions.memberOfHost)
        assertEquals(listOf("ch1"), cmd.conditions.memberOfChannels)
    }

    @Test
    fun caseInsensitive() {
        val cmd = TelegramCommandParser.parse("/PICK SETUP")
        assertNotNull(cmd)
        assertEquals(TriggerMode.WATCH, cmd.triggerMode)
    }

    @Test
    fun scheduledWithWinners() {
        val cmd = TelegramCommandParser.parse("/pick 3 in 1h")
        assertNotNull(cmd)
        assertEquals(3, cmd.winners)
        assertEquals(TriggerMode.SCHEDULED, cmd.triggerMode)
        assertEquals(3_600_000L, cmd.scheduledDelayMs)
    }
}
