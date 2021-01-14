package com.amir.composeplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animate
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.amir.base_android.Pager
import com.amir.base_android.PagerState
import com.amir.composeplayground.databinding.ActivityMainBinding
import com.amir.composeplayground.ui.titlesColor
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

@Preview
@Composable
fun InflatedRowPreview() {

    val pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) {
            PagerState(clock, 0, 0, 52)
        }
    }
    Pager(state = pagerState, modifier = Modifier.fillMaxSize()) {
        val isSelected = currentPage == page
        item(isSelected = isSelected)
    }
}

@Composable
fun item(isSelected: Boolean) {
    val resources = AmbientContext.current.resources
    val displayMetrics = resources.displayMetrics
    val scrWidth = displayMetrics.widthPixels / displayMetrics.density
    val bitmap = loadImageResource(id = R.drawable.cover).resource.resource
    val animateHeight = animate(if (isSelected) 645.dp else 360.dp)
    val animateWidth = animate(if (isSelected) 340.dp else 320.dp)
    val animateElevation = if (isSelected) 12.dp else 2.dp
    bitmap?.let {
        Card(elevation = animate(animateElevation), modifier = Modifier
            .preferredSize(animateWidth).clip(CutCornerShape(36.dp)).padding(16.dp)) {
            Image(bitmap = it,
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
            fontSize = TextUnit.Sp(35), fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .padding(end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = {
                }),
            color = Color.White
        ) {
            materialIcon(Icons.Default.Favorite,
                modifier = Modifier.padding(8.dp), tint = Color.Red)
        }
    }

}


@ExperimentalFoundationApi
@Composable
fun MainScreenContent() {
    ScrollableColumn(modifier = Modifier
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
                style = TextStyle(fontSize = TextUnit.Companion.Sp(14)),
                softWrap = true,
                maxLines = 2)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(text = "singerName",
                style = TextStyle(fontSize = TextUnit.Companion.Sp(12)),
                modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Composable
fun SingerMainScreen() {
    ScrollableRow(modifier = Modifier
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
                Image(it, contentScale = ContentScale.Crop)
            }
        }
        Providers(AmbientContentAlpha provides ContentAlpha.high) {
            Text(text = songName,
                modifier = Modifier
                    .align(Alignment.Start)
                    .preferredWidth(72.dp),
                style = TextStyle(fontSize = TextUnit.Companion.Sp(12)),
                softWrap = true,
                maxLines = 2)
        }
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Text(text = singerName,
                style = TextStyle(fontSize = TextUnit.Companion.Sp(6)),
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