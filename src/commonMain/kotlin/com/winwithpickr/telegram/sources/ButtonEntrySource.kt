package com.winwithpickr.telegram.sources

import com.winwithpickr.core.pipeline.PipelineContext
import com.winwithpickr.core.pipeline.PoolSource
import com.winwithpickr.telegram.TelegramDataSource
import com.winwithpickr.telegram.models.TelegramCandidate

/**
 * Pool source that fetches users who pressed the inline "Enter Giveaway" button.
 * Entries are accumulated in Redis by the server's TelegramUpdateHandler.
 */
class ButtonEntrySource(
    private val dataSource: TelegramDataSource,
    private val chatId: Long,
    private val messageId: Long,
) : PoolSource<TelegramCandidate> {

    override val name: String = "button_entries"

    override suspend fun fetch(context: PipelineContext): List<TelegramCandidate> {
        return dataSource.getButtonEntries(chatId, messageId).map { user ->
            TelegramCandidate(
                id = user.candidateId,
                displayName = user.displayName,
                isPremium = user.isPremium,
            )
        }
    }

    override fun intersects(context: PipelineContext): Boolean = false
}
