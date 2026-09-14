package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Materi
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembelajaranDetailScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val materi = viewModel.selectedMateri.collectAsState().value

    if (materi == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pilih materi pembelajaran terlebih dahulu.")
        }
        return
    }

    var isPlaying by remember { mutableStateOf(false) }
    var videoProgress by remember { mutableStateOf(0.35f) }
    var pdfZoom by remember { mutableStateOf(100) }
    var pdfPage by remember { mutableStateOf(1) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Pembelajaran Kaligrafi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo("MATERI") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = materi.khatNama,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnEmeraldContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (materi.hurufNama.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = AmberContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Huruf ${materi.hurufNama}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAmberContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Format: ${if (materi.mimeType.contains("pdf")) "PDF" else if (materi.mimeType.contains("image")) "Gambar" else "Video"}",
                        fontSize = 11.sp,
                        color = SlateTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = materi.judul,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SlateTextPrimary
                )

                Text(
                    text = "Diajarkan oleh: ${materi.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary
                )
            }

            // Player or PDF/Image Viewer
            val isVideo = materi.mimeType.contains("video") || materi.fileUrl.contains("youtube")
            val isImage = materi.mimeType.contains("image") || materi.fileName.endsWith(".png") || materi.fileName.endsWith(".jpg")
            if (isVideo) {
                // Video / YouTube Player Component
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) EmeraldPrimary else Color.White.copy(alpha = 0.9f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(onClick = { isPlaying = !isPlaying }) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play/Pause",
                                            tint = if (isPlaying) Color.White else EmeraldDark,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = if (isPlaying) "Memutar Video Pembelajaran..." else "Klik untuk Memutar Video",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = materi.fileUrl.ifBlank { "Firebase Storage Video Stream" },
                                    color = Color.LightGray,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Video progress slider & controls
                        Slider(
                            value = videoProgress,
                            onValueChange = { videoProgress = it },
                            colors = SliderDefaults.colors(
                                thumbColor = AmberAccent,
                                activeTrackColor = EmeraldPrimary,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("04:12 / 12:45", color = Color.White, fontSize = 11.sp)
                            Row {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            } else if (isImage) {
                // Image / Lembar Kaidah Visual Viewer
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = materi.fileName.ifBlank { "Lembar_Kaidah_Kaligrafi.png" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (pdfZoom > 50) pdfZoom -= 10 }) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                                }
                                Text("$pdfZoom%", fontSize = 11.sp, color = SlateTextSecondary)
                                IconButton(onClick = { if (pdfZoom < 200) pdfZoom += 10 }) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // High Definition Calligraphy Sheet Rendering
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFFDF8))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "LEMBAR KAIDAH GORESAN ${if (materi.hurufNama.isNotBlank()) "HURUF ${materi.hurufNama.uppercase()}" else materi.khatNama.uppercase()}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnEmeraldContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (materi.hurufNama.isNotBlank()) "ا ب ت ث ج ح خ د ذ ر ز س ش ص" else "قُلْ هُوَ اللَّهُ أَحَدٌ",
                                    fontSize = (28 * (pdfZoom / 100f)).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Proporsi Anatomi & Ukuran Titik Nuqthah (Skala HD)",
                                    fontSize = (12 * (pdfZoom / 100f)).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Kemiringan qalam bambu dijaga konstan.\n• Ketebalan tarikan mengikuti tekanan ujung pena.\n• Panduan grid merah mengindikasikan batas garis dasar (satr).",
                                    fontSize = (11 * (pdfZoom / 100f)).sp,
                                    color = SlateTextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // PDF Viewer Component (Section 21)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                    border = BorderStroke(1.dp, SlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = StatusError)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = materi.fileName.ifBlank { "Modul_Kaligrafi.pdf" },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { if (pdfZoom > 50) pdfZoom -= 10 }) {
                                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", modifier = Modifier.size(18.dp))
                                }
                                Text("$pdfZoom%", fontSize = 11.sp, color = SlateTextSecondary)
                                IconButton(onClick = { if (pdfZoom < 200) pdfZoom += 10 }) {
                                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // PDF Page Simulated Canvas
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFFDF8))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontSize = (26 * (pdfZoom / 100f)).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Kaidah Ukuran Tarikan Garis Huruf ${materi.khatNama} ${if (materi.hurufNama.isNotBlank()) "(${materi.hurufNama})" else ""}",
                                    fontSize = (13 * (pdfZoom / 100f)).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateTextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "1. Sudut pena diletakkan miring 70-80 derajat.\n2. Alif ditulis tegak dengan tinggi 5 titik belah ketupat.\n3. Lengkungan cawan huruf harus halus dan tidak patah.",
                                    fontSize = (11 * (pdfZoom / 100f)).sp,
                                    color = SlateTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SlateSurfaceVariant,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            ) {
                                Text(
                                    text = "Halaman $pdfPage dari 12",
                                    fontSize = 10.sp,
                                    color = SlateTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Description Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tentang Materi Ini",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = materi.deskripsi,
                        fontSize = 13.sp,
                        color = SlateTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Action Button: Kerjakan Tugas
            Button(
                onClick = { viewModel.navigateTo("TUGAS") },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lihat & Kerjakan Tugas Kaligrafi", fontWeight = FontWeight.Bold)
            }
        }
    }
}
