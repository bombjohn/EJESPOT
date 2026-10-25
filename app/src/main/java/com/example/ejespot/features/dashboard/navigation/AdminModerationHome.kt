package com.example.ejespot.features.dashboard.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.ModerationReport
import com.example.ejespot.domain.model.ProposalStatus
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.features.dashboard.profile.ProfileViewModel
import com.example.ejespot.features.dashboard.profile.components.ProfileEmptyState
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Panel de moderación (rol ADMIN). Muestra la cola de reportes
 * y propuestas de la comunidad con acciones interactivas para
 * editar, validar, aprobar o descartar contenido.
 */
@Composable
fun AdminModerationHome(
    padding: PaddingValues,
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateToSpotDetail: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val c = ejeSpotColors()
    val coroutineScope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0: Reportes, 1: Propuestas
    var resolvingReport by remember { mutableStateOf<ModerationReport?>(null) }
    var approvingProposal by remember { mutableStateOf<ProposedSpot?>(null) }

    val pendingProposalsCount = remember(uiState.proposals) {
        uiState.proposals.count { it.status == ProposalStatus.PENDING }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
    ) {
        ProfileTopBar(title = stringResource(R.string.moderation_title), onBack = null)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.moderation_subtitle),
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                color = c.textSoft
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Estadísticas generales del panel
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = c.surface,
                border = BorderStroke(1.dp, c.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AdminStat(
                        value = uiState.pendingModeration.toString(),
                        label = stringResource(R.string.moderation_pending)
                    )
                    AdminStat(
                        value = pendingProposalsCount.toString(),
                        label = "Nuevos Spots"
                    )
                    AdminStat(
                        value = uiState.reviews.size.toString(),
                        label = stringResource(R.string.profile_stats_reviews)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pestañas interactivas de moderación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabChip(
                    text = "🚩 Reportes (${uiState.pendingModeration})",
                    isSelected = activeTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 0 }
                )
                FilterTabChip(
                    text = "✨ Propuestas ($pendingProposalsCount)",
                    isSelected = activeTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 1 }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Contenido según la pestaña seleccionada
        if (activeTab == 0) {
            // Pestaña de Reportes de Contenido
            if (uiState.moderationQueue.isEmpty()) {
                ProfileEmptyState(
                    emoji = "🛡️",
                    title = stringResource(R.string.moderation_empty_title),
                    message = stringResource(R.string.moderation_empty_message)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    items(uiState.moderationQueue, key = { it.id }) { report ->
                        ModerationCard(
                            report = report,
                            onInspect = {
                                report.spotId?.let { id -> onNavigateToSpotDetail?.invoke(id) }
                            },
                            onResolve = {
                                resolvingReport = report
                            }
                        )
                    }
                }
            }
        } else {
            // Pestaña de Propuestas de Nuevos Spots
            val proposals = uiState.proposals
            if (proposals.isEmpty()) {
                ProfileEmptyState(
                    emoji = "📍",
                    title = "Sin propuestas pendientes",
                    message = "Los usuarios no tienen spots esperando revisión en este momento."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    items(proposals, key = { it.id }) { proposal ->
                        ProposalModerationCard(
                            proposal = proposal,
                            onInspect = {
                                proposal.spotId?.let { id -> onNavigateToSpotDetail?.invoke(id) }
                            },
                            onApprove = {
                                approvingProposal = proposal
                            },
                            onReject = {
                                viewModel.rejectProposal(proposal.id)
                                coroutineScope.launch {
                                    snackbarHostState?.showSnackbar("Propuesta de '${proposal.name}' desestimada")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO INTERACTIVO: RESOLVER REPORTE DE LA COMUNIDAD
    resolvingReport?.let { report ->
        ResolveReportDialog(
            report = report,
            onDismiss = { resolvingReport = null },
            onInspectSpot = {
                val spotId = report.spotId
                resolvingReport = null
                if (spotId != null) {
                    onNavigateToSpotDetail?.invoke(spotId)
                }
            },
            onConfirm = { actionTitle, note ->
                viewModel.resolveModerationReport(report.id)
                resolvingReport = null
                coroutineScope.launch {
                    val message = "Reporte de '${report.spotName}' resuelto: $actionTitle" +
                            if (note.isNotBlank()) " ($note)" else ""
                    snackbarHostState?.showSnackbar(message)
                }
            }
        )
    }

    // DIÁLOGO INTERACTIVO: APROBAR PROPUESTA DE NUEVO SPOT
    approvingProposal?.let { proposal ->
        ApproveProposalDialog(
            proposal = proposal,
            onDismiss = { approvingProposal = null },
            onConfirm = {
                viewModel.approveProposal(proposal.id)
                approvingProposal = null
                coroutineScope.launch {
                    snackbarHostState?.showSnackbar("¡Spot '${proposal.name}' aprobado y publicado (+100 XP para el autor)!")
                }
            }
        )
    }
}

@Composable
private fun FilterTabChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (isSelected) EjeSpotPrimary else c.surface,
        border = BorderStroke(1.dp, if (isSelected) EjeSpotPrimary else c.outline),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontFamily = ManropeFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                color = if (isSelected) Color.White else c.text
            )
        }
    }
}

@Composable
private fun AdminStat(value: String, label: String) {
    val c = ejeSpotColors()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = FrauncesFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = EjeSpotPrimary
        )
        Text(
            text = label,
            fontFamily = ManropeFontFamily,
            fontSize = 10.sp,
            color = c.textSoft
        )
    }
}

@Composable
private fun ModerationCard(
    report: ModerationReport,
    onInspect: () -> Unit,
    onResolve: () -> Unit
) {
    val c = ejeSpotColors()
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outlineSoft),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🚩", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = report.spotName,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = c.text,
                    modifier = Modifier.weight(1f)
                )

                if (report.spotId != null) {
                    IconButton(
                        onClick = onInspect,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "Ver spot",
                            tint = c.textSoft,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = c.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.moderation_reason, stringResource(report.reasonRes)),
                    fontFamily = ManropeFontFamily,
                    fontSize = 11.sp,
                    color = c.secondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.moderation_reported_by, report.reporter, stringResource(report.timeLabelRes)),
                    fontFamily = ManropeFontFamily,
                    fontSize = 10.sp,
                    color = c.placeholder,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onResolve,
                    shape = RoundedCornerShape(100.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Resolver",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ProposalModerationCard(
    proposal: ProposedSpot,
    onInspect: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val c = ejeSpotColors()
    val isPending = proposal.status == ProposalStatus.PENDING

    Surface(
        shape = RoundedCornerShape(15.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outlineSoft),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📍", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(7.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = proposal.name,
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = c.text
                    )
                    Text(
                        text = "${proposal.location} · ${stringResource(proposal.submittedLabelRes)}",
                        fontFamily = ManropeFontFamily,
                        fontSize = 10.5.sp,
                        color = c.textSoft
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isPending) Color(0xFFFBF0DB) else Color(0xFFD7E8DC)
                ) {
                    Text(
                        text = if (isPending) "Pendiente" else "Aprobado",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isPending) Color(0xFFB35A2B) else Color(0xFF2C5640),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = proposal.customNote ?: if (proposal.noteRes != 0) stringResource(proposal.noteRes) else "",
                fontFamily = ManropeFontFamily,
                fontSize = 11.sp,
                color = c.textSoft,
                lineHeight = 15.sp
            )

            if (isPending) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE5DDD0))
                    ) {
                        Text(
                            text = "Descartar",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            color = Color(0xFF887D70)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C5640))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Aprobar spot (+100 XP)",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo interactivo para resolver un reporte con opciones
 * reales de corrección de datos y notas de moderación.
 */
