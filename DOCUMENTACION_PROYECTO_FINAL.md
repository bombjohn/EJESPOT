# EJESPOT - DOCUMENTACIÓN TÉCNICA Y FUNCIONAL DEL PROYECTO FINAL

**Materia:** Programación de Aplicaciones Móviles  
**Docente:** Carlos Andrés Flórez Villarraga  
**Framework:** Android con Jetpack Compose y Kotlin  
**Repositorio:** GitHub (EjeSpot)  

---

## 1. Resumen Ejecutivo del Proyecto

**EjeSpot** es una aplicación móvil nativa desarrollada con **Jetpack Compose** y **Kotlin** orientada a la exploración, documentación colaborativa y turismo comunitario en el Paisaje Cultural Cafetero de Colombia (Quindío, Risaralda y Caldas).

La plataforma permite a los usuarios descubrir atractivos turísticos, fincas cafeteras, senderos ecológicos y experiencias gastronómicas; interactuar a través de votaciones de relevancia comunitaria (*"Es importante / Me interesa"*), reseñas con calificaciones y un centro de notificaciones de eventos; planificar sus recorridos mediante un **asistente de Inteligencia Artificial (OpenRouter)**; y visualizar las ubicaciones exactas en un **mapa interactivo**. A su vez, los moderadores precargados garantizan la calidad y veracidad del contenido publicado antes de que aparezca en el feed público.

---

## 2. Arquitectura de Software y Patrones de Diseño

El proyecto sigue los principios de **Clean Architecture** (Arquitectura Limpia) y el patrón **MVI / MVVM** reactivo con Jetpack Compose:

```
app/
 ├── core/                      # Infraestructura transversal
 │    ├── data/                 # Store central reactivo de datos en memoria y cache
 │    ├── session/              # SessionManager (gestión del usuario logueado o invitado)
 │    ├── ui/                   # Componentes visuales reutilizables (SpotImage, etc.)
 │    └── util/                 # Utilidades de imagen, cámara y FileProvider
 ├── domain/                    # Capa de Dominio (independiente de Android UI)
 │    ├── model/                # Modelos (Spot, User, AppNotification, AiMessage, Location)
 │    ├── repository/           # Contratos e interfaces (SpotRepository, ProfileRepository, etc.)
 │    └── usecase/              # Casos de uso de negocio puros:
 │         ├── ApproveProposalUseCase.kt
 │         ├── RejectProposalUseCase.kt
 │         ├── DeleteProposalUseCase.kt
 │         ├── FinalizeProposalUseCase.kt
 │         ├── ReactivateProposalUseCase.kt
 │         ├── UpdateSpotUseCase.kt
 │         ├── ToggleImportanceVoteUseCase.kt
 │         └── AskCoffeeGuideUseCase.kt
 ├── data/                      # Capa de Datos (implementación de repositorios)
 │    └── repository/
 │         ├── LocalSpotRepository.kt
 │         ├── LocalProfileRepository.kt
 │         └── remote/
 │              └── OpenRouterAiRepository.kt
 ├── features/                  # Capa de Presentación (organizada por verticales de negocio)
 │    ├── auth/                 # Login, Registro y Recuperación de Contraseña
 │    ├── dashboard/            # Navegación general, Tabs y Notificaciones
 │    ├── spots/                # Explorar Feed, Mapa interactivo, Crear Spot y Detalle
 │    ├── profile/              # Perfil, Métricas, Gamificación, Ajustes y Moderación
 │    └── ai/                   # Asistente Inteligente "Guía Cafetero IA"
 └── navigation/                # Navegación tipada con Kotlin Serialization (@Serializable)
```

### Características Técnicas Clave de la Arquitectura:
- **Flujos Reactivos:** Empleo de `StateFlow` y `collectAsStateWithLifecycle` / `collectAsState` para que las mutaciones de datos se propaguen instantáneamente a toda la UI.
- **Rutas Tipadas:** Se utiliza la API oficial de Jetpack Navigation Compose con rutas tipadas `@Serializable`, eliminando cadenas de texto propensas a errores.
- **Desacoplamiento:** Los ViewModels consumen casos de uso de dominio (`UseCase`), permitiendo cambiar la fuente de datos (local o remota) sin modificar las pantallas.

---

## 3. Matriz de Cumplimiento de Requisitos (Rúbrica)

