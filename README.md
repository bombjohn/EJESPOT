# EjeSpot

App Android (Kotlin + Jetpack Compose) para descubrir y proponer puntos de interés en el Eje Cafetero.

## Estructura del proyecto

```
app/
├── google-services.json          Configuración de Firebase (no es código)
├── src/main/
│   ├── java/com/example/ejespot/
│   │   ├── MainActivity.kt           Activity raíz
│   │   ├── EjeSpotApplication.kt     Inicialización de servicios globales
│   │   ├── navigation/               Navegación principal (login → dashboard)
│   │   ├── core/                     Sesión, tema, utilidades y store compartido
│   │   │   ├── component/            Componentes genéricos (diálogos, pickers)
│   │   │   ├── data/                 SpotStore y datos quemados de demo
│   │   │   ├── session/              SessionManager, AppearanceSettings
│   │   │   ├── settings/             Controlador de idioma
│   │   │   ├── ui/                   Componentes visuales compartidos
│   │   │   └── util/                 Utilidades (imágenes, recursos)
│   │   ├── data/repository/          Implementaciones local y remota (Firebase)
│   │   ├── domain/                   Modelos, interfaces y casos de uso
│   │   ├── features/                 Pantallas por funcionalidad
│   │   │   ├── ai/                   Guía Cafetero IA (OpenRouter)
│   │   │   ├── auth/ui/              Login, registro y recuperación
│   │   │   ├── dashboard/            Perfil, moderación y navegación interna
│   │   │   ├── onboarding/ui/        Introducción
│   │   │   ├── splash/ui/            Splash
│   │   │   └── spots/                Feed, detalle, mapa, visitados y creación
│   │   └── ui/theme/                 Colores, tipografía y tema Material
│   └── res/
│       ├── values/                   Textos en español (fuente)
│       ├── values-en/                Textos en inglés (misma clave, otro idioma)
│       ├── drawable/                 Imágenes de spots e iconos
│       ├── mipmap-*/                 Icono de launcher por densidad de pantalla
│       └── xml/                      Reglas de backup y FileProvider
├── src/test/                         Pruebas unitarias
└── src/androidTest/                  Pruebas instrumentadas

gradle/ gradlew gradlew.bat           Wrapper de Gradle
scripts/                              Scripts de subida a App Distribution
```

## Sobre los "archivos con el mismo nombre"

Algunos nombres se repiten **por diseño**, no están duplicados por error:

- `strings.xml` aparece en `values/` y `values-en/` → español e inglés.
- `ic_launcher*.webp/png` aparece en cada `mipmap-*` → una imagen por densidad de pantalla.
- `build.gradle.kts` hay dos → el del proyecto y el del módulo `app`.
- `NotificationsScreen.kt` **ya no está duplicado**: se usa solo `features/dashboard/notifications/`.

Las carpetas `build/`, `.gradle/`, `.kotlin/` y `.idea/` son generadas por Gradle/Android Studio, están ignoradas por Git y **no son código fuente**. Se pueden borrar sin peligro; se regeneran al compilar.

## Compilación

```powershell
# Requiere JDK de Android Studio
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug assembleRelease
```

APKs resultantes:

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release.apk`

## Configuración externa

- **Firebase**: `app/google-services.json` (Auth, Firestore, Storage, App Distribution).
- **OpenRouter**: la API key se lee de `local.properties` (ignorado por Git) como
  `openrouter.api.key=...` y se expone como `BuildConfig.OPENROUTER_API_KEY`.

## Distribución

```powershell
firebase login   # una sola vez, en una terminal interactiva
.\scripts\upload-app-distribution.ps1 -AppId "1:413793524043:android:182f931e9fb65cd885f0ce"
```
