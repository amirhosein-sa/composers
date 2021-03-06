package com.smusic.composeplayground

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.smusic.composeplayground.databinding.ActivityMainBinding
import com.smusic.composeplayground.ui.titlesColor
import androidx.compose.material.Icon as materialIcon

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.myView)*/
        setContent {

// Here the result will be a 20.dp x 50.dp blue box centered vertically in a 50.dp x 50.dp
// space. Because of the preferredSize modifier, if wrapContentWidth did not exist,
// the blue rectangle would actually be 50.dp x 50.dp to satisfy the size set by the modifier.
// However, because we provide wrapContentWidth, the blue rectangle is specified to be wrap
// content in width - if the desired width is smaller than 50.dp, it will be centered
// horizontally in this space. Therefore the 50.dp x 20.dp is centered horizontally
// in the space.
            Column(
                Modifier.size(50.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .width(450.dp)
                    .background(Color.Red)
            ){
                Text(text = "Test",modifier = Modifier.width(300.dp).background(Color.Gray))
                Text(text = "Test",modifier = Modifier.width(50.dp) .background(Color.Blue))
            }

        }

    }


}
/*
@Composable
fun Item(isSelected: Boolean) {
    val context = LocalContext.current.resources
    val displayMetrics = context.displayMetrics
    val scrWidth = displayMetrics.widthPixels / displayMetrics.density
    val bitmap = painterResource(id = R.drawable.cover).resource.resource
//    val animateHeight = animateDpAsState(if (isSelected) 645.dp else 360.dp, remember {
//        spring(visibilityThreshold = Dp.VisibilityThreshold)
//    }).value
    val animateWidth = animateDpAsState(if (isSelected) 340.dp else 320.dp, remember {
        spring(visibilityThreshold = Dp.VisibilityThreshold)
    }).value
    val animateElevation = if (isSelected) 12.dp else 2.dp
    bitmap?.let {
        Card(elevation = animateDpAsState(animateElevation, remember {
            spring(visibilityThreshold = Dp.VisibilityThreshold)
        }, null).value, modifier = Modifier
            .size(animateWidth)
            .clip(CutCornerShape(36.dp))
            .padding(16.dp)) {
            Image(bitmap = it,
                null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
        }
    }


}*/

@Composable
fun MainScreenHeader() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "For You", modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            style = TextStyle(color = titlesColor),
            fontSize = 35.sp, fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = {
                }),
            color = Color.White
        ) {
            materialIcon(imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.padding(8.dp), tint = Color.Red)
        }
    }

}


@ExperimentalFoundationApi
@Composable
fun MainScreenContent() {
    Column(modifier = Modifier
        .verticalScroll(state = rememberScrollState())
        .fillMaxSize()
        .padding(top = 32.dp)) {
        MainScreenHeader()
        SingerMainScreen()
        AlbumsGrid()
    }
}

@Composable
fun AlbumsGrid() {
    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier
            .width((screenWidth / 2).dp)
            .fillMaxHeight()
            .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            AlbumItem(height = (130..170).random(), screenWidth)
            AlbumItem(height = (200..250).random(), screenWidth)
            AlbumItem(height = (130..170).random(), screenWidth)
            AlbumItem(height = (200..250).random(), screenWidth)

        }
        Column(modifier = Modifier
            .width((screenWidth / 2).dp)
            .fillMaxHeight()
            .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            AlbumItem((200..250).random(), screenWidth)
            AlbumItem((130..170).random(), screenWidth)
            AlbumItem((200..250).random(), screenWidth)
            AlbumItem((130..170).random(), screenWidth)

        }
    }
}


@Composable
fun AlbumItem(height: Int, screenWidth: Float) {
    val bitmap = painterResource(id = R.drawable.cover)
    Column(modifier = Modifier.width((screenWidth / 3).dp)) {
        bitmap?.let {
            Image(it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(height.dp)
                    .clip(RoundedCornerShape(topStart = 32.dp,
                        topEnd = 8.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 32.dp)))
        }
        Spacer(modifier = Modifier.height(4.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(text = "songName",
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(72.dp),
                style = TextStyle(fontSize = 14.sp),
                softWrap = true,
                maxLines = 2)
        }
        Spacer(modifier = Modifier.height(4.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(text = "singerName",
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Composable
fun SingerMainScreen() {
    Row(modifier = Modifier
        .horizontalScroll(state = rememberScrollState())
        .fillMaxWidth()
        .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        SingerItem("Fear of the Water", "TVORCHI (Disco Lights)")
        SingerItem("Love on the Brain", "Rihanna")
        SingerItem("Hail the Victor", "30 Seconds to the mars")
        SingerItem("Like it doesn't hurt", "Charle Cardin")
        SingerItem("Fear of the Water", "TVORCHI (Disco Lights)")
        SingerItem("Love on the Brain", "Rihanna")
        SingerItem("Hail the Victor", "30 Seconds to the mars")
        SingerItem("Like it doesn't hurt", "Charle Cardin")

    }
}

@Composable
fun SingerItem(songName: String, singerName: String) {
    val bm = painterResource(id = R.drawable.cover)
    Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
        Surface(modifier = Modifier
            .width(72.dp)
            .height(72.dp),
            shape = RoundedCornerShape(topStart = 32f,
                topEnd = 16f,
                bottomStart = 16f,
                bottomEnd = 32f)) {
            bm?.let {
                Image(it, contentDescription = null, contentScale = ContentScale.Crop)
            }
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(text = songName,
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(72.dp),
                style = TextStyle(fontSize = 12.sp),
                softWrap = true,
                maxLines = 2)
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(text = singerName,
                style = TextStyle(fontSize = 6.sp),
                modifier = Modifier.align(Alignment.Start))
        }
    }
}

@Preview
@Composable
fun AlbumsGridPreview() {
    AlbumsGrid()

}

@Preview
@Composable
fun MainHeaderPreview() {
    MainScreenHeader()
}
