| Requisito | Estado | Implementación en EjeSpot |
|---|:---:|---|
| **Rol Moderador (Login, Verificación/Rechazo con motivo, Finalización)** | ✅ 100% | `AdminModerationHome`, `ApproveProposalUseCase`, `RejectProposalUseCase` con diálogo de justificación obligatorio y `FinalizeProposalUseCase`. |
| **Rol Usuario (Registro Gmail, Contraseña min 8 car., Login)** | ✅ 100% | Validaciones estrictas en `RegisterViewModel` y `LoginViewModel`. |
| **Recuperación de Contraseña** | ✅ 100% | `ForgotPasswordScreen` y `ForgotPasswordViewModel` con link por email. |
| **Feed en Lista o Mapa con Filtros** | ✅ 100% | `SpotListScreen` y `MapScreen` con filtrado por categoría (Naturaleza, Café, etc.) y municipio. |
| **CRUD de Publicaciones (Crear, Editar, Borrar, Finalizar)** | ✅ 100% | Pantallas de creación (`CreateSpotScreen`) y detalle (`SpotDetailScreen`) con diálogo de edición y finalización. |
| **Botón "Es importante / Me interesa" (Votar Relevancia)** | ✅ 100% | Botón interactivo con conteo en tiempo real y badge `🔥` en feed y detalle. |
| **Comentarios / Reseñas en Publicaciones** | ✅ 100% | Formulario y listado en `SpotDetailScreen` y `SpotReviewsScreen`. |
| **Detalle Completo (Fotos, GPS, Horario, Precio, Reseñas)** | ✅ 100% | Vista rica con carrusel, información técnica, especialidades y mapa. |
| **Apartado de Notificaciones In-App** | ✅ 100% | `NotificationsScreen` con filtro todas/no leídas, navegación directa al spot y almacenamiento de eventos. |
| **Estadísticas Personales (Dashboard de Métricas)** | ✅ 100% | Tarjetas de conteo de spots Activos, Pendientes y Finalizados en `ProfileScreen`. |
| **Gamificación: 4 Niveles de Usuario e Insignias** | ✅ 100% | Niveles: *Turista*, *Aventurero*, *Explorador Cafetero*, *Embajador Local*; insignias en `GamificationScreen`. |
| **Integración con Inteligencia Artificial** | ✅ 100% | Módulo *Guía Cafetero IA* con OpenRouter API y fallback local inteligente. |
| **Internacionalización (i18n)** | ✅ 100% | Español (`res/values/strings.xml`) e Inglés (`res/values-en/strings.xml`) con selector en Ajustes. |
| **Mapas con Coordenadas Exactas** | ✅ 100% | `osmdroid` (OpenStreetMap) con marcadores reales por municipio y lat/lng. |
| **Almacenamiento de Imágenes en la Nube** | ✅ 100% | `CloudImageStorageService` + `FileProvider` con captura de cámara y galería. |
| **Necesidad Adicional No Trivial** | ✅ 100% | **Modo Invitado**, **Sistema de Denuncia/Reporte de Contenido** y **Spots Guardados**. |

---

## 4. Servicios Externos, Bibliotecas y Justificación Técnica

### 4.1. Servicio de Mapas: `osmdroid` (OpenStreetMap)
- **Biblioteca:** `org.osmdroid:osmdroid-android:6.1.20`
- **Justificación Técnica:** De acuerdo con la pauta del curso, en caso de utilizar un servicio diferente a Mapbox o Google Maps se debe documentar la elección. Se seleccionó **`osmdroid`** debido a que:
  1. Es una solución de código abierto y completamente libre de costos y restricciones de tarjetas de crédito.
  2. Ofrece renderizado nativo de teselas vectoriales y rasterizadas sin requerir cuotas de facturación que bloqueen la evaluación docente.
  3. Soporta marcadores interactivos georreferenciados (`GeoPoint`), superposiciones personalizadas y controles de zoom táctil.

### 4.2. Inteligencia Artificial: OpenRouter API
- **Endpoint:** `https://openrouter.ai/api/v1/chat/completions`
- **Modelo por Defecto:** `meta-llama/llama-3.2-3b-instruct:free`
- **Implementación:** `OpenRouterAiRepository.kt` y `AiAssistantViewModel.kt`.
- **Resiliencia:** Incluye un motor de fallback local que genera respuestas contextuales ricas y recomendaciones turísticas incluso en entornos de prueba sin conectividad a Internet o cuando el límite gratuito de cuota se agota.

