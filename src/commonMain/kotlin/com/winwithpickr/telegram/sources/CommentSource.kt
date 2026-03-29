package com.winwithpickr.telegram.sources

import com.winwithpickr.core.pipeline.PipelineContext
import com.winwithpickr.core.pipeline.PoolSource
import com.winwithpickr.telegram.TelegramDataSource
import com.winwithpickr.telegram.models.TelegramCandidate

/**
 * Pool source that fetches comment authors from a discussion group thread.
 * Deduplicates by user ID (same user commenting twice = one entry).
 */
class CommentSource(
    private val dataSource: TelegramDataSource,
    private val discussionChatId: Long,
    private val discussionMessageId: Long,
) : PoolSource<TelegramCandidate> {

    override val name: String = "comments"

    override suspend fun fetch(context: PipelineContext): List<TelegramCandidate> {
        return dataSource.getCommentAuthors(discussionChatId, discussionMessageId)
            .distinctBy { it.id }
            .map { user ->
                TelegramCandidate(
                    id = user.candidateId,
                    displayName = user.displayName,
                    isPremium = user.isPremium,
                )
            }
    }

    override fun intersects(context: PipelineContext): Boolean = false
}
