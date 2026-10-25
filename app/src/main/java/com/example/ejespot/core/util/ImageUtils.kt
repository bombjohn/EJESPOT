package com.example.ejespot.core.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * Crea un archivo vacío en la caché de la app y devuelve su URI a través del
 * [FileProvider], listo para entregárselo a la cámara del dispositivo.
 *
 * La autoridad (`${packageName}.fileprovider`) debe coincidir con la declarada
 * en el `AndroidManifest.xml`.
 */
fun createTempImageUri(context: Context): Uri {
    val tempFile = File.createTempFile("spot_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

/**
 * Carga el [uri] como un `Bitmap` listo para mostrar en Compose.
 *
 * Se usa para previsualizar la imagen que el usuario eligió de la galería o
 * tomó con la cámara. Devuelve `null` si el contenido no se puede leer.
 */
fun Uri.toImageBitmap(context: Context): android.graphics.Bitmap? = runCatching {
    context.contentResolver.openInputStream(this)?.use { stream ->
        android.graphics.BitmapFactory.decodeStream(stream)
    }
}.getOrNull()
