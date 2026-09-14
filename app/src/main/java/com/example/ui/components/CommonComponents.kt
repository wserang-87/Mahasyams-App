package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Notifikasi
import com.example.data.model.User
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahasyamsTopBar(
    currentUser: User,
    unreadNotifCount: Int,
    onRoleSwitchClick: () -> Unit,
    onNotifClick: () -> Unit,
    onLogoutClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(EmeraldContainer)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Mahasyams Logo",
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Mahasyams",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldDark
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مَهَا شَمْس",
                            fontSize = 13.sp,
                            color = AmberAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Seni & Adab Kaligrafi",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Role switcher pill button
            Surface(
                onClick = onRoleSwitchClick,
                shape = RoundedCornerShape(20.dp),
                color = when (currentUser.role) {
                    "ADMIN" -> AmberContainer
                    "GURU" -> SkyContainer
                    else -> EmeraldContainer
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (currentUser.role) {
                        "ADMIN" -> Icons.Default.AdminPanelSettings
                        "GURU" -> Icons.Default.School
                        else -> Icons.Default.Person
                    }
                    val textColor = when (currentUser.role) {
                        "ADMIN" -> OnAmberContainer
                        "GURU" -> OnSkyContainer
                        else -> OnEmeraldContainer
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = "Role",
                        tint = textColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentUser.role,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }

            // Notifications
            IconButton(onClick = onNotifClick) {
                BadgedBox(
                    badge = {
                        if (unreadNotifCount > 0) {
                            Badge { Text(unreadNotifCount.toString()) }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = EmeraldDark
                    )
                }
            }

            // Logout icon button
            if (onLogoutClick != null) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Keluar Akun",
                        tint = SlateTextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun DeadlineBadge(deadlineMs: Long, isCompleted: Boolean = false) {
    val now = System.currentTimeMillis()
    val diff = deadlineMs - now
    val daysRemaining = (diff / (24 * 60 * 60 * 1000L)).toInt()

    val (text, bgColor, textColor) = when {
        isCompleted -> Triple("Selesai", EmeraldContainer, OnEmeraldContainer)
        diff < 0 -> Triple("Deadline Terlewat", StatusError.copy(alpha = 0.15f), StatusError)
        daysRemaining <= 1 -> Triple("Mendekati Deadline (1 Hari)", AmberContainer, OnAmberContainer)
        daysRemaining <= 3 -> Triple("Sedang Berlangsung ($daysRemaining Hari)", SkyContainer, OnSkyContainer)
        else -> Triple("Tersisa $daysRemaining Hari", SlateSurfaceVariant, SlateTextSecondary)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

@Composable
fun CorrectionBadge(status: String, nilai: Int? = null) {
    val isDone = status == "Sudah Dikoreksi"
    val bgColor = if (isDone) EmeraldContainer else AmberContainer
    val textColor = if (isDone) OnEmeraldContainer else OnAmberContainer

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isDone && nilai != null) "$status (Nilai: $nilai)" else status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Default.AutoStories,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(EmeraldContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = SlateTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = SlateTextSecondary,
            lineHeight = 20.sp
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun NotificationDialog(
    notifications: List<Notifikasi>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID"))
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = EmeraldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notifikasi Mahasyams", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Text("Tidak ada notifikasi baru.", color = SlateTextSecondary)
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    notifications.take(5).forEach { notif ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (!notif.isRead) EmeraldContainer.copy(alpha = 0.4f) else SlateSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMarkRead(notif.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = notif.judul,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EmeraldDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = notif.pesan,
                                    fontSize = 12.sp,
                                    color = SlateTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sdf.format(Date(notif.createdAt)),
                                    fontSize = 10.sp,
                                    color = SlateTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = EmeraldPrimary)
            }
        }
    )
}
