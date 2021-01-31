package com.smusic.ui_playing_song.util

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.DragObserver
import androidx.compose.ui.gesture.dragGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*


//Math
//r = √(x2 + y2)
//x = r * cos(phi) -> arcos(x/r) = phi
//y = r * sin (phi)
//r - radius
//phi - angle

@Composable
fun RoundSlider(modifier: Modifier = Modifier) {
    val dragPosition = mutableStateOf(Offset.Zero)
    Canvas(
        modifier = modifier.dragGestureFilter(
            dragObserver = object : DragObserver {

                override fun onStart(downPosition: Offset) {
                    dragPosition.value = downPosition
                }

                override fun onDrag(dragDistance: Offset): Offset {
                    dragPosition.value = dragPosition.value + dragDistance
                    return super.onDrag(dragDistance)
                }
            }
        )
    ) {
        val (indicatorX, indicatorY) = calculateIndicatorPosition(dragPosition)

        translate(indicatorX, indicatorY) {
            drawCircle(
                color = Color.Magenta,
                radius = indicatorCircleRadius(),
                style = Fill
            )
        }

        drawCircle(
            color = Color.Magenta.copy(alpha = 0.4f),
            radius = outerCircleRadius(),
            style = Stroke(width = 6.dp.toPx())
        )
    }
}

private fun DrawScope.calculateIndicatorPosition(dragPosition: MutableState<Offset>): Offset {
    val dragXOnCanvas = dragPosition.value.x - horizontalCenter
    val dragYOnCanvas = dragPosition.value.y - verticalCenter
    val radius = radiusForPoint(dragXOnCanvas, dragYOnCanvas)
    val angle = kotlin.math.acos(dragXOnCanvas / radius)
    val adjustedAngle = if (dragYOnCanvas < 0) angle * -1 else angle
    val xOnCircle = outerCircleRadius() * cos(adjustedAngle)
    val yOnCircle = outerCircleRadius() * sin(adjustedAngle)
    return Offset(xOnCircle, yOnCircle)
}

fun radiusForPoint(x: Float, y: Float): Float {
    return sqrt(x.pow(2) + y.pow(2))
}

fun DrawScope.indicatorCircleRadius(): Float {
    return outerCircleRadius() / 12
}

private fun DrawScope.outerCircleRadius(): Float {
    return (horizontalCenter).coerceAtMost(verticalCenter)
}

private val DrawScope.horizontalCenter get() = size.width / 2
private val DrawScope.verticalCenter get() = size.height / 2




@RequiresApi(Build.VERSION_CODES.O)
fun Modifier.drawColoredShadow(
    color: Color,
    alpha: Float = 0.2f,
    borderRadius: Dp = 0.dp,
    shadowRadius: Dp = 20.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
) = this.drawBehind {
    val transparentColor = android.graphics.Color.toArgb(color.copy(alpha = 0.0f).value.toLong())
    val shadowColor = android.graphics.Color.toArgb(color.copy(alpha = alpha).value.toLong())
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowRadius.toPx(),
            offsetX.toPx(),
            offsetY.toPx(),
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            borderRadius.toPx(),
            borderRadius.toPx(),
            paint
        )
    }
}


@Composable
fun Draw(shape: Shape, color: Color, preferredSize: Dp) {
    Column(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)) {
        Box(
            modifier = Modifier.preferredSize(preferredSize).clip(shape).background(color)
        )
    }
}


@Composable
fun IconResource(
    @DrawableRes resourceId: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.Black,
) {
    val deferredResource = loadVectorResource(resourceId)
    deferredResource.onLoadRun { vector ->
        Icon(imageVector = vector,contentDescription = null, modifier = modifier, tint = tint)
    }
}

inline fun <T> DeferredResource<T>.onLoadRun(block: (T) -> Unit) {
    val res = resource.resource
    if (res != null) {
        block(res)
    }
}


@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    maxColumnWidth: Dp,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeableXY: MutableMap<Placeable, Pair<Int, Int>> = mutableMapOf()

        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columns = ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt()
        val columnWidth = constraints.maxWidth / columns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(columns) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            placeableXY[placeable] = Pair(columnWidth * column, colHeights[column])
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()
            ?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            placeables.forEach { placeable ->
                placeable.place(
                    x = placeableXY.getValue(placeable).first,
                    y = placeableXY.getValue(placeable).second
                )
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}