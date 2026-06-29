package com.jox3.tv.data.repository

import com.jox3.tv.data.local.dao.CategoryDao
import com.jox3.tv.data.local.dao.SeriesDao
import com.jox3.tv.data.local.entity.CategoryEntity
import com.jox3.tv.data.local.entity.SeriesEntity
import com.jox3.tv.data.remote.api.XtreamApi
import com.jox3.tv.data.remote.dto.SeriesStreamDto
import com.jox3.tv.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepository @Inject constructor(
    private val seriesDao: SeriesDao,
    private val categoryDao: CategoryDao,
    private val api: XtreamApi
) {
    fun getCategories(accountId: Long): Flow<List<Category>> =
        categoryDao.getByType("series", accountId).map { list ->
            list.map { Category(it.categoryId, it.categoryName) }
        }

    fun getSeries(accountId: Long): Flow<List<Series>> =
        seriesDao.getAll(accountId).map { list -> list.map { it.toDomain() } }

    fun getSeriesByCategory(accountId: Long, categoryId: String): Flow<List<Series>> =
        seriesDao.getByCategory(accountId, categoryId).map { list -> list.map { it.toDomain() } }

    fun getFavorites(accountId: Long): Flow<List<Series>> =
        seriesDao.getFavorites(accountId).map { list -> list.map { it.toDomain() } }

    fun searchSeries(accountId: Long, query: String): Flow<List<Series>> =
        seriesDao.search(accountId, query).map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(seriesId: Int, accountId: Long) {
        // Toggle logic
    }

    suspend fun refreshCategories(config: ServerConfig) {
        val categories = api.getSeriesCategories(config.username, config.password)
        categoryDao.deleteByType("series", config.id)
        categoryDao.insertAll(categories.map {
            CategoryEntity(
                categoryId = it.categoryId ?: "",
                type = "series",
                accountId = config.id,
                categoryName = it.categoryName ?: ""
            )
        })
    }

    suspend fun refreshSeries(config: ServerConfig, categoryId: String? = null) {
        val series = api.getSeries(config.username, config.password, categoryId = categoryId)
        if (categoryId == null) {
            seriesDao.deleteAllForAccount(config.id)
        }
        seriesDao.insertAll(series.map { it.toEntity(config.id) })
    }

    suspend fun getSeriesDetail(config: ServerConfig, seriesId: Int): SeriesDetail? {
        return try {
            val response = api.getSeriesInfo(config.username, config.password, seriesId = seriesId)
            val seasons = response.seasons?.map {
                Season(
                    seasonNumber = it.seasonNumber ?: 0,
                    name = it.name,
                    cover = it.coverBig ?: it.cover,
                    episodeCount = it.episodeCount
                )
            } ?: emptyList()

            val episodes = response.episodes?.mapKeys { it.key.toIntOrNull() ?: 0 }?.mapValues { (_, eps) ->
                eps.map { ep ->
                    Episode(
                        id = ep.id ?: "",
                        episodeNum = ep.episodeNum ?: 0,
                        title = ep.title ?: "",
                        containerExtension = ep.containerExtension,
                        season = ep.season ?: 0,
                        plot = ep.info?.plot,
                        image = ep.info?.movieImage,
                        duration = ep.info?.duration,
                        rating = ep.info?.rating
                    )
                }
            } ?: emptyMap()

            SeriesDetail(
                name = response.name ?: "",
                cover = response.cover,
                plot = response.plot,
                cast = response.cast,
                director = response.director,
                genre = response.genre,
                rating = response.rating,
                seasons = seasons,
                episodes = episodes
            )
        } catch (_: Exception) { null }
    }

    fun getEpisodeStreamUrl(config: ServerConfig, episodeId: String, extension: String?): String {
        val ext = extension ?: "mp4"
        return "${config.seriesBaseUrl}$episodeId.$ext"
    }

    private fun SeriesStreamDto.toEntity(accountId: Long) = SeriesEntity(
        seriesId = seriesId ?: 0,
        accountId = accountId,
        name = name ?: "",
        cover = cover,
        plot = plot,
        rating = rating,
        genre = genre,
        categoryId = categoryId,
        categoryName = categoryName,
        releaseDate = releaseDate
    )

    private fun SeriesEntity.toDomain() = Series(
        seriesId = seriesId,
        name = name,
        cover = cover,
        plot = plot,
        rating = rating,
        genre = genre,
        categoryId = categoryId,
        categoryName = categoryName,
        isFavorite = isFavorite
    )
}
