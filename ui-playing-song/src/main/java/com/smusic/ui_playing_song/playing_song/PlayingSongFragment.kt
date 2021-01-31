package com.smusic.ui_playing_song.playing_song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animatedColor
import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.smusic.base_android.Songs
import com.smusic.base_android.rotation
import com.smusic.base_android.rotationTransitionDefinition
import com.smusic.base_android.Pager
import com.smusic.base_android.PagerState
import com.smusic.composeplayground.ui.*
import com.smusic.ui_playing_song.util.Draw
import com.smusic.ui_playing_song.R
import com.smusic.ui_playing_song.playing_song.SongBottomSheetState.Closed
import com.smusic.ui_playing_song.playing_song.SongBottomSheetState.Open
import com.chibde.visualizer.LineBarVisualizer
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import me.tankery.lib.circularseekbar.CircularSeekBar
import kotlin.random.Random

const val TAG = "com.amir.ui_playing_song.PlayingSong.PlayingSongFragment"


// TODO: 1/14/21 Stop rotation animation based on songState and Pager's selected page

lateinit var song: Songs

var songCoverRotationRadius = 0f
var tempIsPlaying = MutableLiveData(false)

class PlayingSongFragment : Fragment() {

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // getting arguments from deep link
        val args = requireArguments()
        val title = args.getString("songTitle")!!
        val id = args.getLong("songID")
        val artist = args.getString("artist")!!
        val songData = args.getString("songData")!!
        val dateAdded = args.getLong("dateAdded")
        val songUri = args.getString("songUri")!!


        song = Songs(songID = id, songTitle = title, artist = artist, songData = songData,
            dateAdded = dateAdded, uri = songUri.toUri())

        return ComposeView(requireContext()).apply {
            setContent {
                val sheetState = rememberSwipeableState(initialValue = Closed)
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels / displayMetrics.density
                ComposePlaygroundTheme {
                    ProvideWindowInsets {
                        Surface(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .swipeable(
                                state = sheetState,
                                anchors = mapOf(
                                    0f to Closed,
                                    screenHeight to Open
                                ),
                                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                                orientation = Orientation.Vertical,
                            ), color = white100) {
                            PlayingSongFragmentScreenContent(requireActivity(), sheetState)
                        }
                    }
                }

            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun PlayingSongFragmentScreenContent(
    activity: FragmentActivity,
    sheetState: SwipeableState<SongBottomSheetState>,
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(white100),
        contentAlignment = Alignment.BottomEnd) {

        ScrollableColumn(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
            verticalArrangement = Arrangement.SpaceEvenly) {
            HeaderComponent(activity = activity)
            SongCoverComponent(song = song)
            SongTitleComponent(song = song)
            SingerNameComponent(song = song)
            SongDetailsComponent()
            SongDurationsComponent()
            SongControllersComponent()
        }
        PlayListFabComponent(sheetState = sheetState)
    }
}

@Composable
fun HeaderComponent(activity: FragmentActivity) {
    val isFav = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = {
                            activity.onBackPressed()
                        })
            )
            Text(text = "Now Playing")
        }
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = {
                    isFav.value = !isFav.value
                }),
            color = Color.White
        ) {
            Icon(
                imageVector = if (isFav.value) Icons.Rounded.FavoriteBorder else Icons.Rounded.Favorite,
                modifier = Modifier.padding(8.dp),
                tint = Color.Red
            )
        }
    }
}

@Composable
fun BottomNextSongViewComponent() {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.KeyboardArrowUp, modifier = Modifier.alpha(reverseAlphaAnimator()))
        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(text = "Next song :")
        }
        Spacer(modifier = Modifier.width(4.dp))
        Providers(AmbientContentAlpha provides ContentAlpha.disabled
        ) {
            Text(text = "Believer | Imagine Dragons", maxLines = 1, softWrap = true)
        }
        Icon(Icons.Default.KeyboardArrowUp, modifier = Modifier.alpha(reverseAlphaAnimator()))
    }

}


@Composable
fun reverseAlphaAnimator(): Float {
    val animatedAlpha = animatedFloat(0f)
    onActive {
        animatedAlpha.animateTo(
            targetValue = 1f,
            anim = infiniteRepeatable(
                repeatMode = RepeatMode.Reverse,
                animation =
                tween(durationMillis = 2000, easing = LinearEasing),
            )
        )
    }
    return animatedAlpha.value
}


@Composable
fun songCoverAnimator(): Float {
    val radius = animatedFloat(0f)
    if (playing){
        radius.animateTo(
            targetValue = 360f,
            anim = infiniteRepeatable(
                animation =
                tween(durationMillis = 10000, easing = LinearEasing),
            )
        )

    }else {
        radius.stop()
    }
    return radius.value
}

@Composable
fun SongCoverComponent(song: Songs) {

    val state = transition(
        definition = rotationTransitionDefinition,
        initState = "A",
        toState = "B"
    )

    val radius = animatedFloat(initVal = 0f)

    val pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) {
            PagerState(clock, 0, 0, 50)
        }
    }


