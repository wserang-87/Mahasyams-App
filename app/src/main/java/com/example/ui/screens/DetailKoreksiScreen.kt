package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.Submission
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailKoreksiScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val submission = viewModel.selectedSubmission.collectAsState().value

    if (submission == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Pilih tugas yang akan dikoreksi terlebih dahulu.")
        }
        return
    }

    var nilai by remember { mutableStateOf(submission.nilai?.toFloat() ?: 85f) }
    var komentar by remember {
        mutableStateOf(
            submission.komentar ?: "Alhamdulillah bentuk dasar huruf sudah proporsional. Perhatikan tarikan ekor huruf dan konsistensi ketebalan garis qalam agar lebih mantap."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // TopBar
        TopAppBar(
            title = { Text("Lembar Koreksi Kaligrafi", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            navigationIcon = {
                IconButton(onClick = { viewModel.navigateTo("KOREKSI") }) {
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
            // Student & Task Info Card (Masterplan Section 35)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = submission.namaSiswa,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "Kelas: ${submission.kelas}",
                                fontSize = 12.sp,
                                color = SlateTextSecondary
                            )
                        }
                        Surface(
                            color = EmeraldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Khat ${submission.khatId.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnEmeraldContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tugas: ${submission.tugasJudul}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = EmeraldDark
                    )

                    if (submission.catatanSiswa.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = SlateSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Catatan Siswa: \"${submission.catatanSiswa}\"",
                                fontSize = 11.sp,
                                color = SlateTextSecondary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            // Preview Karya Siswa (Masterplan Section 35 & 36)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Preview Karya Kaligrafi Siswa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFDF8))
                            .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Latihan Huruf & Rangkaian Kata (${submission.khatId.uppercase()})",
                                fontSize = 11.sp,
                                color = SlateTextSecondary
                            )
                            Text(
                                text = "Format: ${submission.mimeType} • ${submission.fileName}",
                                fontSize = 10.sp,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Grading & Feedback Form (Masterplan Section 35, 37, 72)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nilai Evaluasi (0 - 100)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "${nilai.toInt()}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    // Score Slider
                    Slider(
                        value = nilai,
                        onValueChange = { nilai = it },
                        valueRange = 50f..100f,
                        steps = 50,
                        colors = SliderDefaults.colors(
                            thumbColor = AmberAccent,
                            activeTrackColor = EmeraldPrimary
                        )
                    )

                    // Quick Score Preset Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(75f, 80f, 85f, 90f, 95f, 100f).forEach { scorePreset ->
                            FilterChip(
                                selected = nilai.toInt() == scorePreset.toInt(),
                                onClick = { nilai = scorePreset },
                                label = { Text("${scorePreset.toInt()}", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Komentar & Catatan Perbaikan Guru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = komentar,
                        onValueChange = { komentar = it },
                        label = { Text("Tuliskan evaluasi proporsi, tarikan garis, kerapian...") },
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = SlateBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.simpanKoreksi(
                                submissionId = submission.id,
                                nilai = nilai.toInt(),
                                komentar = komentar
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simpan Hasil Koreksi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
