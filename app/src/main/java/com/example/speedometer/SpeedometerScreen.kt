package com.example.speedometer

import androidx.annotation.FloatRange
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.nio.file.Files.size as size1


import androidx.compose.animation.core.LinearEasing

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.geometry.Offset

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerScreen( modifier: Modifier = Modifier){
    var speedAnimation by remember {
        mutableFloatStateOf(0f)
    }


    LaunchedEffect(true) {
        animate(
            0f,
            240f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    4000,
                    1000, LinearEasing
                ),
                RepeatMode.Reverse
            )
        ){
            value,velocity->
           speedAnimation = value
        }
    }
    Speedometer(currentSpeed = speedAnimation, Modifier
        .padding(90.dp)
        .requiredSize(360.dp))
}

@Composable
private fun Speedometer(
    @FloatRange(from = 0.0, to = 240.0) currentSpeed: Float,
    modifier: Modifier = Modifier
) {
    //required to show speed texts
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier, onDraw = {
        val circleRadius = size.height / 2

        val mainColor = when {
            currentSpeed < 80 -> Color.Green
            currentSpeed < 160 -> Color.Yellow
            else -> Color.Red
        }

        //secondary arc below main arc
        drawArc(
            color = Color.LightGray,
            startAngle = 30f,
            sweepAngle = -240f,
            useCenter = false,
            style = Stroke(width = 5.0.dp.toPx())
        )

        //main arc above secondary arc
        drawArc(
            color = mainColor,
            startAngle = 150f,
            sweepAngle = currentSpeed,
            style = Stroke(
                width = 5.0.dp.toPx(),
                cap = StrokeCap.Round
            ),
            useCenter = false
        )

        for (speed in 0..240 step 2) {

            val angleInRad =
                Math.toRadians(speed + 150.0)  //to start drawing at 150 degree angle anticlockwise

            val lineLength = if (speed % 20 == 0) {
                circleRadius - 50f
            } else {
                circleRadius - 40f
            }

            val lineThickness = if (speed % 20 == 0) {
                5f
            } else if (speed % 10 == 0) {
                2f
            } else {
                1f
            }

            val startOffset = calculateOffSet(
                angleInRad, circleRadius - 20f, center
            )

            val endOffset = calculateOffSet(
                angleInRad, lineLength, center
            )

            //draw all markers
            drawLine(
                color = Color.Black,
                start = startOffset,
                end = endOffset,
                strokeWidth = lineThickness.dp.toPx()
            )


            //draw texts only if speed is multiple of 20
            if (speed % 20 == 0) {
                val textMarker = textMeasurer.measure(
                    text = speed.toString(),
                    style = TextStyle.Default.copy(fontSize = 15.sp)
                )
                val textWidth = textMarker.size.width
                val textHeight = textMarker.size.height

                val textOffset = calculateOffSet(
                    angleInRad, circleRadius - 90f, center
                )

                //draw speed Text
                drawText(
                    textMarker,
                    color = Color.Black,
                    topLeft = Offset(
                        textOffset.x - textWidth / 2,
                        textOffset.y - textHeight / 2
                    )
                )
            }

            val textBottom = textMeasurer.measure(
                text = currentSpeed.toInt().toString(),
                style = TextStyle.Default.copy(fontSize = 25.sp)
            )

            //draw bottom text
            drawText(
                textBottom, Color.Black, Offset(
                    size.width.times(0.45f),
                    size.height.times(.6f)
                )
            )
        }

        //it is the inner circle for indicator base
        drawCircle(
            color = Color.Black,
            radius = 40f,
            center = center
        )

        //draw indicator
        val indicatorOffset = calculateOffSet(
            0.0, circleRadius - 20f, center
        )

        val indicatorPath = Path().apply {
            moveTo(center.x, center.y)
            // move bottom
            lineTo(center.x, center.y - 20f)
            // move left
            lineTo(indicatorOffset.x, indicatorOffset.y)
            //move right
            lineTo(center.x, center.y + 20f)
            close()
        }

        rotate(currentSpeed + 150, center) {
            drawPath(indicatorPath, Color.Red.copy(0.7f))
        }
    })
}

private fun calculateOffSet(
    degrees: Double,
    radius: Float,
    center: Offset
): Offset {
    val x = (radius * cos(degrees) + center.x).toFloat()
    val y = (radius * sin(degrees) + center.x).toFloat()

    return Offset(x, y)
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpeedometerPreview(modifier: Modifier = Modifier){
SpeedometerScreen()
}