//    var glideBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val metrics = AmbientContext.current.resources.displayMetrics
    val mWidth = metrics.widthPixels / metrics.density
    Spacer(modifier = Modifier.height(2.dp))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.shadow(elevation = 150.dp)
    ) {
        val bitmap = loadImageResource(id = R.drawable.cover).resource.resource
        /*Glide.with(AmbientContext.current).asBitmap()
            .load(song.uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?,
                ) {
                    glideBitmap = resource
                }
            })
        glideBitmap?.let {
        }*/

        CircularSeekbarComponent(mWidth / 1.25)
        Pager(state = pagerState, modifier = Modifier
            .fillMaxWidth()
            .height((mWidth / 1.5).dp)) {
            val isSelected = currentPage == page
            SongCoverItemComponent(isSelected = isSelected,
                rotateRadius = state[rotation],
                bitmap = bitmap, width = mWidth)
        }

    }
}

@Composable
fun SongCoverItemComponent(
    isSelected: Boolean,
    rotateRadius: Float,
    bitmap: ImageBitmap?,
    width: Float,
) {
    val animateSize =
        androidx.compose.animation.animate(if (isSelected) (width / 1.5).dp else (width / 2).dp)
    val animateElevation = androidx.compose.animation.animate(if (isSelected) 36.dp else 0.dp)

    Card(Modifier
        .padding(start = 36.dp, end = 36.dp)
        .wrapContentSize()
        .clip(CircleShape),
        elevation = animateElevation) {
        Box(modifier = Modifier
            .preferredSize(animateSize)
            .clip(CircleShape)
            .rotate(songCoverAnimator())
            , contentAlignment = Alignment.Center) {

            bitmap?.let {
                Image(it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop)
            }
            Draw(CircleShape, Color.Transparent.copy(alpha = 0.5f), (width / 6).dp)
        }
    }

}

@Composable
fun SongDurationsComponent() {
    val timePassed = "01:50"
    val songDuration = "02:48"
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(text = timePassed,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = TextUnit.Sp(12)))
            Text(text = songDuration,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = TextUnit.Sp(12)))
        }
    }
}

@Composable
fun CircularSeekbarComponent(mSize: Double) {
    AndroidView(modifier = Modifier.preferredSize(mSize.dp), viewBlock = { context ->
        CircularSeekBar(context).apply {
            circleProgressColor = android.graphics.Color.parseColor("#FF3700B2")
            pointerColor = android.graphics.Color.parseColor("#FF3700B2")
            circleColor = android.graphics.Color.parseColor("#00000000")
            pointerStrokeWidth = 50f
            circleStrokeWidth = 15f
            pointerAlpha = 1
        }
    })
}


@Composable
fun SongTitleComponent(song: Songs) {
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = song.songTitle,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = TextStyle(
            shadow = Shadow(color = Color.Gray,
                blurRadius = 15f,
                offset = Offset(x = 5f, y = 5f)),
            color = titlesColor,
            fontWeight = FontWeight.Bold,
            fontSize = TextUnit.Sp(20),
        )
    )
}

@Composable
fun SingerNameComponent(song: Songs) {
    Spacer(modifier = Modifier.height(4.dp))
    Providers(AmbientContentAlpha provides ContentAlpha.high) {
        Text(
            text = song.artist,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            softWrap = true,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = TextStyle(
                fontWeight = FontWeight.Thin,
                fontSize = TextUnit.Sp(12),
            )
        )
    }
}

@Composable
fun SongDetailsComponent() {
    val songDuration = 55 // FIXME: 12/26/20 Use Dynamic values here

    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = { /*TODO*/ }),
            color = white200
        ) {
            Icon(Icons.Rounded.VolumeDown, modifier = Modifier.padding(12.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        MusicVisualizerComponent()
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = { /*TODO*/ }),
            color = white200
        ) {
            Icon(Icons.Rounded.VolumeUp, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
fun MusicVisualizerComponent() {

    val metrics = AmbientContext.current.resources.displayMetrics
    val mWidth = metrics.widthPixels / metrics.density

    AndroidView(modifier = Modifier
        .height(50.dp)
        .preferredWidth((mWidth * 0.25).dp),
        viewBlock = {
            LineBarVisualizer(it).apply {
                setColor(android.graphics.Color.parseColor("#FF3700B2"))
//            setPlayer(mp.audioSessionId)
            }
        })

}


@Composable
fun SongControllersComponent() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MusicControlButton(icon = Icons.Rounded.Shuffle, onClick = { /*TODO*/ })
        MusicControlButton(icon = Icons.Rounded.SkipPrevious, onClick = { /*TODO*/ })
        MusicControlButton(hasForeground = true,
            icon = Icons.Rounded.RotateLeft,
            onClick = { /*TODO*/ })
        PlayPauseButton()
        MusicControlButton(hasForeground = true,
            icon = Icons.Rounded.RotateRight,
            onClick = { /*TODO*/ })
        MusicControlButton(icon = Icons.Rounded.SkipNext, onClick = { /*TODO*/ })
        // fixme : remove this button :|
        MusicControlButton(icon = Icons.Rounded.VolumeDown, onClick = { /*TODO*/ })
    }
}

var playing by mutableStateOf(true)


@Composable
fun PlayPauseButton() {
//    val isPlaying = remember { playing }
    val playPauseIcon =
        if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
    Surface(
        modifier = Modifier
            .padding(all = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = {
                playing = !playing
            }),
        color = purple700
    ) {
        Icon(
            imageVector = playPauseIcon,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp)
        )
    }
}

