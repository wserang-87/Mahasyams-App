package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.User
import com.example.ui.CalligraphyViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: CalligraphyViewModel,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card (Masterplan Section 43, 44)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer)
                        .border(2.dp, AmberAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentUser.nama,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = SlateTextPrimary
                )

                Text(
                    text = currentUser.email,
                    fontSize = 13.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = when (currentUser.role) {
                        "ADMIN" -> AmberContainer
                        "GURU" -> SkyContainer
                        else -> EmeraldContainer
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Peran: ${currentUser.role}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentUser.role) {
                            "ADMIN" -> OnAmberContainer
                            "GURU" -> OnSkyContainer
                            else -> OnEmeraldContainer
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = SlateBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Detail Data
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileItemRow("Username", "@${currentUser.username}", Icons.Default.AlternateEmail)
                    ProfileItemRow("Kelas / Rombel", currentUser.kelas, Icons.Default.Class)
                    ProfileItemRow("Guru Pembimbing", currentUser.guruPembimbing, Icons.Default.School)
                    ProfileItemRow("Status Akun", currentUser.status, Icons.Default.CheckCircle)
                }
            }
        }

        // Fast Role / Account Switcher for testing (Masterplan Section 4, 40-42)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwitchAccount, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ganti Peran / Akun Pengguna",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldDark
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Beralih peran secara instan untuk mencoba hak akses Siswa, Guru, atau Admin:",
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                allUsers.forEach { user ->
                    val isSelected = user.uid == currentUser.uid
                    Surface(
                        onClick = { viewModel.switchUser(user) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) EmeraldContainer.copy(alpha = 0.5f) else SlateSurfaceVariant,
                        border = if (isSelected) BorderStroke(1.5.dp, EmeraldPrimary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = user.nama,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SlateTextPrimary
                                )
                                Text(
                                    text = "${user.role} • ${user.email}",
                                    fontSize = 11.sp,
                                    color = SlateTextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Aktif", tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // About Application Card (Masterplan Section 47)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = appConfig.appName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldDark
                )
                Text(
                    text = appConfig.tagline,
                    fontSize = 12.sp,
                    color = SlateTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Versi ${appConfig.versi} • ${appConfig.kontak}",
                    fontSize = 11.sp,
                    color = SlateTextSecondary
                )
            }
        }

        // Tombol Keluar (Logout)
        OutlinedButton(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusError),
            border = BorderStroke(1.dp, StatusError.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = StatusError, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Keluar Akun (Kembali ke Halaman Login)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ProfileItemRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, color = SlateTextSecondary)
        }
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = SlateTextPrimary
        )
    }
}
