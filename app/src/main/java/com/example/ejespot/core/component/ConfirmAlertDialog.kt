package com.example.ejespot.core.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.ejespot.R

/**
 * Diálogo de confirmación reutilizable (guía 10).
 *
 * Solo informa y devuelve la decisión del usuario: quien lo usa decide qué
 * hacer en `onConfirm` y `onDismiss`. Se usa, por ejemplo, para confirmar la
 * salida de un formulario antes de perder los datos escritos.
 */
@Composable
fun ConfirmAlertDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = stringResource(R.string.common_accept),
    dismissText: String = stringResource(R.string.common_cancel)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        }
    )
}
