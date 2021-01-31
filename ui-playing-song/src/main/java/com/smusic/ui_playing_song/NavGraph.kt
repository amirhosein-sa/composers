package com.smusic.ui_playing_song/*
package com.smusic.ui_playing_song

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*


object MainDestinations {
    const val DEST2 = "playing"
    const val DEST1 = "songs"
    const val SONG_DETAIL_ID_KEY = "songId"
}

@Composable
fun NavGraph(startDestination: String = MainDestinations.DEST1) {
    val navController = rememberNavController()

    val actions = remember(navController) { MainActions(navController) }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.DEST1) {
            */
/*Onboarding(onboardingComplete = actions.onboardingComplete)*//*

//            AllSongsFragmentScreenContent()
              Destination1(actions.gotoSecondDestination)
        }
        composable(MainDestinations.DEST2) {
            */
/*Courses(selectCourse = actions.seletCourse)*//*

//            com.amir.ui_playing_song.PlayingSong.PlayingSongFragment()
            Destination2()
        }
        */
/*composable(
            "${MainDestinations.ALLSONGS_ROUTE}/{$}",
            arguments = listOf(navArgument(SONG_DETAIL_ID_KEY) { type = NavType.StringType })
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            PlayingSongFragmentScreenContent(arguments.getString(SONG_DETAIL_ID_KEY))
        }*//*

    }
}

@Composable
fun Destination1(action : () -> Unit) {
    Text(text = "Test", modifier = Modifier.clickable(onClick = { action() }))
}

@Composable
fun Destination2() {
    Text(text = "Test222")
}

*/
/**
 * Models the navigation actions in the app.
 *//*

class MainActions(navController: NavHostController) {
    val gotoSecondDestination: () -> Unit = {
        navController.navigate(MainDestinations.DEST2)
    }
    */
/*val selectCourse: (Long) -> Unit = { courseId: Long ->
        navController.navigate("${MainDestinations.ALLSONGS_ROUTE}/$courseId")
    }
    val upPress: () -> Unit = {
        navController.navigateUp()
    }*//*

}
*/
