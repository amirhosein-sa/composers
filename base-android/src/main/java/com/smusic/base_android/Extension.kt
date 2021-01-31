package com.smusic.base_android

import androidx.annotation.AnimRes
import androidx.compose.animation.core.*
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

fun DefaultNavOptions(
    @AnimRes enterAnim: Int = R.anim.nav_default_enter_anim,
    @AnimRes exitAnim: Int = R.anim.nav_default_exit_anim,
    @AnimRes popEnterAnim: Int = 0,
    @AnimRes popExitAnim: Int = R.anim.nav_default_pop_exit_anim,
): NavOptions {
    return navOptions {
        anim {
            enter = enterAnim
            exit = exitAnim
            popExit = popExitAnim
            popEnter = popEnterAnim
        }
    }
}


val rotation = FloatPropKey("rotation animation")
val rotationTransitionDefinition = transitionDefinition<String> {


    state("A") { this[rotation] = 0f }
    state("B") { this[rotation] = 360f }

    transition("A" to "B") {
        rotation using infiniteRepeatable(              
            animation = tween(
                durationMillis = 15000,
                easing = LinearEasing
            )
        )
    }
}