@Composable
fun MusicControlButton(
    hasForeground: Boolean = false,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val foregroundColor = if (hasForeground) purple700 else Color.Black
    IconButton(onClick = onClick,
        modifier = Modifier
            .clickable(onClick = { })
            .preferredSize(36.dp)) {
        Icon(imageVector = icon, tint = foregroundColor)
    }
}


@Composable
fun DrawLine() {
    val onSurfaceColor = purple700
    val rand = Random.nextDouble(10.0, 100.0)
    val offset = 100f - rand
    val startYOffset = offset / 2
    Canvas(modifier = Modifier
        .padding(top = (rand % 3).dp, bottom = (rand % 2).dp)
        .preferredHeight(50.dp)
        .preferredWidth(1.dp)) {
        drawLine(
            color = onSurfaceColor,
            start = Offset(0f, startYOffset.toFloat()),
            end = Offset(0f, 100f - (startYOffset.toFloat())),
            strokeWidth = size.width * 2f
        )
    }
}


@ExperimentalMaterialApi
@Composable
fun PlayListFabComponent(sheetState: SwipeableState<SongBottomSheetState>) {
    val fabSize = animatedFloat(initVal = 48f)
    val fabColor = animatedColor(purple700)
    val playListIconAlpha = animatedFloat(initVal = 1f)
    val columnAlpha = animatedFloat(initVal = 0f)
    val metrics = AmbientContext.current.resources.displayMetrics
    val height = metrics.heightPixels / metrics.density

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd
    ) {
        Canvas(modifier = Modifier
            .width(fabSize.value.dp)
            .navigationBarsHeight(additional = fabSize.value.dp + 16.dp)
            .clip(
                RoundedCornerShape(topLeft = 24.dp))
            .clickable(
                onClick = {
                    openSongsBottomSheetContentsAnimations(fabSize = fabSize,
                        height = height,
                        playListIconAlpha = playListIconAlpha,
                        columnAlpha = columnAlpha,
                        fabColor = fabColor)

                }))
        {
            drawRoundRect(
                color = fabColor.value,
            )
        }
        Icon(Icons.Rounded.PlaylistPlay,
            modifier = Modifier
                .alpha(playListIconAlpha.value)
                .padding(horizontal = 12.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            tint = Color.White)
    }

}

private fun openSongsBottomSheetContentsAnimations(
    fabSize: AnimatedFloat,
    height: Float,
    playListIconAlpha: AnimatedFloat,
    columnAlpha: AnimatedFloat,
    fabColor: AnimatedValue<Color, AnimationVector4D>,
) {
    fabSize.animateTo(
        targetValue = height,
        anim =
        tween(durationMillis = 600, easing = FastOutSlowInEasing),
    )
    playListIconAlpha.animateTo(targetValue = 0f,
        anim = tween(600, easing = FastOutSlowInEasing))
    columnAlpha.animateTo(targetValue = 1f,
        anim = tween(600, easing = FastOutSlowInEasing))
    fabColor.animateTo(targetValue = white100,
        anim = tween(1000, easing = FastOutSlowInEasing))
}

private fun closeSongsBottomSheetContentsAnimations(
    fabSize: AnimatedFloat,
    playListIconAlpha: AnimatedFloat,
    columnAlpha: AnimatedFloat,
    fabColor: AnimatedValue<Color, AnimationVector4D>,
) {
    fabSize.animateTo(
        targetValue = 48f,
        anim =
        tween(durationMillis = 600, easing = FastOutSlowInEasing),
    )
    playListIconAlpha.animateTo(targetValue = 1f,
        anim = tween(600, easing = FastOutSlowInEasing))
    columnAlpha.animateTo(targetValue = 0f,
        anim = tween(600, easing = FastOutSlowInEasing))
    fabColor.animateTo(targetValue = purple700,
        anim = tween(1000, easing = FastOutSlowInEasing))
}

enum class SongBottomSheetState {
    Open,
    Closed
}


@Preview
@Composable
fun NextSongViewPreview() {
    BottomNextSongViewComponent()
}

@Preview
@Composable
fun SongDetailsPreview() {
    SongDetailsComponent()
}



