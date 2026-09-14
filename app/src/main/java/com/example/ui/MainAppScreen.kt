package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.components.MahasyamsTopBar
import com.example.ui.components.NotificationDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

@Composable
fun MainAppScreen(
    viewModel: CalligraphyViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifs = notifications.count { !it.isRead }
    val allUsers by viewModel.allUsers.collectAsState()

    var showNotifDialog by remember { mutableStateOf(false) }
    var showRoleSwitchDialog by remember { mutableStateOf(false) }

    if (!isLoggedIn || currentScreen == "LOGIN") {
        LoginScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Hide custom top bar on detail screens that have their own back top app bar
            if (currentScreen !in listOf("DETAIL_MATERI", "DETAIL_TUGAS", "DETAIL_KOREKSI")) {
                MahasyamsTopBar(
                    currentUser = currentUser,
                    unreadNotifCount = unreadNotifs,
                    onRoleSwitchClick = { showRoleSwitchDialog = true },
                    onNotifClick = { showNotifDialog = true },
                    onLogoutClick = { viewModel.logout() }
                )
            }
        },
        bottomBar = {
            // Hide bottom bar on deep detail screens for more focused workspace
            if (currentScreen !in listOf("DETAIL_MATERI", "DETAIL_TUGAS", "DETAIL_KOREKSI")) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "DASHBOARD",
                        onClick = { viewModel.navigateTo("DASHBOARD") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                        label = { Text("Beranda", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen in listOf("ISTILAH_KHAT", "HIJAIYAH"),
                        onClick = { viewModel.navigateTo("ISTILAH_KHAT") },
                        icon = { Icon(Icons.Default.FormatQuote, contentDescription = "Khat") },
                        label = { Text("Khat", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "MATERI",
                        onClick = { viewModel.navigateTo("MATERI") },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Materi") },
                        label = { Text("Materi", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "TUGAS",
                        onClick = { viewModel.navigateTo("TUGAS") },
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Tugas") },
                        label = { Text("Tugas", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        )
                    )

                    // Role-based specific bottom tab
                    if (currentUser.role == "SISWA") {
                        NavigationBarItem(
                            selected = currentScreen == "LATIHAN_SAYA",
                            onClick = { viewModel.navigateTo("LATIHAN_SAYA") },
                            icon = { Icon(Icons.Default.Palette, contentDescription = "Latihan") },
                            label = { Text("Latihan", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                indicatorColor = EmeraldContainer
                            )
                        )
                    } else if (currentUser.role in listOf("GURU")) {
                        NavigationBarItem(
                            selected = currentScreen == "KOREKSI",
                            onClick = { viewModel.navigateTo("KOREKSI") },
                            icon = { Icon(Icons.Default.RateReview, contentDescription = "Koreksi") },
                            label = { Text("Koreksi", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                indicatorColor = EmeraldContainer
                            )
                        )
                    } else { // ADMIN
                        NavigationBarItem(
                            selected = currentScreen == "ADMIN",
                            onClick = { viewModel.navigateTo("ADMIN") },
                            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                            label = { Text("Admin", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldDark,
                                indicatorColor = EmeraldContainer
                            )
                        )
                    }

                    NavigationBarItem(
                        selected = currentScreen == "PROFIL",
                        onClick = { viewModel.navigateTo("PROFIL") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldDark,
                            indicatorColor = EmeraldContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                "DASHBOARD" -> HomeScreen(viewModel = viewModel, currentUser = currentUser)
                "ISTILAH_KHAT" -> IstilahKhatScreen(viewModel = viewModel, currentUser = currentUser)
                "HIJAIYAH" -> HijaiyahScreen(viewModel = viewModel, currentUser = currentUser)
                "MATERI" -> MateriScreen(viewModel = viewModel, currentUser = currentUser)
                "DETAIL_MATERI" -> PembelajaranDetailScreen(viewModel = viewModel, currentUser = currentUser)
                "TUGAS" -> TugasScreen(viewModel = viewModel, currentUser = currentUser)
                "DETAIL_TUGAS" -> DetailTugasScreen(viewModel = viewModel, currentUser = currentUser)
                "LATIHAN_SAYA" -> LatihanSayaScreen(viewModel = viewModel, currentUser = currentUser)
                "KOREKSI" -> KoreksiLatihanScreen(viewModel = viewModel, currentUser = currentUser)
                "DETAIL_KOREKSI" -> DetailKoreksiScreen(viewModel = viewModel, currentUser = currentUser)
                "KHATHATH" -> KhathathScreen(viewModel = viewModel, currentUser = currentUser)
                "ADMIN" -> AdminScreen(viewModel = viewModel, currentUser = currentUser)
                "PROFIL" -> ProfileScreen(viewModel = viewModel, currentUser = currentUser)
                else -> HomeScreen(viewModel = viewModel, currentUser = currentUser)
            }
        }
    }

    // Role Switch Dialog
    if (showRoleSwitchDialog) {
        AlertDialog(
            onDismissRequest = { showRoleSwitchDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ganti Peran Pengguna", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Pilih akun untuk menguji fitur peran Siswa, Guru, atau Admin:",
                        fontSize = 12.sp,
                        color = SlateTextSecondary
                    )
                    allUsers.forEach { user ->
                        val isCurrent = user.uid == currentUser.uid
                        Surface(
                            onClick = {
                                viewModel.switchUser(user)
                                showRoleSwitchDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCurrent) EmeraldContainer.copy(alpha = 0.5f) else SlateSurfaceVariant,
                            border = if (isCurrent) BorderStroke(1.5.dp, EmeraldPrimary) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(user.nama, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${user.role} • ${user.email}", fontSize = 11.sp, color = SlateTextSecondary)
                                }
                                if (isCurrent) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleSwitchDialog = false }) {
                    Text("Tutup", color = EmeraldPrimary)
                }
            }
        )
    }

    // Notification Dialog
    if (showNotifDialog) {
        NotificationDialog(
            notifications = notifications,
            onDismiss = { showNotifDialog = false },
            onMarkRead = { notifId -> viewModel.markNotifikasiRead(notifId) }
        )
    }
}
