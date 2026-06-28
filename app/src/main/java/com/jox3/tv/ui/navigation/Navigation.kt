package com.jox3.tv.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jox3.tv.ui.epg.EpgScreen
import com.jox3.tv.ui.home.HomeScreen
import com.jox3.tv.ui.live.LiveScreen
import com.jox3.tv.ui.movies.MovieDetailScreen
import com.jox3.tv.ui.movies.MoviesScreen
import com.jox3.tv.ui.player.PlayerScreen
import com.jox3.tv.ui.series.SeriesDetailScreen
import com.jox3.tv.ui.series.SeriesScreen
import com.jox3.tv.ui.settings.LoginScreen
import com.jox3.tv.ui.settings.SettingsScreen
import com.jox3.tv.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    data object Home : Screen("home", "Inicio", Icons.Outlined.Home, Icons.Filled.Home)
    data object Live : Screen("live", "TV", Icons.Outlined.LiveTv, Icons.Filled.LiveTv)
    data object Movies : Screen("movies", "Películas", Icons.Outlined.Movie, Icons.Filled.Movie)
    data object Series : Screen("series", "Series", Icons.Outlined.Tv, Icons.Filled.Tv)
    data object Settings : Screen("settings", "Ajustes", Icons.Outlined.Settings, Icons.Filled.Settings)
}

val bottomNavItems = listOf(Screen.Home, Screen.Live, Screen.Movies, Screen.Series, Screen.Settings)

@Composable
fun Jox3NavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    contentColor = TextPrimary,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanAccent,
                                unselectedIconColor = TextTertiary,
                                unselectedTextColor = TextTertiary,
                                indicatorColor = CyanAccent.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Live.route) {
                LiveScreen(navController = navController)
            }
            composable(Screen.Movies.route) {
                MoviesScreen(navController = navController)
            }
            composable(Screen.Series.route) {
                SeriesScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }

            // Login
            composable("login") {
                LoginScreen(navController = navController)
            }

            // Player
            composable(
                route = "player?url={url}&title={title}",
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                PlayerScreen(streamUrl = url, title = title, navController = navController)
            }

            // Movie Detail
            composable(
                route = "movie_detail/{vodId}",
                arguments = listOf(navArgument("vodId") { type = NavType.IntType })
            ) {
                MovieDetailScreen(navController = navController)
            }

            // Series Detail
            composable(
                route = "series_detail/{seriesId}",
                arguments = listOf(navArgument("seriesId") { type = NavType.IntType })
            ) {
                SeriesDetailScreen(navController = navController)
            }

            // EPG
            composable("epg") {
                EpgScreen(navController = navController)
            }

            // EPG for specific channel
            composable(
                route = "epg/{channelId}",
                arguments = listOf(navArgument("channelId") { type = NavType.StringType })
            ) {
                EpgScreen(navController = navController)
            }
        }
    }
}
