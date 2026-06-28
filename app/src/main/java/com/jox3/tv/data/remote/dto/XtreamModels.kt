package com.jox3.tv.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ── Auth ──
@JsonClass(generateAdapter = true)
data class XtreamLoginResponse(
    @Json(name = "user_info") val userInfo: UserInfo? = null,
    @Json(name = "server_info") val serverInfo: ServerInfo? = null
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    @Json(name = "username") val username: String? = null,
    @Json(name = "password") val password: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "auth") val auth: Int? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "exp_date") val expDate: String? = null,
    @Json(name = "is_trial") val isTrial: String? = null,
    @Json(name = "active_cons") val activeCons: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "max_connections") val maxConnections: String? = null,
    @Json(name = "allowed_output_formats") val allowedOutputFormats: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class ServerInfo(
    @Json(name = "url") val url: String? = null,
    @Json(name = "port") val port: String? = null,
    @Json(name = "https_port") val httpsPort: String? = null,
    @Json(name = "server_protocol") val serverProtocol: String? = null,
    @Json(name = "rtmp_port") val rtmpPort: String? = null,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "timestamp_now") val timestampNow: Long? = null,
    @Json(name = "time_now") val timeNow: String? = null
)

// ── Categories ──
@JsonClass(generateAdapter = true)
data class CategoryDto(
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
    @Json(name = "parent_id") val parentId: Int? = null
)

// ── Live Streams ──
@JsonClass(generateAdapter = true)
data class LiveStreamDto(
    @Json(name = "num") val num: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "stream_type") val streamType: String? = null,
    @Json(name = "stream_id") val streamId: Int? = null,
    @Json(name = "stream_icon") val streamIcon: String? = null,
    @Json(name = "epg_channel_id") val epgChannelId: String? = null,
    @Json(name = "added") val added: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "custom_sid") val customSid: String? = null,
    @Json(name = "tv_archive") val tvArchive: Int? = null,
    @Json(name = "direct_source") val directSource: String? = null,
    @Json(name = "tv_archive_duration") val tvArchiveDuration: Int? = null
)

// ── VOD (Movies) ──
@JsonClass(generateAdapter = true)
data class VodStreamDto(
    @Json(name = "num") val num: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "stream_type") val streamType: String? = null,
    @Json(name = "stream_id") val streamId: Int? = null,
    @Json(name = "stream_icon") val streamIcon: String? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "rating_5based") val rating5based: Double? = null,
    @Json(name = "added") val added: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "container_extension") val containerExtension: String? = null,
    @Json(name = "custom_sid") val customSid: String? = null,
    @Json(name = "direct_source") val directSource: String? = null
)

@JsonClass(generateAdapter = true)
data class VodInfoResponse(
    @Json(name = "info") val info: VodInfo? = null,
    @Json(name = "movie_data") val movieData: MovieData? = null
)

@JsonClass(generateAdapter = true)
data class VodInfo(
    @Json(name = "movie_image") val movieImage: String? = null,
    @Json(name = "tmdb_id") val tmdbId: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "o_name") val oName: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "genre") val genre: String? = null,
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "cast") val cast: String? = null,
    @Json(name = "director") val director: String? = null,
    @Json(name = "releasedate") val releaseDate: String? = null,
    @Json(name = "duration") val duration: String? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "backdrop_path") val backdropPath: List<String>? = null,
    @Json(name = "youtube_trailer") val youtubeTrailer: String? = null
)

@JsonClass(generateAdapter = true)
data class MovieData(
    @Json(name = "stream_id") val streamId: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "added") val added: String? = null,
    @Json(name = "category_id") val categoryId: String? = null,
    @Json(name = "container_extension") val containerExtension: String? = null
)

// ── Series ──
@JsonClass(generateAdapter = true)
data class SeriesStreamDto(
    @Json(name = "num") val num: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "series_id") val seriesId: Int? = null,
    @Json(name = "cover") val cover: String? = null,
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "cast") val cast: String? = null,
    @Json(name = "director") val director: String? = null,
    @Json(name = "genre") val genre: String? = null,
    @Json(name = "releaseDate") val releaseDate: String? = null,
    @Json(name = "last_modified") val lastModified: String? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "rating_5based") val rating5based: Double? = null,
    @Json(name = "backdrop_path") val backdropPath: List<String>? = null,
    @Json(name = "youtube_trailer") val youtubeTrailer: String? = null,
    @Json(name = "episode_run_time") val episodeRunTime: String? = null,
    @Json(name = "category_name") val categoryName: String? = null,
    @Json(name = "category_id") val categoryId: String? = null
)

@JsonClass(generateAdapter = true)
data class SeriesInfoResponse(
    @Json(name = "name") val name: String? = null,
    @Json(name = "cover") val cover: String? = null,
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "cast") val cast: String? = null,
    @Json(name = "director") val director: String? = null,
    @Json(name = "genre") val genre: String? = null,
    @Json(name = "releaseDate") val releaseDate: String? = null,
    @Json(name = "rating") val rating: String? = null,
    @Json(name = "backdrop_path") val backdropPath: List<String>? = null,
    @Json(name = "youtube_trailer") val youtubeTrailer: String? = null,
    @Json(name = "episode_run_time") val episodeRunTime: String? = null,
    @Json(name = "seasons") val seasons: List<SeasonDto>? = null,
    @Json(name = "episodes") val episodes: Map<String, List<EpisodeDto>>? = null
)

@JsonClass(generateAdapter = true)
data class SeasonDto(
    @Json(name = "season_number") val seasonNumber: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "episode_count") val episodeCount: Int? = null,
    @Json(name = "cover") val cover: String? = null,
    @Json(name = "cover_big") val coverBig: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "air_date") val airDate: String? = null
)

@JsonClass(generateAdapter = true)
data class EpisodeDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "episode_num") val episodeNum: Int? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "container_extension") val containerExtension: String? = null,
    @Json(name = "info") val info: EpisodeInfo? = null,
    @Json(name = "custom_sid") val customSid: String? = null,
    @Json(name = "added") val added: String? = null,
    @Json(name = "season") val season: Int? = null
)

@JsonClass(generateAdapter = true)
data class EpisodeInfo(
    @Json(name = "tmdb_id") val tmdbId: Int? = null,
    @Json(name = "releasedate") val releaseDate: String? = null,
    @Json(name = "plot") val plot: String? = null,
    @Json(name = "duration_secs") val durationSecs: Int? = null,
    @Json(name = "duration") val duration: String? = null,
    @Json(name = "movie_image") val movieImage: String? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "season") val season: Int? = null
)

// ── EPG ──
@JsonClass(generateAdapter = true)
data class EpgShortResponse(
    @Json(name = "epg_listings") val epgListings: List<EpgListingDto>? = null
)

@JsonClass(generateAdapter = true)
data class EpgListingDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "epg_id") val epgId: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "lang") val lang: String? = null,
    @Json(name = "start") val start: String? = null,
    @Json(name = "end") val end: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "now_playing") val nowPlaying: Int? = null,
    @Json(name = "has_archive") val hasArchive: Int? = null
)
