package com.amir.composeplayground.ui.util

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.amir.composeplayground.ui.util.ButtonsToggleAnimation.ButtonsState.CLICKED
import com.amir.composeplayground.ui.util.ButtonsToggleAnimation.ButtonsState.IDLE
import com.amir.composeplayground.ui.util.ButtonsToggleAnimation.buttonsTransitionDefinition
import com.amir.composeplayground.ui.util.ButtonsToggleAnimation.iconSize

@Preview
@Composable
fun AnimatedToggleButtonPreview(){
    val state = remember { mutableStateOf(IDLE) }
    AnimatedToggleButton(modifier = Modifier, icon = Icons.Rounded.Favorite, buttonState = state, onClick = {
        state.value = if (state.value == IDLE) CLICKED else IDLE
    })
}

@Composable
fun AnimatedToggleButton(
    modifier: Modifier,
    icon: ImageVector,
    buttonState: MutableState<ButtonsToggleAnimation.ButtonsState>,
    onClick: () -> Unit,
    ) {
    val toState =
        if (buttonState.value == IDLE) CLICKED else IDLE
    val state = transition(definition = buttonsTransitionDefinition,
        initState = buttonState.value,
        toState = toState)
    ToggleButton(modifier = modifier,
        icon = icon,
        buttonState = buttonState,
        state = state,
        onClick = onClick)

}


@Composable
private fun ToggleButton(
    modifier: Modifier,
    icon: ImageVector,
    buttonState: MutableState<ButtonsToggleAnimation.ButtonsState>,
    state: TransitionState,
    onClick: () -> Unit,
) {
        Icon(icon, modifier = modifier
            .clickable(onClick = onClick)
            .width(state[iconSize])
            .height(state[iconSize]))


}

object ButtonsToggleAnimation {
    enum class ButtonsState {
        IDLE, CLICKED
    }

    val idleSize = 48.dp
    val clickedSize = 36.dp
    val iconSize = DpPropKey("idleIconSizeKey")

    val buttonsTransitionDefinition = transitionDefinition<ButtonsState> {
        state(IDLE) {
            this[iconSize] = idleSize
        }
        state(CLICKED) {
            this[iconSize] = clickedSize
        }
        transition(IDLE to CLICKED) {
            iconSize using keyframes {
                durationMillis = 300
                clickedSize at 100
                idleSize at 200
            }
        }

        transition(CLICKED to IDLE) {
            iconSize using keyframes {
                durationMillis = 300
                clickedSize at 100
                idleSize at 200
            }
        }

    }
}