package com.winwithpickr.telegram

import com.winwithpickr.telegram.models.TelegramUser

/**
 * Interface for Telegram Bot API data access.
 * Implemented by the server's TelegramClient.
 */
interface TelegramDataSource {
    /**
     * Get accumulated button-press entries for a giveaway message.
     * @param chatId The chat where the giveaway was posted
     * @param messageId The giveaway message ID
     * @return List of users who pressed the entry button
     */
    suspend fun getButtonEntries(chatId: Long, messageId: Long): List<TelegramUser>

    /**
     * Get comment authors from the discussion group thread linked to a giveaway.
     * @param chatId The discussion group chat ID
     * @param messageId The thread root message ID
     * @return List of users who commented
     */
    suspend fun getCommentAuthors(chatId: Long, messageId: Long): List<TelegramUser>

    /**
     * Check if a user is a member of a channel/group.
     * @param chatId The channel or group to check
     * @param userId The user to check membership for
     * @return true if the user is a member (member, admin, or creator status)
     */
    suspend fun isChatMember(chatId: Long, userId: Long): Boolean

    /**
     * Resolve a channel username to its chat ID.
     * @param username Channel username (without @)
     * @return Chat ID, or null if not found
     */
    suspend fun resolveChatId(username: String): Long?
}
