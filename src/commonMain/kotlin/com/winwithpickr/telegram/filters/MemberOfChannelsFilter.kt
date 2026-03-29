package com.winwithpickr.telegram.filters

import com.winwithpickr.core.pipeline.PipelineContext
import com.winwithpickr.core.pipeline.PoolFilter
import com.winwithpickr.telegram.TelegramDataSource
import com.winwithpickr.telegram.models.TelegramCandidate

/**
 * Filters candidates to only those who are members of ALL specified channels.
 * Channels are resolved by username → chatId before checking membership.
 */
class MemberOfChannelsFilter(
    private val dataSource: TelegramDataSource,
    private val channelUsernames: List<String>,
) : PoolFilter<TelegramCandidate> {

    override val name: String = "member_of_channels_filter"

    override suspend fun apply(candidates: MutableMap<String, TelegramCandidate>, context: PipelineContext) {
        if (candidates.isEmpty() || channelUsernames.isEmpty()) return

        // Resolve channel usernames to chat IDs
        val channelChatIds = channelUsernames.mapNotNull { username ->
            dataSource.resolveChatId(username)
        }

        if (channelChatIds.isEmpty()) return

        val toRemove = mutableListOf<String>()
        for ((id, _) in candidates) {
            val userId = id.removePrefix("tg:").toLongOrNull()
            if (userId == null) {
                toRemove.add(id)
                continue
            }
            val isMemberOfAll = channelChatIds.all { chatId ->
                try {
                    dataSource.isChatMember(chatId, userId)
                } catch (_: Exception) {
                    true // Keep on error
                }
            }
            if (!isMemberOfAll) {
                toRemove.add(id)
            }
        }
        toRemove.forEach { candidates.remove(it) }
    }
}