@Composable
private fun ResolveReportDialog(
    report: ModerationReport,
    onDismiss: () -> Unit,
    onInspectSpot: () -> Unit,
    onConfirm: (actionTitle: String, note: String) -> Unit
) {
    val c = ejeSpotColors()
    var selectedActionIndex by remember { mutableStateOf(0) }
    var correctedHours by remember { mutableStateOf("8:00 a.m. - 5:00 p.m.") }
    var correctedPrice by remember { mutableStateOf("Entrada libre / Gratuito") }
    var moderatorNote by remember { mutableStateOf("Se verificó la información oficial y se ajustaron los datos.") }

    val actions = listOf(
        "✏️ Corregir datos del lugar" to "Se corrigen horarios, precios o ficha técnica del spot.",
        "✅ Validar información actual" to "La información actual está confirmada y el spot cumple normas.",
        "🚫 Desactivar / Ocultar spot" to "El lugar está cerrado permanentemente o es duplicado.",
        "↩️ Desestimar reporte" to "Falsa alarma o reporte sin fundamento."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🛡️", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Resolver Reporte",
                        fontFamily = FrauncesFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = c.text
                    )
                    Text(
                        text = report.spotName,
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = c.textSoft
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // Tarjeta de contexto del reporte
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = c.secondaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Motivo: ${stringResource(report.reasonRes)}",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = c.secondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Reportado por ${report.reporter} · ${stringResource(report.timeLabelRes)}",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            color = c.textSoft
                        )
                    }
                }

                if (report.spotId != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(
                        onClick = onInspectSpot,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(
                            text = "👁️ Ver publicación en la app",
                            fontFamily = ManropeFontFamily,
                            fontSize = 11.sp,
                            color = EjeSpotPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "¿Qué acción deseas aplicar?",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = c.text
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Selector de opciones de resolución
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    actions.forEachIndexed { index, (title, desc) ->
                        val isSelected = selectedActionIndex == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) c.secondaryContainer else c.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) c.secondary else c.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedActionIndex = index }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                                Text(
                                    text = title,
                                    fontFamily = ManropeFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) c.secondary else c.text
                                )
                                Text(
                                    text = desc,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 10.sp,
                                    color = c.textSoft
                                )
                            }
                        }
                    }
                }

                // Campos de edición interactiva si se elige "Corregir datos"
                if (selectedActionIndex == 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Datos ajustados por moderación:",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = c.text
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = correctedHours,
                        onValueChange = { correctedHours = it },
                        label = { Text("Horario verificado", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = correctedPrice,
                        onValueChange = { correctedPrice = it },
                        label = { Text("Precio verificado", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = moderatorNote,
                    onValueChange = { moderatorNote = it },
                    label = { Text("Nota de resolución", fontSize = 10.sp) },
                    singleLine = false,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val actionName = when (selectedActionIndex) {
                        0 -> "Información corregida ($correctedHours · $correctedPrice)"
                        1 -> "Información verificada y confirmada"
                        2 -> "Publicación ocultada de la vista pública"
                        else -> "Reporte desestimado"
                    }
                    onConfirm(actionName, moderatorNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
            ) {
                Text(
                    text = "Aplicar Resolución",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontFamily = ManropeFontFamily,
                    color = c.textSoft,
                    fontSize = 12.sp
                )
            }
        }
    )
}

/**
 * Diálogo de confirmación para aprobar un nuevo spot propuesto por la comunidad.
 */
@Composable
private fun ApproveProposalDialog(
    proposal: ProposedSpot,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val c = ejeSpotColors()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aprobar Nuevo Spot",
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = c.text
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "¿Confirmas que el spot '${proposal.name}' en ${proposal.location} cumple con las normas y debe publicarse en el feed oficial?",
                    fontFamily = ManropeFontFamily,
                    fontSize = 12.5.sp,
                    color = c.text
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFD7E8DC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🎉 El autor recibirá +100 XP por su contribución verificada.",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF2C5640),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C5640))
            ) {
                Text(
                    text = "Confirmar y Publicar",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    fontFamily = ManropeFontFamily,
                    color = c.textSoft,
                    fontSize = 12.sp
                )
            }
        }
    )
}
