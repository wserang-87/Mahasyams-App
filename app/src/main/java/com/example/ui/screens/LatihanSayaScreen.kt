package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Submission
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.CorrectionBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatihanSayaScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val mySubmissions by viewModel.mySubmissions.collectAsState()
    var filterStatus by remember { mutableStateOf("SEMUA") }
    var selectedSubmissionForDetail by remember { mutableStateOf<Submission?>(null) }

    val filteredList = remember(mySubmissions, filterStatus) {
        when (filterStatus) {
            "MENUNGGU" -> mySubmissions.filter { it.status == "Menunggu Koreksi" }
            "SUDAH" -> mySubmissions.filter { it.status == "Sudah Dikoreksi" }
            else -> mySubmissions
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Latihan & Karya Saya",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
            Text(
                text = "Pantau status koreksi, nilai, dan catatan ulasan kaligrafi dari guru",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterStatus == "SEMUA",
                onClick = { filterStatus = "SEMUA" },
                label = { Text("Semua (${mySubmissions.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filterStatus == "MENUNGGU",
                onClick = { filterStatus = "MENUNGGU" },
                label = { Text("🟡 Menunggu (${mySubmissions.count { it.status == "Menunggu Koreksi" }})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filterStatus == "SUDAH",
                onClick = { filterStatus = "SUDAH" },
                label = { Text("🟢 Dinilai (${mySubmissions.count { it.status == "Sudah Dikoreksi" }})", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Palette,
                title = "Belum Ada Latihan",
                subtitle = "Anda belum mengumpulkan karya latihan untuk kategori ini. Buka menu Tugas untuk mulai menulis!",
                actionLabel = "Lihat Tugas",
                onAction = { viewModel.navigateTo("TUGAS") }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList, key = { it.id }) { submission ->
                    SubmissionItemCard(
                        submission = submission,
                        onClick = { selectedSubmissionForDetail = submission }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedSubmissionForDetail?.let { sub ->
        SubmissionDetailDialog(
            submission = sub,
            onDismiss = { selectedSubmissionForDetail = null }
        )
    }
}

@Composable
private fun SubmissionItemCard(
    submission: Submission,
    onClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val isGraded = submission.status == "Sudah Dikoreksi"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Khat ${submission.khatId.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                CorrectionBadge(status = submission.status, nilai = submission.nilai)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = submission.tugasJudul,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SlateTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Dikirim: ${sdf.format(Date(submission.submittedAt))}",
                fontSize = 11.sp,
                color = SlateTextSecondary
            )

            // If Graded, show score highlight
            if (isGraded && submission.nilai != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Komentar Guru (${submission.guruKorektor ?: "Guru"}):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Text(
                                text = submission.komentar ?: "Bagus",
                                fontSize = 12.sp,
                                color = SlateTextPrimary,
                                maxLines = 2
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${submission.nilai}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionDetailDialog(
    submission: Submission,
    onDismiss: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Hasil Latihan & Koreksi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EmeraldDark
                )
                Text(
                    text = submission.tugasJudul,
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Artwork Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFFDF8))
                        .border(1.dp, AmberAccent.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Berkas: ${submission.fileName}",
                            fontSize = 10.sp,
                            color = SlateTextSecondary
                        )
                    }
                }

                if (submission.catatanSiswa.isNotBlank()) {
                    Text(
                        text = "Catatan Anda: \"${submission.catatanSiswa}\"",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                }

                Divider(color = SlateBorder)

                // Correction info
                if (submission.status == "Sudah Dikoreksi") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Nilai Evaluasi:", fontSize = 12.sp, color = SlateTextSecondary)
                            Text("${submission.nilai} / 100", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Guru Korektor:", fontSize = 11.sp, color = SlateTextSecondary)
                            Text(submission.guruKorektor ?: "-", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateTextPrimary)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Ulasan & Pembinaan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(submission.komentar ?: "-", fontSize = 12.sp, color = SlateTextPrimary, lineHeight = 17.sp)
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = OnAmberContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Karya Anda telah diterima dan sedang menunggu giliran koreksi dari Ustadz.",
                                fontSize = 12.sp,
                                color = OnAmberContainer,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Tutup")
            }
        }
    )
}
