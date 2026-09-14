package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.CalligraphyViewModel
import com.example.ui.components.CorrectionBadge
import com.example.ui.components.DeadlineBadge
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val materiList by viewModel.materiList.collectAsState()
    val tugasList by viewModel.tugasList.collectAsState()
    val mySubmissions by viewModel.mySubmissions.collectAsState()
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    val pendingCorrections by viewModel.pendingCorrections.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allSiswa by viewModel.allSiswa.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Salam Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_kaligrafi),
                        contentDescription = "Kaligrafi Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Dark Emerald Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        EmeraldDark.copy(alpha = 0.5f),
                                        EmeraldDark.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "Assalamu'alaikum,",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${currentUser.nama} 👋",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AmberAccent.copy(alpha = 0.9f)
                            ) {
                                Text(
                                    text = currentUser.role,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "مَهَا شَمْس — Mahasyams",
                                color = AmberContainer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Mari belajar kaligrafi dengan sabar, rapi, dan penuh adab.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Stats Row (Section 40, 41, 42 of masterplan)
        item {
            when (currentUser.role) {
                "SISWA" -> {
                    val averageScore = mySubmissions.mapNotNull { it.nilai }.let {
                        if (it.isNotEmpty()) it.average().toInt() else 0
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Materi", materiList.size.toString(), Icons.Default.MenuBook, EmeraldPrimary, Modifier.weight(1f))
                        StatCard("Tugas", tugasList.size.toString(), Icons.Default.Assignment, SkySecondary, Modifier.weight(1f))
                        StatCard("Latihan", mySubmissions.size.toString(), Icons.Default.Brush, AmberAccent, Modifier.weight(1f))
                        StatCard("Rata-rata", if (averageScore > 0) averageScore.toString() else "-", Icons.Default.Star, EmeraldPrimary, Modifier.weight(1f))
                    }
                }
                "GURU" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Siswa", allSiswa.size.toString(), Icons.Default.People, SkySecondary, Modifier.weight(1f))
                        StatCard("Materi", materiList.size.toString(), Icons.Default.MenuBook, EmeraldPrimary, Modifier.weight(1f))
                        StatCard("Tugas", tugasList.size.toString(), Icons.Default.Assignment, AmberAccent, Modifier.weight(1f))
                        StatCard("Koreksi", pendingCorrections.size.toString(), Icons.Default.RateReview, StatusError, Modifier.weight(1f))
                    }
                }
                else -> { // ADMIN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard("Total User", allUsers.size.toString(), Icons.Default.SupervisedUserCircle, SkySecondary, Modifier.weight(1f))
                        StatCard("Guru", allUsers.count { it.role == "GURU" }.toString(), Icons.Default.School, EmeraldPrimary, Modifier.weight(1f))
                        StatCard("Siswa", allSiswa.size.toString(), Icons.Default.People, AmberAccent, Modifier.weight(1f))
                        StatCard("Karya", allSubmissions.size.toString(), Icons.Default.Brush, EmeraldDark, Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Menu Shortcuts
        item {
            Text(
                text = "Menu Pembelajaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickMenuItem(
                        title = "Istilah Khat",
                        arabic = "أنواع الخط",
                        icon = Icons.Default.FormatQuote,
                        color = EmeraldPrimary,
                        onClick = { viewModel.navigateTo("ISTILAH_KHAT") }
                    )
                }
                item {
                    QuickMenuItem(
                        title = "Hijaiyah",
                        arabic = "الحروف الهجائية",
                        icon = Icons.Default.Translate,
                        color = SkySecondary,
                        onClick = { viewModel.navigateTo("HIJAIYAH") }
                    )
                }
                item {
                    QuickMenuItem(
                        title = "Materi & Video",
                        arabic = "الدروس والوسائط",
                        icon = Icons.Default.PlayCircle,
                        color = AmberAccent,
                        onClick = { viewModel.navigateTo("MATERI") }
                    )
                }
                item {
                    QuickMenuItem(
                        title = "Tugas Kaligrafi",
                        arabic = "الواجبات",
                        icon = Icons.Default.AssignmentTurnedIn,
                        color = EmeraldDark,
                        onClick = { viewModel.navigateTo("TUGAS") }
                    )
                }
                if (currentUser.role == "SISWA") {
                    item {
                        QuickMenuItem(
                            title = "Latihan Saya",
                            arabic = "تماريني",
                            icon = Icons.Default.Palette,
                            color = SkySecondary,
                            onClick = { viewModel.navigateTo("LATIHAN_SAYA") }
                        )
                    }
                }
                if (currentUser.role in listOf("GURU", "ADMIN")) {
                    item {
                        QuickMenuItem(
                            title = "Koreksi Latihan",
                            arabic = "تصحيح التمارين",
                            icon = Icons.Default.RateReview,
                            color = StatusError,
                            badge = pendingCorrections.size.toString(),
                            onClick = { viewModel.navigateTo("KOREKSI") }
                        )
                    }
                    item {
                        QuickMenuItem(
                            title = "Daftar Khathath",
                            arabic = "قائمة الخطاطين",
                            icon = Icons.Default.PeopleAlt,
                            color = EmeraldPrimary,
                            onClick = { viewModel.navigateTo("KHATHATH") }
                        )
                    }
                }
            }
        }

        // Tugas Terdekat Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tugas Aktif",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SlateTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo("TUGAS") }) {
                    Text("Lihat Semua", color = EmeraldPrimary, fontSize = 12.sp)
                }
            }

            if (tugasList.isEmpty()) {
                Text("Belum ada tugas saat ini.", color = SlateTextSecondary, fontSize = 13.sp)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tugasList.take(2).forEach { tugas ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SlateSurface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.openTugasDetail(tugas) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
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
                                            text = tugas.khatId.uppercase(),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnEmeraldContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    DeadlineBadge(deadlineMs = tugas.deadline)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = tugas.judul,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SlateTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tugas.deskripsi,
                                    fontSize = 12.sp,
                                    color = SlateTextSecondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }

        // Materi Terbaru Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Materi Pembelajaran",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = SlateTextPrimary
                )
                TextButton(onClick = { viewModel.navigateTo("MATERI") }) {
                    Text("Lihat Semua", color = EmeraldPrimary, fontSize = 12.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                materiList.take(3).forEach { materi ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateSurface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openMateriDetail(materi) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (materi.mimeType.contains("video")) SkyContainer else AmberContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (materi.mimeType.contains("video")) Icons.Default.PlayArrow else Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = if (materi.mimeType.contains("video")) OnSkyContainer else OnAmberContainer
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = materi.khatNama,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = materi.judul,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = SlateTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${materi.tipeSumber} • ${materi.createdBy}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Buka",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = SlateTextPrimary
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = SlateTextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuickMenuItem(
    title: String,
    arabic: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    badge: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = SlateSurface,
        shadowElevation = 1.dp,
        modifier = Modifier.width(130.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (badge != null && badge != "0") {
                    Badge(containerColor = StatusError) {
                        Text(badge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SlateTextPrimary
            )
            Text(
                text = arabic,
                fontSize = 11.sp,
                color = AmberAccent,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
