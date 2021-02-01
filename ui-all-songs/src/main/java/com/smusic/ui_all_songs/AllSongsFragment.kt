package com.smusic.ui_all_songs

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.compose.animation.animatedFloat
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.smusic.base_android.DefaultNavOptions
import com.smusic.base_android.Songs
import com.smusic.composeplayground.ui.ComposePlaygroundTheme
import com.smusic.composeplayground.ui.purple700
import com.smusic.composeplayground.ui.white100
import com.smusic.ui_all_songs.BottomBarTabs.*
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding

private val TitleHeight = 50.dp
private val categoriesOffset = 86.dp

class AllSongsFragment : Fragment() {
    @ExperimentalMaterialApi
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val mockData = mockGenerator()
        return ComposeView(requireContext()).apply {
            setContent {
                ComposePlaygroundTheme {
                    ProvideWindowInsets {
                        Scaffold(modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .background(white100),
                            topBar = { AppBarAllSongsComponent() },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = {},
                                ) {

                                    Icon(imageVector = Icons.Rounded.PlayCircleOutline)
                                }
                            },
                            floatingActionButtonPosition = FabPosition.Center,
                            isFloatingActionButtonDocked = true,
                            bottomBar = { AllSongsBottomBarComponent() }) {
                            /* fixme: Handle fetching songs and granting permissions in splash screen */
                            askForPermissions()
                            AllSongsColumnComponent(items = mockData)
                        }
                    }
                }
            }
        }
    }

    private fun hasPermission(permission: String?): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            permission!!) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions() {
        val REQUEST_CODE = 13
        val recordAudio: String = Manifest.permission.RECORD_AUDIO
        val modifyAudioSetting: String = Manifest.permission.MODIFY_AUDIO_SETTINGS
        val externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionList: MutableList<String> = ArrayList()
        if (!hasPermission(recordAudio)) {
            permissionList.add(recordAudio)
        }
        if (!hasPermission(modifyAudioSetting)) {
            permissionList.add(modifyAudioSetting)
        }
        if (!hasPermission(externalStoragePermission)) {
            permissionList.add(externalStoragePermission)
        }
        if (permissionList.isNotEmpty()) {
            val permissions = permissionList.toTypedArray()
            ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_CODE)
        }
    }

    private fun getSongs(): List<Songs> {
        val songList = ArrayList<Songs>()
        val contentResolver = activity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val songCursor = contentResolver?.query(
            songUri,
            null,
            selection,
            null,
            null
        )
        if (songCursor != null && songCursor.moveToFirst()) {
            val sArtWorkUri = Uri.parse("content://media/external/audio/albumart")
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            val albumColumn = songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            while (songCursor.moveToNext()) {
                val currentId = songCursor.getLong(songId)
                val currentTitle = songCursor.getString(songTitle)
                val currentArtist = songCursor.getString(songArtist)
                val currentData = songCursor.getString(songData)
                val currentDate = songCursor.getLong(dateIndex)
                val songAlbum = songCursor.getLong(albumColumn)
                val uri = ContentUris.withAppendedId(sArtWorkUri, songAlbum)
                songList.add(
                    Songs(
                        currentId,
                        currentTitle,
                        currentArtist,
                        currentData,
                        currentDate,
                        uri
                    )
                )
            }
        }
        songCursor?.close()
        return songList
    }


    @Composable
    fun AllSongsColumnComponent(items: List<Songs>) {
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Wow, Such Empty :]")
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                val scroll = rememberScrollState(0f)
                Body(scroll, items)
                Header(scroll.value)
            }
            /*LazyColumn(content = {
                items(items) { song ->
                    AllSongsItemComponent(song)
                }
            })*/
        }
    }

    @Composable
    fun Body(
        scroll: ScrollState,
        items: List<Songs>,
    ) {
        val navOption = DefaultNavOptions(
            // FIXME: 1/25/21 Create custom animations for navigate between composables
            /*enterAnim = R.anim.from_end,
            popExitAnim = R.anim.to_end,
            popEnterAnim = R.anim.from_end,
            exitAnim = R.anim.to_end*/
        )

        ScrollableColumn(scrollState = scroll) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(categoriesOffset),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Category(icon = Icons.Rounded.Apps, scroll) {
                    val uri = Uri.parse("smusic://AlbumsFragment/")
                    findNavController().navigate(uri, navOption)
                }
                Category(icon = Icons.Rounded.Favorite, scroll) {
                    val uri = Uri.parse("smusic://FavoriteSongs/")
                    findNavController().navigate(uri, navOption)
                }

                Category(icon = Icons.Rounded.PlaylistPlay, scroll) {
                    val uri = Uri.parse("smusic://AllSongsPlaylists/")
                    findNavController().navigate(uri, navOption)
                }
            }
            Spacer(Modifier.preferredHeight(56.dp))
            items.forEach {
                AllSongsItemComponent(song = it)
            }
        }
    }

    @Composable
    fun AllSongsItemComponent(song: Songs) {

        val bitmap = loadImageResource(id = R.drawable.cover).resource.resource
        var glideBitmap by remember { mutableStateOf<Bitmap?>(null) }
        val navOption =
            DefaultNavOptions(enterAnim = R.anim.from_botom, popExitAnim = R.anim.to_bottom)

        Glide.with(AmbientContext.current).asBitmap()
            .load(if (Uri.EMPTY != song.uri) song.uri else bitmap)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?,
                ) {
                    glideBitmap = resource
                }
            })

        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                val uri =
                    Uri.parse("smusic://PlayingSong/${song.songID}/${song.songTitle}/${song.artist}/${song.songData}/${song.dateAdded}/${song.uri.toString()}")
                findNavController().navigate(uri, navOption)
            })
            .padding(4.dp), horizontalArrangement = Arrangement.Start) {
            glideBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .preferredSize(48.dp)
                        .clip(
                            RoundedCornerShape(4.dp))
                )


                Column(modifier = Modifier.preferredHeight(48.dp),
                    horizontalAlignment = Alignment.Start) {
                    Text(text = song.songTitle,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .layoutId("songTitle")
                            .padding(4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = TextUnit.Sp(15),
                        softWrap = true)
                    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                        Text(text = song.artist,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .layoutId("singerName")
                                .padding(4.dp),
                            maxLines = 1,
                            style = TextStyle(fontSize = TextUnit.Sp(12)),
                            softWrap = true)
                    }
                }
            }
            Divider(modifier = Modifier
                .padding(start = 16.dp, end = 16.dp))
        }

    }


    @Composable
    fun Header(scroll: Float) {
        val maxOffset = with(AmbientDensity.current) { categoriesOffset.toPx() }
        val minOffset = with(AmbientDensity.current) { 0.5.dp.toPx() }
        val offset = (maxOffset - scroll).coerceAtLeast(minOffset)
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .preferredHeightIn(min = 56.dp)
                .graphicsLayer { translationY = offset }
                .fillMaxWidth()
                .background(white100)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = "Local Songs",
                style = MaterialTheme.typography.h4)

            Row(modifier = Modifier
                .padding(end = 16.dp, bottom = 8.dp)) {
                val hotVector = loadVectorResource(id = R.drawable.hot_songs_16).resource.resource
                val shuffleVector = loadVectorResource(id = R.drawable.shuffle_16).resource.resource
                val sortVector = loadVectorResource(id = R.drawable.round_sort_16).resource.resource
                shuffleVector?.let {
                    Surface(modifier = Modifier
                        .clickable(
                            onClick = { /*TODO*/ })) {
                        Icon(imageVector = it, modifier = Modifier
                            .padding(8.dp))
                    }
                }
                hotVector?.let {
                    Surface(modifier = Modifier
                        .clickable(
                            onClick = { /*TODO*/ })
                    ) {
                        Icon(imageVector = it, modifier = Modifier
                            .padding(8.dp))
                    }

                }
                sortVector?.let {
                    Surface(modifier = Modifier
                        .clickable(
                            onClick = { /*TODO*/ })) {
                        Icon(imageVector = it, modifier = Modifier
                            .padding(8.dp))
                    }
                }
            }
        }
    }

    private fun mockGenerator(): List<Songs> {
        val l = mutableListOf<Songs>()
        for (i in 0..50)
            l.add(Songs(songID = i.toLong(),
                songTitle = "Title $i",
                artist = "Artist $i",
                songData = "Data $i",
                dateAdded = i.toLong(),
                uri = "dujfhsdajgf".toUri()))
        return l
    }


    @Preview
    @Composable
    fun AllSongsBottomBarComponent() {
        val tabs = values()
        val (selectedTab, setSelectedTab) = remember { mutableStateOf(AllSongs) }
        BottomAppBar(cutoutShape = CircleShape,
            modifier = Modifier
                .navigationBarsHeight(additional = 56.dp)
                .clip(RoundedCornerShape(topLeft = 16.dp, topRight = 16.dp))) {
            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp),
                constraintSet = ConstraintSet {
                    val myMusic = createRefFor("myMusic")
                    val explore = createRefFor("explore")
                    constrain(myMusic) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    constrain(explore) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                }
            ) {
                BottomNavigationItem(
                    modifier = Modifier
                        .layoutId("myMusic")
                        .navigationBarsPadding(),
                    icon = {
                        Icon(AllSongs.icon)
                    },
                    label = { Text(AllSongs.title) },
                    alwaysShowLabels = false,
                    selectedContentColor = Color.White,
                    unselectedContentColor = androidx.compose.material.AmbientContentColor.current,
                    selected = AllSongs == selectedTab,
                    onClick = { setSelectedTab(AllSongs) })

                BottomNavigationItem(
                    modifier = Modifier
                        .layoutId("explore")
                        .navigationBarsPadding(),
                    icon = {
                        Icon(Explore.icon)
                    },
                    label = { Text(Explore.title) },
                    alwaysShowLabels = false,
                    selectedContentColor = Color.White,
                    unselectedContentColor = androidx.compose.material.AmbientContentColor.current,
                    selected = Explore == selectedTab,
                    onClick = { setSelectedTab(Explore) })
            }
        }
    }
}


