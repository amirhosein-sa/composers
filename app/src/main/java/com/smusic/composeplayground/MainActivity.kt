package com.smusic.composeplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animate
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.myView)
    }

                                                                                             
}

@Composable
fun Item(isSelected: Boolean) {
    val context = AmbientContext.current.resources
    val displayMetrics = context.displayMetrics
    val scrWidth = displayMetrics.widthPixels / displayMetrics.density
    val bitmap = loadImageResource(id = R.drawable.cover).resource.resource
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
            .preferredSize(animateWidth)
            .clip(CutCornerShape(36.dp))
            .padding(16.dp)) {
            Image(bitmap = it,
                null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop)
        }
    }


}

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
    val context = AmbientContext.current
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
    val bitmap = loadImageResource(id = R.drawable.cover).resource.resource
    Column(modifier = Modifier.width((screenWidth / 3).dp)) {
        bitmap?.let {
            Image(it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(height.dp)
                    .clip(RoundedCornerShape(topLeft = 32.dp,
                        topRight = 8.dp,
                        bottomLeft = 8.dp,
                        bottomRight = 32.dp)))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(text = "songName",
                modifier = Modifier
                    .align(Alignment.Start)
                    .preferredWidth(72.dp),
                style = TextStyle(fontSize = 14.sp),
                softWrap = true,
                maxLines = 2)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
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
    val bm = loadImageResource(id = R.drawable.cover).resource.resource
    Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
        Surface(modifier = Modifier
            .preferredWidth(72.dp)
            .preferredHeight(72.dp),
            shape = RoundedCornerShape(topLeft = 32f,
                topRight = 16f,
                bottomLeft = 16f,
                bottomRight = 32f)) {
            bm?.let {
                Image(it,contentDescription = null, contentScale = ContentScale.Crop)
            }
        }
        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(text = songName,
                modifier = Modifier
                    .align(Alignment.Start)
                    .preferredWidth(72.dp),
                style = TextStyle(fontSize = 12.sp),
                softWrap = true,
                maxLines = 2)
        }
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
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
































