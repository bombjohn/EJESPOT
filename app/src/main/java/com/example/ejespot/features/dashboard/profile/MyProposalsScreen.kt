package com.example.ejespot.features.dashboard.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ejespot.R
import com.example.ejespot.domain.model.ProposalStatus
import com.example.ejespot.domain.model.ProposedSpot
import com.example.ejespot.features.dashboard.profile.components.ProfileChip
import com.example.ejespot.features.dashboard.profile.components.ProfileEmptyState
import com.example.ejespot.features.dashboard.profile.components.ProfileTopBar
import com.example.ejespot.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MyProposalsScreen(
    padding: PaddingValues = PaddingValues(),
    viewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState? = null,
    onNavigateBack: () -> Unit,
    onNavigateToMap: () -> Unit,
    onOpenSpot: (String) -> Unit = {},
    onProposeSpot: () -> Unit
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var selectedFilter by remember { mutableStateOf("Todos") }

    var proposalToEdit by remember { mutableStateOf<ProposedSpot?>(null) }
    var proposalToDelete by remember { mutableStateOf<ProposedSpot?>(null) }

    val filtered = remember(uiState.proposals, selectedFilter) {
        when (selectedFilter) {
            "Aprobados" -> uiState.proposals.filter { it.status == ProposalStatus.APPROVED }
            "En revisión" -> uiState.proposals.filter { it.status == ProposalStatus.PENDING }
            else -> uiState.proposals
        }
    }

    val approvedCount = remember(uiState.proposals) {
        uiState.proposals.count { it.status == ProposalStatus.APPROVED }
    }
    val earnedXp = remember(uiState.proposals) {
        uiState.proposals.sumOf { it.xpEarned }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
    ) {
        ProfileTopBar(
            title = stringResource(R.string.my_proposals_title),
            onBack = onNavigateBack,
            trailing = {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotPrimaryContainer
                ) {
                    Text(
                        text = "${uiState.proposals.size}",
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.5.sp,
                        color = EjeSpotPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.my_proposals_subtitle),
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                color = c.textSoft
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                SummaryCard(
                    value = uiState.proposals.size.toString(),
                    label = stringResource(R.string.my_proposals_stat_proposals),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    value = approvedCount.toString(),
                    label = stringResource(R.string.my_proposals_stat_approved),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    value = "+$earnedXp",
                    label = stringResource(R.string.my_proposals_stat_xp),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileChip(
                    text = stringResource(R.string.my_proposals_filter_all),
                    selected = selectedFilter == "Todos",
                    onClick = { selectedFilter = "Todos" }
                )
                ProfileChip(
                    text = stringResource(R.string.my_proposals_filter_approved),
                    selected = selectedFilter == "Aprobados",
                    onClick = { selectedFilter = "Aprobados" }
                )
                ProfileChip(
                    text = stringResource(R.string.my_proposals_filter_pending),
                    selected = selectedFilter == "En revisión",
                    onClick = { selectedFilter = "En revisión" }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filtered.isEmpty()) {
            ProfileEmptyState(
                emoji = "📍",
                title = stringResource(R.string.my_proposals_empty_filter_title),
                message = stringResource(R.string.my_proposals_empty_filter_message)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                items(filtered, key = { it.id }) { proposal ->
                    ProposalCard(
                        proposal = proposal,
                        onOpenSpot = onOpenSpot,
                        onNavigateToMap = onNavigateToMap,
                        onEdit = { proposalToEdit = proposal },
                        onDelete = { proposalToDelete = proposal }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onProposeSpot,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EjeSpotSecondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.my_proposals_propose_new),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // Modal para editar la propuesta
    proposalToEdit?.let { proposal ->
        EditProposalDialog(
            proposal = proposal,
            onDismiss = { proposalToEdit = null },
            onConfirm = { newName, newLocation, newNote ->
                viewModel.editProposal(proposal.id, newName, newLocation, newNote)
                proposalToEdit = null
                coroutineScope.launch {
                    snackbarHostState?.showSnackbar("Propuesta '$newName' actualizada con éxito")
                }
            }
        )
    }

    // Modal de confirmación para eliminar
    proposalToDelete?.let { proposal ->
        DeleteProposalDialog(
            proposal = proposal,
            onDismiss = { proposalToDelete = null },
            onConfirm = {
                viewModel.deleteProposal(proposal.id)
                proposalToDelete = null
                coroutineScope.launch {
                    snackbarHostState?.showSnackbar("Propuesta eliminada")
                }
            }
        )
    }
}

@Composable
private fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    val c = ejeSpotColors()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(13.dp),
        color = c.surface,
        border = BorderStroke(1.dp, c.outline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = EjeSpotPrimary
            )
            Text(
                text = label,
                fontFamily = ManropeFontFamily,
                fontSize = 9.5.sp,
                color = c.textSoft
            )
        }
    }
}

@Composable
private fun ProposalCard(
    proposal: ProposedSpot,
    onOpenSpot: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isApproved = proposal.status == ProposalStatus.APPROVED
    val c = ejeSpotColors()

    val submittedText = proposal.customSubmittedLabel
        ?: if (proposal.submittedLabelRes != 0) stringResource(proposal.submittedLabelRes) else "Reciente"

    val noteText = proposal.customNote
        ?: if (proposal.noteRes != 0) stringResource(proposal.noteRes) else ""

    Surface(
        shape = RoundedCornerShape(15.dp),
        color = c.surface,
        border = BorderStroke(
            1.dp,
            if (isApproved) c.successBg else c.tertiaryContainer
        ),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isApproved) "✅" else "⏳",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = proposal.name,
                    fontFamily = FrauncesFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = c.text,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isApproved) c.successBg else c.secondaryContainer
                ) {
                    Text(
                        text = if (isApproved) stringResource(R.string.my_proposals_status_approved) else stringResource(R.string.my_proposals_status_pending),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        color = if (isApproved) c.successText else c.secondary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.my_proposals_location, proposal.location, submittedText),
                fontFamily = ManropeFontFamily,
                fontSize = 10.5.sp,
                color = c.placeholder
            )

            if (noteText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isApproved) c.successBg else c.tertiaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = noteText,
                        fontFamily = ManropeFontFamily,
                        fontSize = 11.sp,
                        color = if (isApproved) c.successText else c.textSoft,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = EjeSpotPrimaryContainer
                ) {
                    Text(
                        text = if (proposal.xpEarned > 0) stringResource(R.string.my_proposals_xp_earned, proposal.xpEarned) else stringResource(R.string.my_proposals_xp_pending),
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 9.5.sp,
                        color = EjeSpotPrimary,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Editar
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar propuesta",
                            tint = EjeSpotPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Botón Eliminar
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Eliminar propuesta",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Botón Ver Spot o Mapa
                    OutlinedButton(
                        onClick = {
                            val spotId = proposal.spotId
                            if (spotId != null) onOpenSpot(spotId) else onNavigateToMap()
                        },
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier.height(30.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = c.surface),
                        border = BorderStroke(1.dp, c.outline)
                    ) {
                        Text(
                            text = if (proposal.spotId != null) stringResource(R.string.my_reviews_view_spot) else stringResource(R.string.my_proposals_view_map),
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = c.successText
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProposalDialog(
    proposal: ProposedSpot,
    onDismiss: () -> Unit,
    onConfirm: (name: String, location: String, note: String) -> Unit
) {
    var name by remember { mutableStateOf(proposal.name) }
    var location by remember { mutableStateOf(proposal.location) }
    val initialNote = proposal.customNote
        ?: if (proposal.noteRes != 0) stringResource(proposal.noteRes) else ""
    var note by remember { mutableStateOf(initialNote) }
    val isFormValid = name.isNotBlank() && location.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar punto propuesto",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del lugar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Ubicación (ej: Salento, Quindío)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota o descripción de la propuesta") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isFormValid) {
                        onConfirm(name, location, note)
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(containerColor = EjeSpotPrimary)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun DeleteProposalDialog(
    proposal: ProposedSpot,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¿Eliminar propuesta?",
                fontFamily = FrauncesFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas eliminar '${proposal.name}'? Se quitará de tu lista de puntos propuestos y de la cola de moderación.",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
