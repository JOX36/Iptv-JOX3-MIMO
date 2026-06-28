package com.jox3.tv.domain.model

data class ServerConfig(
    val id: Long = 0,
    val name: String = "",
    val url: String = "",
    val port: String = "",
    val username: String = "",
    val password: String = "",
    val isActive: Boolean = false
) {
    val baseUrl: String get() {
        val cleanUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
        return "http://$cleanUrl:$port/"
    }
    val streamBaseUrl: String get() {
        val cleanUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
        return "http://$cleanUrl:$port/live/$username/$password/"
    }
    val movieBaseUrl: String get() {
        val cleanUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
        return "http://$cleanUrl:$port/movie/$username/$password/"
    }
    val seriesBaseUrl: String get() {
        val cleanUrl = url.trimEnd('/').removePrefix("http://").removePrefix("https://")
        return "http://$cleanUrl:$port/series/$username/$password/"
    }
}

data class Category(
    val id: String,
    val name: String
)

data class LiveChannel(
    val streamId: Int,
    val name: String,
    val icon: String?,
    val epgChannelId: String?,
    val categoryId: String?,
    val categoryName: String?,
    val isFavorite: Boolean = false
) {
    val streamUrl: String get() = "" // populated by repo
}

data class Movie(
    val streamId: Int,
    val name: String,
    val icon: String?,
    val rating: String?,
    val categoryId: String?,
    val categoryName: String?,
    val containerExtension: String?,
    val isFavorite: Boolean = false,
    val watchProgress: Long = 0
)

data class Series(
    val seriesId: Int,
    val name: String,
    val cover: String?,
    val plot: String?,
    val rating: String?,
    val genre: String?,
    val categoryId: String?,
    val categoryName: String?,
    val isFavorite: Boolean = false
)

data class Season(
    val seasonNumber: Int,
    val name: String?,
    val cover: String?,
    val episodeCount: Int?
)

data class Episode(
    val id: String,
    val episodeNum: Int,
    val title: String,
    val containerExtension: String?,
    val season: Int,
    val plot: String?,
    val image: String?,
    val duration: String?,
    val rating: Double?
)

data class SeriesDetail(
    val name: String,
    val cover: String?,
    val plot: String?,
    val cast: String?,
    val director: String?,
    val genre: String?,
    val rating: String?,
    val seasons: List<Season>,
    val episodes: Map<Int, List<Episode>>
)

data class MovieDetail(
    val name: String,
    val image: String?,
    val description: String?,
    val genre: String?,
    val cast: String?,
    val director: String?,
    val releaseDate: String?,
    val duration: String?,
    val rating: String?,
    val backdropPaths: List<String>
)

data class EpgProgram(
    val channelId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val category: String = ""
) {
    val isCurrentlyPlaying: Boolean get() {
        val now = System.currentTimeMillis()
        return now in startTime..endTime
    }
}
