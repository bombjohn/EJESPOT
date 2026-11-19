package com.example.ejespot.features.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ejespot.R
import com.example.ejespot.domain.model.AiMessage
import com.example.ejespot.ui.theme.*
import java.util.regex.Pattern

@Composable
fun AiAssistantScreen(
    spotContext: String? = null,
    padding: PaddingValues = PaddingValues(),
    onNavigateBack: () -> Unit,
    viewModel: AiAssistantViewModel = viewModel()
) {
    val c = ejeSpotColors()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(spotContext) {
        if (!spotContext.isNullOrBlank()) {
            viewModel.setSpotContext(spotContext)
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EjeSpotBackground)
            .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
            // Con edge-to-edge, el teclado tapa el campo de texto si no se
            // reserva su altura (targetSdk 37).
            .imePadding()
    ) {
        // --- Top Bar: Botón Volver + Título + Badge de IA ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = c.surface,
            border = BorderStroke(1.dp, c.outlineSoft),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = c.surfaceVariant,
                    border = BorderStroke(1.dp, c.outline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = c.text,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(EjeSpotPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☕", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Guía Cafetero IA",
                            fontFamily = FrauncesFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = c.text
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = EjeSpotGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EjeSpotPrimary)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "En línea • Paisaje Cultural Cafetero",
                            fontFamily = ManropeFontFamily,
                            fontSize = 10.5.sp,
                            color = c.textSoft
                        )
                    }
                }

                Surface(
                    onClick = { viewModel.clearConversation() },
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = c.surfaceVariant,
                    border = BorderStroke(1.dp, c.outline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reiniciar conversación",
                            tint = c.textSoft,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // --- Lista de Mensajes ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                MessageBubble(message = msg)
            }

            if (uiState.isLoading) {
                item(key = "loading_indicator") {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EjeSpotPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("☕", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = c.surface,
                            border = BorderStroke(1.dp, c.outlineSoft)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = EjeSpotPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "El Guía Cafetero está pensando...",
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 12.sp,
                                    color = c.textSoft
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Chips de Preguntas Rápidas ---
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = c.surface,
                    border = BorderStroke(1.dp, c.outline),
                    modifier = Modifier.clickable {
                        viewModel.sendMessage(customPrompt = prompt)
                    }
                ) {
                    Text(
                        text = prompt,
                        fontFamily = ManropeFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.5.sp,
                        color = c.text,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Mostrar fallos de red/API en vez de dejar al usuario sin respuesta.
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                fontFamily = ManropeFontFamily,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        // --- Input de Texto y Botón Enviar ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = c.surface,
            border = BorderStroke(1.dp, c.outlineSoft)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = viewModel::onInputTextChanged,
                    placeholder = {
                        Text(
                            text = "Pregúntale al Guía Cafetero...",
                            fontSize = 13.sp,
                            color = c.placeholder
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EjeSpotPrimary,
                        unfocusedBorderColor = c.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendMessage() },
                    enabled = uiState.inputText.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.inputText.isNotBlank() && !uiState.isLoading) EjeSpotPrimary
                            else c.surfaceVariant
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = if (uiState.inputText.isNotBlank() && !uiState.isLoading) Color.White else c.textSoft,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Convierte texto con formato Markdown básico (**negrita**, *cursiva*, # encabezados)
 * en un [AnnotatedString] de Jetpack Compose sin asteriscos visibles.
 */
@Composable
private fun rememberMarkdownAnnotatedString(rawText: String): AnnotatedString {
    return remember(rawText) {
        val lines = rawText.lines().map { line ->
            val trimmed = line.trimStart()
            when {
                trimmed.startsWith("### ") -> "**" + trimmed.removePrefix("### ").trim() + "**"
                trimmed.startsWith("## ") -> "**" + trimmed.removePrefix("## ").trim() + "**"
                trimmed.startsWith("# ") -> "**" + trimmed.removePrefix("# ").trim() + "**"
                else -> line
            }
        }
        val text = lines.joinToString("\n")

        buildAnnotatedString {
            val pattern = Pattern.compile("(\\*\\*(.*?)\\*\\*)|(\\*([^*\\n]+)\\*)")
            val matcher = pattern.matcher(text)
            var lastIndex = 0

            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()

                if (start > lastIndex) {
                    append(text.substring(lastIndex, start))
                }

                val boldContent = matcher.group(2)
                val italicContent = matcher.group(4)

                if (boldContent != null) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(boldContent)
                    }
                } else if (italicContent != null) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(italicContent)
                    }
                }

                lastIndex = end
            }

            if (lastIndex < text.length) {
                append(text.substring(lastIndex))
            }
        }
    }
}

@Composable
private fun MessageBubble(message: AiMessage) {
    val c = ejeSpotColors()
    val isUser = message.isUser
    val formattedText = rememberMarkdownAnnotatedString(message.text)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(EjeSpotPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("☕", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) EjeSpotPrimary else c.surface,
            border = if (isUser) null else BorderStroke(1.dp, c.outlineSoft),
            shadowElevation = if (isUser) 1.dp else 0.5.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = formattedText,
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = if (isUser) Color.White else c.text,
                lineHeight = 18.5.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}
