package com.jox3.tv.data.repository

import com.jox3.tv.data.local.dao.CategoryDao
import com.jox3.tv.data.local.dao.LiveChannelDao
import com.jox3.tv.data.local.entity.CategoryEntity
import com.jox3.tv.data.local.entity.LiveChannelEntity
import com.jox3.tv.data.remote.api.XtreamApi
import com.jox3.tv.data.remote.dto.EpgListingDto
import com.jox3.tv.data.remote.dto.LiveStreamDto
import com.jox3.tv.domain.model.Category
import com.jox3.tv.domain.model.EpgProgram
import com.jox3.tv.domain.model.LiveChannel
import com.jox3.tv.domain.model.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveRepository @Inject constructor(
    private val liveChannelDao: LiveChannelDao,
    private val categoryDao: CategoryDao,
    private val api: XtreamApi
) {
    fun getCategories(accountId: Long): Flow<List<Category>> =
        categoryDao.getByType("live", accountId).map { list ->
            list.map { Category(it.categoryId, it.categoryName) }
        }

    fun getChannels(accountId: Long): Flow<List<LiveChannel>> =
        liveChannelDao.getAll(accountId).map { list -> list.map { it.toDomain() } }

    fun getChannelsByCategory(accountId: Long, categoryId: String): Flow<List<LiveChannel>> =
        liveChannelDao.getByCategory(accountId, categoryId).map { list -> list.map { it.toDomain() } }

    fun getFavorites(accountId: Long): Flow<List<LiveChannel>> =
        liveChannelDao.getFavorites(accountId).map { list -> list.map { it.toDomain() } }

    fun searchChannels(accountId: Long, query: String): Flow<List<LiveChannel>> =
        liveChannelDao.search(accountId, query).map { list -> list.map { it.toDomain() } }

    fun getRecentlyWatched(accountId: Long): Flow<List<LiveChannel>> =
        liveChannelDao.getRecentlyWatched(accountId).map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(streamId: Int, accountId: Long) {
        val current = liveChannelDao.getByStreamId(streamId, accountId)
        liveChannelDao.setFavorite(streamId, accountId, !(current?.isFavorite ?: false))
    }

    suspend fun markWatched(streamId: Int, accountId: Long) {
        liveChannelDao.updateLastWatched(streamId, accountId)
    }

    suspend fun refreshCategories(config: ServerConfig) {
        val categories = api.getLiveCategories(config.username, config.password)
        categoryDao.deleteByType("live", config.id)
        categoryDao.insertAll(categories.map {
            CategoryEntity(
                categoryId = it.categoryId ?: "",
                type = "live",
                accountId = config.id,
                categoryName = it.categoryName ?: ""
            )
        })
    }

    suspend fun refreshChannels(config: ServerConfig, categoryId: String? = null) {
        val channels = api.getLiveStreams(
            config.username, config.password,
            categoryId = categoryId
        )
        if (categoryId == null) {
            liveChannelDao.deleteAllForAccount(config.id)
        }
        liveChannelDao.insertAll(channels.map { it.toEntity(config.id) })
    }

    suspend fun getShortEpg(config: ServerConfig, streamId: Int): List<EpgProgram> {
        return try {
            val response = api.getShortEpg(config.username, config.password, streamId = streamId)
            response.epgListings?.mapNotNull { it.toProgram() } ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getStreamUrl(config: ServerConfig, streamId: Int): String {
        return "${config.streamBaseUrl}$streamId.m3u8"
    }

    private fun LiveStreamDto.toEntity(accountId: Long) = LiveChannelEntity(
        streamId = streamId ?: 0,
        accountId = accountId,
        name = name ?: "",
        streamIcon = streamIcon,
        epgChannelId = epgChannelId,
        categoryId = categoryId,
        categoryName = categoryName,
        added = added
    )

    private fun LiveChannelEntity.toDomain() = LiveChannel(
        streamId = streamId,
        name = name,
        icon = streamIcon,
        epgChannelId = epgChannelId,
        categoryId = categoryId,
        categoryName = categoryName,
        isFavorite = isFavorite
    )

    private fun EpgListingDto.toProgram(): EpgProgram? {
        val startTime = parseEpgDateTime(start ?: return null)
        val endTime = parseEpgDateTime(end ?: return null)
        return EpgProgram(
            channelId = epgId ?: "",
            title = title ?: "",
            description = description ?: "",
            startTime = startTime,
            endTime = endTime
        )
    }

    private fun parseEpgDateTime(dateStr: String): Long {
        return try {
            val cleaned = dateStr.trim()
            val formats = listOf(
                java.text.SimpleDateFormat("yyyyMMddHHmmss Z", java.util.Locale.US),
                java.text.SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }
            )
            for (fmt in formats) {
                try { return fmt.parse(cleaned)?.time ?: 0L } catch (_: Exception) {}
            }
            0L
        } catch (_: Exception) { 0L }
    }
}
