package com.example.ejespot.features.dashboard.profile

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.AppPermission
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun PermissionsScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val c = ejeSpotColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            .verticalScroll(rememberScrollState())
    ) {
        ProfileTopBar(title = stringResource(R.string.permissions_title), onBack = onNavigateBack)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = c.surfaceVariant,
                border = BorderStroke(1.dp, c.outlineSoft),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.permissions_info),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.5.sp,
                    color = c.textSoft,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(13.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            uiState.permissions.forEachIndexed { index, permission ->
                val permissionTitle = stringResource(permission.titleRes)
                // Mensajes resueltos aquí (contexto @Composable) para poder
                // mostrarlos desde la corrutina del snackbar.
                val enabledMessage = stringResource(
                    R.string.permissions_enabled_snackbar,
                    permissionTitle
                )
                val disabledMessage = stringResource(
                    R.string.permissions_disabled_snackbar,
                    permissionTitle
                )
                PermissionCard(
                    permission = permission,
                    onToggle = {
                        viewModel.togglePermission(permission.id)
                        coroutineScope.launch {
                            snackbarHostState?.showSnackbar(
                                if (!permission.isGranted) {
                                    enabledMessage
                                } else {
                                    disabledMessage
                                }
                            )
                        }
                    }
                )
                if (index < uiState.permissions.lastIndex) {
                    Spacer(modifier = Modifier.height(11.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = c.surface),
                border = BorderStroke(1.dp, c.outline)
            ) {
                Text(
                    text = stringResource(R.string.permissions_open_android_settings),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = c.successText
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.permissions_note),
                fontFamily = ManropeFontFamily,
                fontSize = 10.sp,
                color = c.placeholder,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun PermissionCard(
    permission: AppPermission,
    onToggle: () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        color = c.surface,
        border = BorderStroke(1.dp, if (permission.isGranted) c.successBg else c.outlineSoft),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (permission.isGranted) c.successBg else c.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = permission.emoji, fontSize = 19.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(permission.titleRes),
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(permission.descriptionRes),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.5.sp,
                    color = c.placeholder,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (permission.isGranted) c.successBg else c.surfaceVariant
                ) {
                    Text(
                        text = if (permission.isGranted) stringResource(R.string.permissions_granted) else stringResource(R.string.permissions_not_granted),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        color = if (permission.isGranted) c.successText else c.placeholder,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                    )
                }
            }

            Switch(
                checked = permission.isGranted,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EjeSpotPrimary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFD9CFC0)
                )
            )
        }
    }
}
