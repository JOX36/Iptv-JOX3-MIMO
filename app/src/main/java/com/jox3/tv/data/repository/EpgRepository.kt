package com.jox3.tv.data.repository

import com.jox3.tv.data.remote.api.EpgParser
import com.jox3.tv.data.remote.api.XtreamApi
import com.jox3.tv.domain.model.EpgProgram
import com.jox3.tv.domain.model.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpgRepository @Inject constructor(
    private val api: XtreamApi,
    private val epgParser: EpgParser
) {
    private var fullEpgCache: Map<String, List<EpgProgram>> = emptyMap()
    private var lastFetchTime: Long = 0
    private val cacheValidityMs = 30 * 60 * 1000L // 30 minutes

    suspend fun getFullEpg(config: ServerConfig): Map<String, List<EpgProgram>> {
        val now = System.currentTimeMillis()
        if (fullEpgCache.isNotEmpty() && (now - lastFetchTime) < cacheValidityMs) {
            return fullEpgCache
        }
        return withContext(Dispatchers.IO) {
            try {
                val responseBody = api.getFullEpg(config.username, config.password)
                val result = responseBody.byteStream().use { stream ->
                    epgParser.parse(stream)
                }
                fullEpgCache = result
                lastFetchTime = now
                result
            } catch (_: Exception) {
                fullEpgCache
            }
        }
    }

    suspend fun getProgramsForChannel(config: ServerConfig, epgChannelId: String): List<EpgProgram> {
        val epg = getFullEpg(config)
        return epg[epgChannelId]?.sortedBy { it.startTime } ?: emptyList()
    }

    suspend fun getCurrentProgram(config: ServerConfig, epgChannelId: String): EpgProgram? {
        val programs = getProgramsForChannel(config, epgChannelId)
        val now = System.currentTimeMillis()
        return programs.find { now in it.startTime..it.endTime }
    }
}