### 4.3. Manejo de Imágenes y Almacenamiento en la Nube
- **Mecanismos Utilizados:**
  1. **Firebase Storage & Cloudinary:** Servicio implementado en `CloudImageStorageService` (`FirebaseStorage` con subida a `spot_images/` y soporte de URL pública de descarga `imageUrl`).
  2. **Android FileProvider:** Generación de URIs seguros de contenido para captura directa desde la cámara del dispositivo (`androidx.core.content.FileProvider`).
  3. **Visualización Reactiva:** Componente `SpotImage` con soporte para Bitmaps decodificados en memoria (`toImageBitmap`), URLs remotas, recursos empaquetados e ilustraciones generativas con degradados de café.

### 4.4. Autenticación y Base de Datos (Firebase Auth & Firestore)
- **Firebase Authentication:**
  - Implementado en `FirebaseAuthRepository` con soporte para creación de cuentas (`createUserWithEmailAndPassword`), inicio de sesión (`signInWithEmailAndPassword`) y recuperación de contraseñas mediante link por correo electrónico (`sendPasswordResetEmail`).
  - Fallback local automático en `DemoAuthRepository` para resiliencia en pruebas sin conectividad o claves.
- **Firebase Firestore:**
  - Implementado en `FirestoreSpotRepository`:
    - Colección `spots/`: persistencia de publicaciones con georreferenciación (latitud, longitud), estado de verificación y votos.
    - Subcolección `spots/{id}/reviews/`: almacenamiento de comentarios y calificaciones.
    - Colección `notifications/`: almacenamiento y consulta de notificaciones por usuario (`userId`).
    - Colección `votes/`: registro de votaciones de importancia comunitaria.
- **Persistencia Local:** `androidx.datastore:datastore-preferences` para persistencia de sesión y preferencias de idioma.

### 4.5. Internacionalización (Español e Inglés)
- Se implementaron más de 120 cadenas de texto traducidas en:
  - `app/src/main/res/values/strings.xml` (Español)
  - `app/src/main/res/values-en/strings.xml` (Inglés)
- La configuración del idioma se puede cambiar dinámicamente desde el menú de Ajustes de la aplicación (`LanguageScreen`) preservando las preferencias del usuario.

---

## 5. Credenciales de Prueba Precargadas

Para facilitar la evaluación del docente, la aplicación cuenta con los siguientes usuarios listos para iniciar sesión:

### 👑 Rol Moderador (Admin)
- **Correo:** `admin@ejespot.com`
- **Contraseña:** `admin1234`
- **Acceso:** Panel de Moderación superior con pestañas *Por Revisar*, *Aprobados* y *Rechazados*; capacidad de aprobar propuestas, rechazar con motivo y finalizar spots de cualquier usuario.

### 👤 Rol Usuario Estándar
- **Correo:** `usuario@gmail.com`
- **Contraseña:** `usuario1234`
- **Acceso:** Feed completo, creación y edición de publicaciones, votación de relevancia, reseñas, métricas personales y asistente IA.

### 🥾 Modo Invitado
- **Acceso:** Botón *"Entrar como Invitado"* en la pantalla de bienvenida. Permite explorar spots, ver mapas y consultar el Asistente IA, con indicaciones claras al intentar realizar acciones que requieran cuenta (como publicar o comentar).

---

## 6. Generación y Distribución del APK

### 6.1. Comando de Compilación (Release / Debug)
Para generar el archivo ejecutable (`.apk`) desde la terminal del proyecto:

```powershell
# Configuración del entorno JDK
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# Compilación del APK de depuración o lanzamiento
.\gradlew.bat assembleDebug
# o para release:
.\gradlew.bat assembleRelease
```

### 6.2. Ubicación de Salida del APK
- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK (Firmado y listo para instalar):** `app/build/outputs/apk/release/app-release.apk`

### 6.3. Distribución en Firebase App Distribution
1. Acceder a [Firebase Console](https://console.firebase.google.com/) > Proyecto EjeSpot.
2. Ir a **Release & Monitor** > **App Distribution**.
3. Arrastrar y soltar el archivo `.apk` generado.
4. Agregar el grupo de evaluadores (testers) o el correo del docente para descarga directa e instalación en el dispositivo Android.
