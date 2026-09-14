package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CalligraphyPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun CalligraphyCanvas(
    modifier: Modifier = Modifier,
    onStrokesChanged: (String) -> Unit = {}
) {
    var paths by remember { mutableStateOf(listOf<CalligraphyPath>()) }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF1E293B)) } // Tinta Hitam
    var strokeWidth by remember { mutableStateOf(16f) }
    var showGuidelines by remember { mutableStateOf(true) }

    val colors = listOf(
        Color(0xFF0F172A) to "Tinta Hitam",
        Color(0xFFB45309) to "Tinta Emas",
        Color(0xFF047857) to "Tinta Zamrud",
        Color(0xFF0369A1) to "Tinta Safir"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFFDF8)) // Textured parchment white
            .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "قلم الخط العربي (Qalam Kaligrafi)",
                style = MaterialTheme.typography.labelLarge,
                color = EmeraldDark
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Toggle Pedoman Garis
                IconButton(
                    onClick = { showGuidelines = !showGuidelines },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Garis Pedoman",
                        tint = if (showGuidelines) EmeraldPrimary else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Undo
                IconButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            paths = paths.dropLast(1)
                            onStrokesChanged("PATHS:${paths.size}")
                        }
                    },
                    enabled = paths.isNotEmpty(),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Undo",
                        tint = if (paths.isNotEmpty()) EmeraldDark else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Clear
                IconButton(
                    onClick = {
                        paths = emptyList()
                        currentPoints = emptyList()
                        onStrokesChanged("")
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus Bersih",
                        tint = StatusError,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Drawing Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFCFBF7))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + change.position
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    paths = paths + CalligraphyPath(currentPoints, selectedColor, strokeWidth)
                                    currentPoints = emptyList()
                                    onStrokesChanged("PATHS:${paths.size}_PTS:${paths.sumOf { it.points.size }}")
                                }
                            }
                        )
                    }
            ) {
                // Draw Calligraphy guidelines (Mizan Khat)
                if (showGuidelines) {
                    val lineSpacing = size.height / 5
                    for (i in 1..4) {
                        val y = i * lineSpacing
                        val isBaseline = i == 3
                        drawLine(
                            color = if (isBaseline) EmeraldPrimary.copy(alpha = 0.45f) else Color(0xFFCBD5E1),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = if (isBaseline) 2f else 1f
                        )
                    }
                }

                // Draw completed paths
                for (p in paths) {
                    if (p.points.size > 1) {
                        val path = Path().apply {
                            moveTo(p.points.first().x, p.points.first().y)
                            for (i in 1 until p.points.size) {
                                val prev = p.points[i - 1]
                                val curr = p.points[i]
                                quadraticTo(prev.x, prev.y, (prev.x + curr.x) / 2, (prev.y + curr.y) / 2)
                            }
                        }
                        drawPath(
                            path = path,
                            color = p.color,
                            style = Stroke(
                                width = p.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (p.points.isNotEmpty()) {
                        drawCircle(
                            color = p.color,
                            radius = p.strokeWidth / 2,
                            center = p.points.first()
                        )
                    }
                }

                // Draw active drawing stroke
                if (currentPoints.size > 1) {
                    val path = Path().apply {
                        moveTo(currentPoints.first().x, currentPoints.first().y)
                        for (i in 1 until currentPoints.size) {
                            val prev = currentPoints[i - 1]
                            val curr = currentPoints[i]
                            quadraticTo(prev.x, prev.y, (prev.x + curr.x) / 2, (prev.y + curr.y) / 2)
                        }
                    }
                    drawPath(
                        path = path,
                        color = selectedColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (paths.isEmpty() && currentPoints.isEmpty()) {
                Text(
                    text = "✍️ Goreskan Qalam kaligrafi di sini...\n(Tulis huruf atau lafadz tugas Anda)",
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tool Controls: Size and Colors
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colors
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { (color, _) ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selectedColor == color) 2.5.dp else 1.dp,
                                color = if (selectedColor == color) AmberAccent else Color.LightGray,
                                shape = CircleShape
                            )
                    ) {
                        IconButton(
                            onClick = { selectedColor = color },
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            }

            // Brush Sizes (Nuqthah ukuran kalam)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(8f to "Kecil", 16f to "Sedang", 26f to "Besar").forEach { (size, label) ->
                    FilterChip(
                        selected = strokeWidth == size,
                        onClick = { strokeWidth = size },
                        label = { Text(label, fontSize = 11.sp) },
                        modifier = Modifier.height(30.dp)
                    )
                }
            }
        }
    }
}
