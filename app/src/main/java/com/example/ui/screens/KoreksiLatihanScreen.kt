package com.example.ui.screens

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
fun KoreksiLatihanScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    var filterTab by remember { mutableStateOf("MENUNGGU") } // "MENUNGGU", "SUDAH", "SEMUA"

    val filteredSubmissions = remember(allSubmissions, filterTab) {
        when (filterTab) {
            "MENUNGGU" -> allSubmissions.filter { it.status == "Menunggu Koreksi" }
            "SUDAH" -> allSubmissions.filter { it.status == "Sudah Dikoreksi" }
            else -> allSubmissions
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
                text = "Koreksi Latihan Siswa",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
            Text(
                text = "Periksa karya santri/siswa, berikan nilai kaidah dan catatan pembinaan",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterTab == "MENUNGGU",
                onClick = { filterTab = "MENUNGGU" },
                label = {
                    Text(
                        "Perlu Koreksi (${allSubmissions.count { it.status == "Menunggu Koreksi" }})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            FilterChip(
                selected = filterTab == "SUDAH",
                onClick = { filterTab = "SUDAH" },
                label = { Text("Sudah Dikoreksi (${allSubmissions.count { it.status == "Sudah Dikoreksi" }})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = filterTab == "SEMUA",
                onClick = { filterTab = "SEMUA" },
                label = { Text("Semua (${allSubmissions.size})", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredSubmissions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.CheckCircle,
                title = "Semua Sudah Dikoreksi! ✨",
                subtitle = "Tidak ada antrean pengumpulan tugas kaligrafi yang tertunda."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredSubmissions, key = { it.id }) { sub ->
                    KoreksiItemCard(
                        submission = sub,
                        onKoreksiClick = {
                            viewModel.openKoreksiDetail(sub)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KoreksiItemCard(
    submission: Submission,
    onKoreksiClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
    val isPending = submission.status == "Menunggu Koreksi"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onKoreksiClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = submission.namaSiswa,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = SkyContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = submission.kelas,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSkyContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                CorrectionBadge(status = submission.status, nilai = submission.nilai)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tugas: ${submission.tugasJudul}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = EmeraldDark
            )

            if (submission.catatanSiswa.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Catatan Siswa: \"${submission.catatanSiswa}\"",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kumpul: ${sdf.format(Date(submission.submittedAt))}",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )

                Button(
                    onClick = onKoreksiClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPending) EmeraldPrimary else SlateSurfaceVariant,
                        contentColor = if (isPending) Color.White else EmeraldDark
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isPending) Icons.Default.RateReview else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPending) "Koreksi Karya" else "Ubah Nilai (${submission.nilai})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
