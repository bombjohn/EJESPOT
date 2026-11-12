package com.example.ejespot.data.repository.remote

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Servicio de almacenamiento de imágenes en la nube con **Firebase Storage**.
 *
 * Sube la imagen seleccionada por el usuario (cámara o galería) a la
 * carpeta `spot_images/` de Firebase Storage y devuelve la URL pública
 * de descarga que se almacena en el campo `imageUrl` del Spot.
 *
 * Si Firebase Storage no está configurado, devuelve el URI local como
 * fallback para que la app siga funcionando en modo offline / demo.
 */
object CloudImageStorageService {

    private val storage: FirebaseStorage? = try {
        FirebaseStorage.getInstance()
    } catch (_: Exception) {
        null
    }

    /**
     * Sube una imagen a Firebase Storage.
     *
     * @param context Contexto de Android para resolver el ContentProvider del URI.
     * @param localUri URI local de la imagen (content:// o file://).
     * @return URL pública de descarga, o el URI local como string si Firebase no está disponible.
     */
    suspend fun uploadImage(context: Context, localUri: Uri): String {
        if (storage == null) {
            return localUri.toString()
        }
        return try {
            val fileName = "spot_images/${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(fileName)

            // Subir el archivo
            ref.putFile(localUri).await()

            // Obtener la URL de descarga pública
            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        } catch (_: Exception) {
            // Si la subida falla, devolver el URI local como fallback
            localUri.toString()
        }
    }

    /**
     * Sube una imagen desde un array de bytes (útil para Bitmaps comprimidos).
     *
     * @param imageBytes Bytes de la imagen comprimida (JPEG).
     * @param spotId ID del spot para nombrar el archivo.
     * @return URL pública de descarga, o null si Firebase no está disponible.
     */
    suspend fun uploadImageBytes(imageBytes: ByteArray, spotId: String): String? {
        if (storage == null) return null
        return try {
            val fileName = "spot_images/${spotId}_${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child(fileName)
            ref.putBytes(imageBytes).await()
            ref.downloadUrl.await().toString()
        } catch (_: Exception) {
            null
        }
    }

    /** Elimina una imagen de Firebase Storage por su URL. */
    suspend fun deleteImage(imageUrl: String) {
        if (storage == null || !imageUrl.contains("firebase")) return
        try {
            val ref = storage.getReferenceFromUrl(imageUrl)
            ref.delete().await()
        } catch (_: Exception) { /* ignorar */ }
    }
}