fun onQueryTextChange(query: String?): Boolean {
    // Here is where we are going to implement the filter logic
    return false
}

fun onQueryTextSubmit(query: String?): Boolean {
    return false
}


@Composable
fun SearchFieldComponent() {
//    val textController = remember { mutableStateOf(TextFieldValue()) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        AndroidView(modifier = Modifier.weight(1f), viewBlock = {
            EditText(it).apply {
                hint = "Search Library"
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            }
        })
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = {
                    }),
                color = Color.White
            ) {
                Icon(Icons.Default.Search,
                    modifier = Modifier.padding(8.dp))
            }

        }

    }
}

@Preview
@Composable
fun AppBarAllSongsComponent() {
//    val textController = remember { mutableStateOf(TextFieldValue()) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = {
                }),
            color = Color.White
        ) {
            Icon(Icons.Default.Settings,
                modifier = Modifier.padding(8.dp))
        }
        SearchFieldComponent()

    }
}


@Composable
fun Category(icon: ImageVector, state: ScrollState, onClick: () -> Unit = {}) {

    val fabHeight = animatedFloat(initVal = 80f)
    val fabWidth = animatedFloat(initVal = 80f)
    val contentAlpha = animatedFloat(initVal = 1f)
    val maxElevation = 45.dp
    val minElevation = 2.dp
    val elevation = (maxElevation - state.value.dp).coerceAtLeast(minElevation)

    val metrics = AmbientContext.current.resources.displayMetrics
    val Height = metrics.heightPixels / metrics.density
    val scrWidth = metrics.widthPixels / metrics.density

    Card(
        modifier = Modifier
            .preferredSize(80.dp)
            .padding(8.dp)
            .shadow(elevation = 25.dp, shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentColor = purple700,
        elevation = elevation,
    ) {
        Box {
            Icon(imageVector = icon, modifier = Modifier.align(Alignment.Center))
        }

    }

}


private enum class BottomBarTabs(
    val title: String,
    val icon: ImageVector,
) {
    AllSongs("My Music", Icons.Rounded.MusicNote),
    Explore("Explore", Icons.Rounded.Explore)
}


