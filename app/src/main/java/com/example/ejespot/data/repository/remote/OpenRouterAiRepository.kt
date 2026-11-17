package com.example.ejespot.data.repository.remote

import com.example.ejespot.domain.model.AiMessage
import com.example.ejespot.domain.repository.AiAssistantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Implementación del servicio de Inteligencia Artificial mediante OpenRouter.
 *
 * Utiliza modelos conversacionales avanzados a través de la API abierta de OpenRouter.
 * Cuenta con fallback local inteligente en caso de falta de conectividad.
 */
class OpenRouterAiRepository(
    private var apiKey: String = DEFAULT_API_KEY
) : AiAssistantRepository {

    companion object {
        private const val OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"
        // Modelo gratuito de alta velocidad en OpenRouter
        private const val MODEL_NAME = "meta-llama/llama-3.2-3b-instruct:free"

        // Clave de demo para OpenRouter (se puede sobreescribir desde la UI)
        private const val DEFAULT_API_KEY = "sk-or-v1-demo-ejespot-key"
    }

    fun updateApiKey(newKey: String) {
        if (newKey.isNotBlank()) {
            apiKey = newKey.trim()
        }
    }

    override suspend fun askGuide(
        prompt: String,
        history: List<AiMessage>,
        spotContext: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemInstruction = buildSystemPrompt(spotContext)

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemInstruction)
                })
                // Agregar últimos 4 mensajes del historial para contexto
                history.takeLast(4).forEach { msg ->
                    put(JSONObject().apply {
                        put("role", if (msg.isUser) "user" else "assistant")
                        put("content", msg.text)
                    })
                }
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val requestBody = JSONObject().apply {
                put("model", MODEL_NAME)
                put("messages", messagesArray)
                put("temperature", 0.7)
                put("max_tokens", 450)
            }

            val url = URL(OPENROUTER_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 12000
                readTimeout = 18000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Authorization", "Bearer $apiKey")
                setRequestProperty("HTTP-Referer", "https://github.com/bombjohn/EJESPOT")
                setRequestProperty("X-Title", "EjeSpot Android App")
            }

            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
                    reader.readText()
                }
                val jsonResponse = JSONObject(responseText)
                val choices = jsonResponse.getJSONArray("choices")
                if (choices.length() > 0) {
                    val content = choices.getJSONObject(0).getJSONObject("message").getString("content")
                    return@withContext Result.success(content.trim())
                }
            }

            // Si OpenRouter responde con error o rate-limit, usamos el generador local inteligente
            val fallbackResponse = generateLocalSmartFallback(prompt, spotContext)
            Result.success(fallbackResponse)
        } catch (e: Exception) {
            // Fallback sin fallos ante problemas de conexión
            val fallbackResponse = generateLocalSmartFallback(prompt, spotContext)
            Result.success(fallbackResponse)
        }
    }

    private fun buildSystemPrompt(spotContext: String?): String {
        val base = "Eres 'Guía Cafetero IA', el asistente turístico virtual de la app EjeSpot en Colombia. " +
            "Tu misión es guiar a turistas y locales en el Paisaje Cultural Cafetero (Quindío, Risaralda y Caldas). " +
            "Responde de forma amable, entusiasta y hospitalaria, con expresiones cafeteras sutiles ('¡Hola parcero!', 'buena cosecha', 'un tinto de bienvenida'). " +
            "Recomienda planes, transporte típico (Willys), horarios recomendados, clima de montaña, gastronomía (trucha al ajillo, patacón, arepa de choclo) y café especial. " +
            "Sé conciso, estructurado con viñetas cuando des itinerarios."

        return if (!spotContext.isNullOrBlank()) {
            "$base Contexto del lugar actual que el usuario está consultando: $spotContext."
        } else {
            base
        }
    }

    /**
     * Fallback contextual que garantiza respuestas turísticas de alta calidad
     * incluso si el dispositivo no tiene internet o se agota la cuota del API.
     */
    private fun generateLocalSmartFallback(prompt: String, spotContext: String?): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("extremo") || lower.contains("aventura") || lower.contains("parapente") ||
                lower.contains("canopy") || lower.contains("tirolesa") || lower.contains("rafting") ||
                lower.contains("deporte") || lower.contains("adrenalina") || lower.contains("torrentismo") -> """
¡Para los amantes de la adrenalina y la aventura, el Eje Cafetero es un paraíso natural! 🧗‍♂️🪂

⛰️ **Top 5 planes extremos imperdibles:**
1. **Parapente en Buenavista o Calarcá:** Vuelo térmico sobrevolando los cañones del río Barragán y cafetales infinitos con instructores certificados.
2. **Canopy y Tirolinas gigantes:** Deslízate entre copas de niebla en la Finca San Vicente (Santa Rosa) o en el Bosque del Samán (Alcalá).
3. **Balsaje y Rafting en el Río La Vieja (Quimbaya):** Navega rápidos de clase II y III rodeado de guaduales, monos aulladores y garzas.
4. **Torrentismo y Rappel en cascadas:** Descenso por muros de agua en la Cascada Los Frailes (Pereira) o en cascadas vírgenes de Santa Bárbara.
5. **Ascenso de Alta Montaña al PNN Los Nevados:** Trekking hacia el glaciar del Nevado Santa Isabel o paramillo del Quindío (requiere guía de alta montaña y ropa térmica de tres capas).

🧤 **Equipo sugerido:** Calzado con suela de tracción, ropa de secado rápido, bloqueador biodegradable y muda de cambio completa.
            """.trimIndent()

            lower.contains("comida") || lower.contains("comer") || lower.contains("gastronom") ||
                lower.contains("restaurante") || lower.contains("plato") || lower.contains("trucha") ||
                lower.contains("bandeja") || lower.contains("chorizo") || lower.contains("almuerzo") ||
                lower.contains("cena") || lower.contains("arepa") -> """
¡La cocina del Eje Cafetero es pura tradición y sabor montañero! 🍽️😋

🍲 **Platos típicos que debes probar:**
1. **Trucha arcoíris al ajillo:** El plato estrella de Salento, servida con un patacón gigante crujiente y ensalada fresca.
2. **Chorizo Santarrosano original:** En Santa Rosa de Cabal, elaborado artesanalmente sin conservantes, acompañado de arepa de mote y limón mandarino.
3. **Bandeja Paisa / Frijolada montañera:** Fríjoles cargamanto calados con pezuña, chicharrón crocante, carne molida, plátano maduro, huevo frito y aguacate.
4. **Arepa de Chócolo con quesito:** Asada a la brasa con mantequilla de campo, infaltable a orilla de carretera.
5. **Postres tradicionales:** Arequipe con brevas, forcha, solteritas y café frío artesanal.
            """.trimIndent()

            lower.contains("cocora") || lower.contains("salento") -> """
¡Qué gran destino, parcero! 🌴 Salento y el Valle de Cocora son el corazón del Eje Cafetero.

☕ **Tips del Guía Cafetero:**
1. **Transporte:** Toma un Jeep Willys tradicional en la plaza principal de Salento hacia Cocora ($4.500 COP aprox).
2. **Mejor hora:** Llega a Cocora antes de las 9:00 AM para evitar neblina espesa y disfrutar las palmas de cera con buena luz.
3. **Calzado:** Botas pantaneras o calzado de trekking con buen agarre (el sendero de Acaime suele tener barro).
4. **Comida típica:** En Salento pide trucha al ajillo con patacón gigante en la Calle Real.
            """.trimIndent()

            lower.contains("termal") || lower.contains("santa rosa") -> """
¡Plan de descanso total! ♨️ Los Termales de Santa Rosa de Cabal son aguas termales medicinales brotando en plena cordillera.

🧖‍♂️ **Recomendaciones clave:**
- **Horario ideal:** La tarde-noche (5:00 PM en adelante) para sentir el contraste del agua caliente con el frío de la noche.
- **Ropa:** Lleva toalla y vestido de baño oscuro (los minerales naturales pueden teñir telas blancas).
- **Gastronomía:** Antes de subir a los termales, pasa por Santa Rosa a probar el famoso Chorizo Santarrosano original.
            """.trimIndent()

            lower.contains("café") || lower.contains("cafe") || lower.contains("tinto") || lower.contains("catación") || lower.contains("barista") -> """
¡Estás en la tierra del mejor café suave del mundo! ☕

✨ **Experiencias recomendadas en EjeSpot:**
- **Café San Alberto (Buenavista):** Bautizo cafetero con vista panorámica de 360° sobre el valle.
- **Fincas Tradicionales en Filandia o Chinchiná:** Recorre el cafetal, recolecta granos maduros y prueba métodos de filtrado (Chemex, Prensa Francesa y Dripper).
- **Tip del barista:** Pide tu café sin azúcar para apreciar las notas a frutos rojos, chocolate y panela características del grano arábigo regional.
            """.trimIndent()

            lower.contains("itinerario") || lower.contains("ruta") || lower.contains("día") || lower.contains("dias") -> """
¡Te armé esta ruta soñada de 3 días por el Eje Cafetero! 🎒🗺️

📅 **Día 1 - Tradición & Palmas:**
- Mañana: Caminata por el Valle de Cocora entre palmas de cera gigantes.
- Tarde: Recorrido fotográfico por la Calle Real de Salento y mirador Alto de la Cruz.

📅 **Día 2 - Arquitectura & Miradores:**
- Mañana: Pueblo de Filandia, subida al Mirador Colina Iluminada y recorrido por cestería tradicional.
- Tarde: Tarde de cafés especiales en Buenavista.

📅 **Día 3 - Termalismo & Naturaleza:**
- Mañana: Senderismo en la Cascada de los Frailes (Pereira).
- Tarde/Noche: Relax total en los Termales de Santa Rosa de Cabal.
            """.trimIndent()

            lower.contains("pueblo") || lower.contains("filandia") || lower.contains("pijao") ||
                lower.contains("marsella") || lower.contains("salamina") || lower.contains("arquitectura") -> """
¡Los pueblos del Paisaje Cultural Cafetero son joyas vivas de arquitectura colonial en bahareque y madera colorida! 🏘️🌸

📍 **Pueblos recomendados para tu ruta:**
1. **Filandia (Quindío):** Calles llenas de balcones floridos, mirador panorámico 'Colina Iluminada' y talleres de artesanos en bejuco.
2. **Pijao (Quindío):** El primer municipio *Cittaslow* de Latinoamérica; un remanso de paz para caminar despacio, conversar con abuelos cafeteros y tomar café de origen.
3. **Marsella (Risaralda):** Casa de la Cultura señorial, Jardín Botánico Alejandro Humboldt y vistas cafeteras espectaculares.
4. **Salamina (Caldas):** Ciudad Luz de Colombia, famosa por sus tallas en madera, portones coloniales y atardeceres mágicos.
            """.trimIndent()

            lower.contains("transporte") || lower.contains("llegar") || lower.contains("willys") ||
                lower.contains("chiva") || lower.contains("bus") || lower.contains("aeropuerto") -> """
¡Moverte por el Eje Cafetero es fácil, pintoresco y muy seguro! 🚙💨

🚌 **Consejos de transporte:**
- **Jeep Willys tradicional:** Salen de las plazas centrales de los pueblos hacia veredas y miradores. Si tienes espíritu aventurero, ¡pide viajar 'en el estribo'!
- **Buses intermunicipales:** Terminales de Armenia, Pereira y Manizales tienen salidas cada 15 a 30 minutos conectando toda la región.
- **Aeropuertos:** Aeropuerto El Edén (AXM) en Armenia y Matecaña (PEI) en Pereira reciben vuelos directos nacionales e internacionales diarios.
            """.trimIndent()

            !spotContext.isNullOrBlank() -> """
¡Excelente elección! Sobre **$spotContext**: 📍

Es uno de los puntos más destacados en EjeSpot. Te recomiendo visitarlo temprano para disfrutar la vista con calma, llevar cámara para inmortalizar el paisaje verde y probar los productos locales de la zona. 

¿Te gustaría que te recomiende lugares cercanos para armar un circuito en el mapa? ☕
            """.trimIndent()

            else -> """
¡Excelente consulta sobre "$prompt", caminante! 🌿☕

Para disfrutar al máximo este plan en el Paisaje Cultural Cafetero:
1. **Busca en el mapa de EjeSpot:** Explora los puntos turísticos y miradores filtrados por municipio (Quindío, Risaralda y Caldas).
2. **Mejor horario:** Las primeras horas de la mañana (7:00 AM - 11:00 AM) son ideales para salidas al aire libre antes de las lloviznas de tarde.
3. **Transporte:** Apóyate en los Jeeps Willys y chivas tradicionales desde las plazas principales de cada pueblo.
4. **Comunidad:** Lee los comentarios y recomendaciones de otros viajeros en la ficha detallada de cada spot.

💡 *Nota:* Puedes configurar tu API Key de OpenRouter tocando el icono de la llave (🔑) arriba a la derecha para chatear con IA generativa abierta en vivo. ¿Deseas información de algún municipio en específico?
            """.trimIndent()
        }
    }
}
