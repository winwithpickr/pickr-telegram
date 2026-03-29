package com.winwithpickr.telegram

import com.winwithpickr.core.models.TierConfig
import com.winwithpickr.core.pipeline.PoolFilter
import com.winwithpickr.core.pipeline.PoolPipeline
import com.winwithpickr.core.pipeline.PoolSource
import com.winwithpickr.telegram.filters.MemberFilter
import com.winwithpickr.telegram.filters.MemberOfChannelsFilter
import com.winwithpickr.telegram.models.TelegramCandidate
import com.winwithpickr.telegram.models.TelegramEntryConditions
import com.winwithpickr.telegram.sources.ButtonEntrySource
import com.winwithpickr.telegram.sources.CommentSource

/**
 * Assembles entry pool pipeline from Telegram conditions and tier config.
 * Mirrors the pattern of XPoolBuilder for X/Twitter.
 */
object TelegramPoolBuilder {

    /**
     * Build a pool pipeline for a Telegram giveaway.
     *
     * @param dataSource Telegram Bot API access
     * @param chatId Chat where giveaway was posted
     * @param messageId Giveaway message ID
     * @param discussionChatId Discussion group chat ID (for comments), null if none
     * @param discussionMessageId Thread root message ID in discussion group
     * @param conditions Entry conditions from the parsed command
     * @param tierConfig Effective tier config (with add-on features injected)
     * @return Built PoolPipeline ready for execution
     */
    fun build(
        dataSource: TelegramDataSource,
        chatId: Long,
        messageId: Long,
        discussionChatId: Long? = null,
        discussionMessageId: Long? = null,
        conditions: TelegramEntryConditions,
        tierConfig: TierConfig,
    ): PoolPipeline<TelegramCandidate> {
        val sources = mutableListOf<PoolSource<TelegramCandidate>>()
        val filters = mutableListOf<PoolFilter<TelegramCandidate>>()

        // Sources
        if (conditions.buttons) {
            sources.add(ButtonEntrySource(dataSource, chatId, messageId))
        }
        if (conditions.comments && discussionChatId != null && discussionMessageId != null) {
            sources.add(CommentSource(dataSource, discussionChatId, discussionMessageId))
        }

        // Default to button entries if no sources configured
        if (sources.isEmpty()) {
            sources.add(ButtonEntrySource(dataSource, chatId, messageId))
        }

        // Filters
        if (conditions.memberOfHost && tierConfig.hasFeature(TelegramFeatures.MEMBER_CHECK)) {
            filters.add(MemberFilter(dataSource, chatId))
        }
        if (conditions.memberOfChannels.isNotEmpty() && tierConfig.hasFeature(TelegramFeatures.MEMBER_OF_CHANNELS_CHECK)) {
            filters.add(MemberOfChannelsFilter(dataSource, conditions.memberOfChannels))
        }

        return PoolPipeline(
            sources = sources,
            filters = filters,
        )
    }
}
