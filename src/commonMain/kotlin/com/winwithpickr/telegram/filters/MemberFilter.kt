package com.winwithpickr.telegram.filters

import com.winwithpickr.core.pipeline.PipelineContext
import com.winwithpickr.core.pipeline.PoolFilter
import com.winwithpickr.telegram.TelegramDataSource
import com.winwithpickr.telegram.models.TelegramCandidate

/**
 * Filters candidates to only those who are members of the host channel/group.
 * Uses `getChatMember` Bot API call (free, no per-request cost).
 */
class MemberFilter(
    private val dataSource: TelegramDataSource,
    private val chatId: Long,
) : PoolFilter<TelegramCandidate> {

    override val name: String = "member_filter"

    override suspend fun apply(candidates: MutableMap<String, TelegramCandidate>, context: PipelineContext) {
        if (candidates.isEmpty()) return

        val toRemove = mutableListOf<String>()
        for ((id, _) in candidates) {
            val userId = id.removePrefix("tg:").toLongOrNull()
            if (userId == null) {
                toRemove.add(id)
                continue
            }
            try {
                if (!dataSource.isChatMember(chatId, userId)) {
                    toRemove.add(id)
                }
            } catch (_: Exception) {
                // If membership check fails (rate limit, etc.), keep the candidate
            }
        }
        toRemove.forEach { candidates.remove(it) }
    }
}
