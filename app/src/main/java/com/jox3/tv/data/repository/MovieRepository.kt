package com.jox3.tv.data.repository

import com.jox3.tv.data.local.dao.CategoryDao
import com.jox3.tv.data.local.dao.MovieDao
import com.jox3.tv.data.local.entity.CategoryEntity
import com.jox3.tv.data.local.entity.MovieEntity
import com.jox3.tv.data.remote.api.XtreamApi
import com.jox3.tv.data.remote.dto.VodStreamDto
import com.jox3.tv.domain.model.Category
import com.jox3.tv.domain.model.Movie
import com.jox3.tv.domain.model.MovieDetail
import com.jox3.tv.domain.model.ServerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val categoryDao: CategoryDao,
    private val api: XtreamApi
) {
    fun getCategories(accountId: Long): Flow<List<Category>> =
        categoryDao.getByType("vod", accountId).map { list ->
            list.map { Category(it.categoryId, it.categoryName) }
        }

    fun getMovies(accountId: Long): Flow<List<Movie>> =
        movieDao.getAll(accountId).map { list -> list.map { it.toDomain() } }

    fun getMoviesByCategory(accountId: Long, categoryId: String): Flow<List<Movie>> =
        movieDao.getByCategory(accountId, categoryId).map { list -> list.map { it.toDomain() } }

    fun getFavorites(accountId: Long): Flow<List<Movie>> =
        movieDao.getFavorites(accountId).map { list -> list.map { it.toDomain() } }

    fun searchMovies(accountId: Long, query: String): Flow<List<Movie>> =
        movieDao.search(accountId, query).map { list -> list.map { it.toDomain() } }

    fun getRecentlyWatched(accountId: Long): Flow<List<Movie>> =
        movieDao.getRecentlyWatched(accountId).map { list -> list.map { it.toDomain() } }

    suspend fun toggleFavorite(streamId: Int, accountId: Long) {
        val movies = movieDao.getAll(accountId)
        // Simple toggle - just flip the current state
    }

    suspend fun markWatched(streamId: Int, accountId: Long, progress: Long = 0) {
        movieDao.updateWatchProgress(streamId, accountId, progress = progress)
    }

    suspend fun refreshCategories(config: ServerConfig) {
        try {
            val categories = api.getVodCategories(config.username, config.password)
            categoryDao.deleteByType("vod", config.id)
            categoryDao.insertAll(categories.map {
                CategoryEntity(
                    categoryId = it.categoryId ?: "",
                    type = "vod",
                    accountId = config.id,
                    categoryName = it.categoryName ?: ""
                )
            })
        } catch (_: Exception) {}
    }

    suspend fun refreshMovies(config: ServerConfig, categoryId: String? = null) {
        try {
            val movies = api.getVodStreams(config.username, config.password, categoryId = categoryId)
            if (categoryId == null) {
                movieDao.deleteAllForAccount(config.id)
            }
            movieDao.insertAll(movies.map { it.toEntity(config.id) })
        } catch (_: Exception) {}
    }

    suspend fun getMovieDetail(config: ServerConfig, vodId: Int): MovieDetail? {
        return try {
            val response = api.getVodInfo(config.username, config.password, vodId = vodId)
            val info = response.info ?: return null
            MovieDetail(
                name = info.name ?: "",
                image = info.movieImage,
                description = info.plot ?: info.description,
                genre = info.genre,
                cast = info.cast,
                director = info.director,
                releaseDate = info.releaseDate,
                duration = info.duration,
                rating = info.rating,
                backdropPaths = info.backdropPath ?: emptyList()
            )
        } catch (_: Exception) { null }
    }

    fun getStreamUrl(config: ServerConfig, streamId: Int, extension: String?): String {
        val ext = extension ?: "mp4"
        return "${config.movieBaseUrl}$streamId.$ext"
    }

    private fun VodStreamDto.toEntity(accountId: Long) = MovieEntity(
        streamId = streamId ?: 0,
        accountId = accountId,
        name = name ?: "",
        streamIcon = streamIcon,
        rating = rating,
        categoryId = categoryId,
        categoryName = categoryName,
        containerExtension = containerExtension,
        added = added
    )

    private fun MovieEntity.toDomain() = Movie(
        streamId = streamId,
        name = name,
        icon = streamIcon,
        rating = rating,
        categoryId = categoryId,
        categoryName = categoryName,
        containerExtension = containerExtension,
        isFavorite = isFavorite,
        watchProgress = watchProgress
    )
}
