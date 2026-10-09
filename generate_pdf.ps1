$uploadedDir = "C:\Users\Johna\.gemini\antigravity\brain\fe80312b-408c-4aa6-b162-d398d852ca63\.user_uploaded"
$logoPath = "c:\Users\Johna\OneDrive\Desktop\EJESPOT\app\src\main\res\drawable\ic_logo_ejespot.png"
$htmlPath = "c:\Users\Johna\OneDrive\Desktop\EJESPOT\informe_fase2.html"
$pdfPath = "c:\Users\Johna\OneDrive\Desktop\EJESPOT\Seguimiento_Fase_2_EJESPOT.pdf"

function Get-Base64($path) {
    if (Test-Path $path) {
        $bytes = [System.IO.File]::ReadAllBytes($path)
        $ext = [System.IO.Path]::GetExtension($path).ToLower().Replace(".", "")
        if ($ext -eq "jpg") { $ext = "jpeg" }
        return "data:image/$ext;base64," + [Convert]::ToBase64String($bytes)
    }
    return ""
}

$imgSplashOnboarding = Get-Base64 (Join-Path $uploadedDir "media_1791505961401.png")
$imgLogin = Get-Base64 (Join-Path $uploadedDir "media_1791508167356.png")
$imgRegister = Get-Base64 (Join-Path $uploadedDir "media_1791508762967.png")
$imgForgot = Get-Base64 (Join-Path $uploadedDir "media_1791510182984.png")
$imgFeed = Get-Base64 (Join-Path $uploadedDir "media_1791512722052_6273ec12.png")
$imgDetail = Get-Base64 (Join-Path $uploadedDir "media_1791514838054_5e4734f3.png")
$imgCreate = Get-Base64 (Join-Path $uploadedDir "media_1791517225971_cb9bb3ef.png")
$imgLogo = Get-Base64 $logoPath

$htmlContent = @"
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Seguimiento Fase 2 - EjeSpot</title>
    <style>
        @page {
            size: A4;
            margin: 12mm 14mm 14mm 14mm;
        }
        * {
            box-sizing: border-box;
            -webkit-print-color-adjust: exact !important;
            print-color-adjust: exact !important;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Arial, sans-serif;
            color: #1F1A15;
            background: #FFFFFF;
            margin: 0;
            padding: 0;
            font-size: 9.5pt;
            line-height: 1.45;
        }
        .page {
            page-break-after: always;
            clear: both;
            position: relative;
            min-height: 980px;
        }
        .page:last-child {
            page-break-after: avoid;
        }
        .page-footer {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            display: flex;
            justify-content: space-between;
            font-size: 8pt;
            color: #887D70;
            border-top: 1px solid #E5DDD0;
            padding-top: 6px;
        }

        /* HEADER & COVER */
        .cover-header {
            border-bottom: 3px solid #3D6B4F;
            padding-bottom: 12px;
            margin-bottom: 18px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .uni-title {
            font-size: 11.5pt;
            font-weight: bold;
            color: #3D6B4F;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .uni-sub {
            font-size: 9pt;
            color: #6E6252;
        }
        .main-title {
            font-family: Georgia, 'Times New Roman', serif;
            font-size: 24pt;
            font-weight: bold;
            color: #1F1A15;
            margin: 8px 0 4px 0;
            line-height: 1.15;
        }
        .main-subtitle {
            font-size: 11.5pt;
            color: #B5652F;
            font-weight: 600;
            margin-bottom: 16px;
        }
        
        /* GITHUB BANNER */
        .repo-banner {
            background: #F3ECE0;
            border: 1.5px solid #CFE8D6;
            border-left: 6px solid #3D6B4F;
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .repo-label {
            font-size: 9pt;
            font-weight: bold;
            color: #3D6B4F;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .repo-url {
            font-family: Consolas, monospace;
            font-size: 11pt;
            font-weight: bold;
            color: #1F1A15;
            text-decoration: none;
        }
        .repo-badge {
            background: #3D6B4F;
            color: white;
            font-size: 8.5pt;
            padding: 4px 10px;
            border-radius: 20px;
            font-weight: bold;
        }

        /* SECTION STYLING */
        h2.section-title {
            font-family: Georgia, serif;
            font-size: 13.5pt;
            color: #3D6B4F;
            border-bottom: 1.5px solid #E5DDD0;
            padding-bottom: 6px;
            margin-top: 16px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
        }
        h2.section-title span.num {
            background: #3D6B4F;
            color: white;
            font-size: 9.5pt;
            font-family: sans-serif;
            font-weight: bold;
            padding: 2px 8px;
            border-radius: 4px;
            margin-right: 8px;
        }
        p {
            margin: 6px 0;
            color: #332B24;
            font-size: 9.5pt;
        }

        /* IMAGE GRIDS */
        .img-grid-2 {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
            margin: 12px 0;
        }
        .img-card {
            background: #FAF6EE;
            border: 1px solid #E5DDD0;
            border-radius: 10px;
            padding: 10px;
            text-align: center;
        }
        .img-card img {
            max-width: 100%;
            max-height: 480px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            object-fit: contain;
            background: #000000;
        }
        .img-caption {
            font-size: 9pt;
            font-weight: bold;
            color: #1F1A15;
            margin-top: 8px;
        }
        .img-desc {
            font-size: 8pt;
            color: #6E6252;
            margin-top: 2px;
        }

        .single-img-center {
            text-align: center;
            background: #FAF6EE;
            border: 1px solid #E5DDD0;
            border-radius: 10px;
            padding: 12px;
            margin: 12px 0;
        }
        .single-img-center img {
            max-width: 95%;
            max-height: 530px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            object-fit: contain;
        }

        /* HIGHLIGHT BOXES */
        .info-pill {
            display: inline-block;
            background: #CFE8D6;
            color: #2C5640;
            font-size: 8pt;
            font-weight: bold;
            padding: 3px 8px;
            border-radius: 12px;
            margin-right: 6px;
        }
        .feedback-pill {
            display: inline-block;
            background: #FCE8E6;
            color: #C5221F;
            font-size: 8pt;
            font-weight: bold;
            padding: 3px 8px;
            border-radius: 12px;
        }

        /* TABLES */
        table.rubric-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
            font-size: 8.5pt;
        }
        table.rubric-table th {
            background: #3D6B4F;
            color: white;
            text-align: left;
            padding: 8px 10px;
            font-weight: bold;
        }
        table.rubric-table td {
            border: 1px solid #E5DDD0;
            padding: 8px 10px;
            vertical-align: top;
        }
        table.rubric-table tr:nth-child(even) {
            background: #FAF6EE;
        }
        .check-icon {
            color: #2E7D32;
            font-weight: bold;
            font-size: 10pt;
        }

        /* CODE BOX */
        .code-box {
            background: #23272E;
            color: #ABB2BF;
            font-family: Consolas, monospace;
            font-size: 8.5pt;
            padding: 8px 12px;
            border-radius: 6px;
            margin: 8px 0;
            overflow-x: hidden;
            white-space: pre-wrap;
        }
    </style>
</head>
<body>

    <!-- ================= P&Aacute;GINA 1: PORTADA, ENLACE GITHUB Y ARQUITECTURA ================= -->
    <div class="page">
        <div class="cover-header">
            <div>
                <div class="uni-title">Universidad del Quind&iacute;o</div>
                <div class="uni-sub">Facultad de Ingenier&iacute;a &bull; Ingenier&iacute;a de Sistemas y Computaci&oacute;n</div>
                <div class="uni-sub">Programaci&oacute;n de Aplicaciones M&oacute;viles &bull; Docente: Carlos Andr&eacute;s Fl&oacute;rez V.</div>
            </div>
            <img src="$imgLogo" style="height: 48px;" alt="Logo EjeSpot">
        </div>

        <div class="main-title">Informe de Seguimiento Fase 2</div>
        <div class="main-subtitle">Proyecto: EjeSpot &ndash; Gu&iacute;a Tur&iacute;stica Colaborativa del Eje Cafetero</div>

        <!-- REPO GITHUB DESTACADO -->
        <div class="repo-banner">
            <div>
                <div class="repo-label">Repositorio Oficial en GitHub</div>
                <a class="repo-url" href="https://github.com/bombjohn/EJESPOT" target="_blank">https://github.com/bombjohn/EJESPOT</a>
                <div style="font-size: 8pt; color: #6E6252; margin-top: 3px;">Rama principal: <strong>master</strong> &bull; Commits organizados por cada funcionalidad</div>
            </div>
            <div class="repo-badge">&#10003; GitHub Verificado</div>
        </div>

        <h2 class="section-title"><span class="num">1</span> Organizaci&oacute;n y Arquitectura del Proyecto</h2>
        <p>Siguiendo las gu&iacute;as del curso, el proyecto fue estructurado bajo los principios de <strong>Clean Architecture</strong> y el patr&oacute;n <strong>MVVM (Model-View-ViewModel)</strong> con <strong>UIState reactivo</strong> (StateFlow) y <strong>Jetpack Compose Material 3</strong>:</p>

        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-top: 8px;">
            <div style="background: #FAF6EE; border: 1px solid #E5DDD0; border-radius: 8px; padding: 10px;">
                <strong style="color: #3D6B4F; font-size: 9pt;">Estructura de Paquetes (`com.example.ejespot`):</strong>
                <ul style="margin: 6px 0; padding-left: 18px; font-size: 8.5pt; color: #3E352B;">
                    <li><strong>core/ui:</strong> Componentes base reutilizables (`LoadingScreen`).</li>
                    <li><strong>domain/model:</strong> Modelos de dominio (`Spot`, `User`, `UserRole`, `SpotReview`).</li>
                    <li><strong>domain/repository:</strong> Abstracci&oacute;n de datos (`AuthRepository`).</li>
                    <li><strong>features/auth:</strong> Login, Registro, Recuperaci&oacute;n con ViewModel y UiState.</li>
                    <li><strong>features/dashboard:</strong> Scaffold &uacute;nico y navegaci&oacute;n por rol.</li>
                    <li><strong>features/spots:</strong> Feed (list), Detalle (detail) y Creaci&oacute;n (create).</li>
                    <li><strong>navigation:</strong> `AppNavigation.kt` y `DashboardNavigation.kt`.</li>
                </ul>
            </div>

            <div style="background: #FAF6EE; border: 1px solid #E5DDD0; border-radius: 8px; padding: 10px;">
                <strong style="color: #3D6B4F; font-size: 9pt;">Patrones y Buenas Pr&aacute;cticas Aplicadas:</strong>
                <ul style="margin: 6px 0; padding-left: 18px; font-size: 8.5pt; color: #3E352B;">
                    <li><strong>Separaci&oacute;n Estricta:</strong> Cada pantalla cuenta con su propio <strong>ViewModel</strong> y <strong>UiState</strong> desacoplado.</li>
                    <li><strong>Navegaci&oacute;n Anidada (Gu&iacute;a 11):</strong> Un &uacute;nico `Scaffold` en `MainScreen` con `SnackbarHostState` centralizado, evitando conflictos visuales.</li>
                    <li><strong>Inmutabilidad:</strong> Flujos StateFlow (`asStateFlow()`) para actualizar la UI de manera unidireccional.</li>
                    <li><strong>&Iacute;cono de la App:</strong> Configurado en `AndroidManifest.xml` (`@mipmap/ic_launcher_ejespot`).</li>
                </ul>
            </div>
        </div>

        <h2 class="section-title"><span class="num">2</span> Configuraci&oacute;n del &Iacute;cono de la Aplicaci&oacute;n</h2>
        <p>Se gener&oacute; el &iacute;cono representativo de la marca <strong>EjeSpot</strong> (monta&ntilde;as del Eje Cafetero con granos de caf&eacute; y pin de geolocalizaci&oacute;n) y se incorpor&oacute; en todas las densidades de la carpeta <code>res/mipmap-*/</code> vincul&aacute;ndolo en <code>AndroidManifest.xml</code>:</p>
        
        <div class="code-box">&lt;application
    android:icon="@mipmap/ic_launcher_ejespot"
    android:roundIcon="@mipmap/ic_launcher_ejespot"
    android:label="@string/app_name" ... &gt;</div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 1 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 2: SPLASH Y ONBOARDING ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">3</span> Pantalla Inicial: Splash Screen y Onboarding</h2>
        <p>La aplicaci&oacute;n cuenta con una pantalla de bienvenida <strong>Splash</strong> con fondo degradado y logo animado, seguida de un flujo de <strong>Onboarding</strong> interactivo en 3 diapositivas (`HorizontalPager`) que explica la propuesta de valor comunitaria de EjeSpot.</p>

        <div class="single-img-center">
            <img src="$imgSplashOnboarding" alt="Splash y Onboarding">
            <div class="img-caption">Pantalla 1.1 Splash (Apertura) y Pantalla 1.2 Onboarding (Tutorial de Bienvenida)</div>
            <div class="img-desc">Implementado con `SplashScreen.kt` y `OnboardingScreen.kt` con soporte de salto ("Omitir tutorial") e indicadores din&aacute;micos.</div>
        </div>

        <div style="background: #F3ECE0; border-left: 4px solid #3D6B4F; padding: 10px; border-radius: 4px; font-size: 8.5pt;">
            <strong>Detalles de Implementaci&oacute;n:</strong><br>
            &bull; <strong>SplashScreen:</strong> Temporizador con corrutinas (`delay(2000)`) que transiciona suavemente hacia el Onboarding o la autenticaci&oacute;n.<br>
            &bull; <strong>OnboardingScreen:</strong> Carrusel con selecci&oacute;n visual de pasos, textos explicativos de turismo regional ("Descubre Joyas Ocultas", "Gana XP", "Comunidad Eje Cafetero").
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 2 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 3: FORMULARIOS DE AUTENTICACI&Oacute;N ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">4</span> Formularios de Autenticaci&oacute;n con Retroalimentaci&oacute;n (Snackbar)</h2>
        <p>Todos los formularios de acceso implementan validaci&oacute;n reactiva con su respectivo <code>ViewModel</code> y <code>UiState</code>, emitiendo mensajes de retroalimentaci&oacute;n inmediata mediante <strong>Snackbar</strong> y textos de validaci&oacute;n en tiempo real.</p>

        <div class="img-grid-2">
            <div class="img-card">
                <img src="$imgLogin" alt="Login con Retroalimentacion">
                <div class="img-caption">Pantalla 2.1: Inicio de Sesi&oacute;n (Login)</div>
                <div class="img-desc"><span class="feedback-pill">Retroalimentaci&oacute;n:</span> "Por favor complete todos los campos" / Notificaci&oacute;n de credenciales.</div>
            </div>
            <div class="img-card">
                <img src="$imgRegister" alt="Registro de Turista">
                <div class="img-caption">Pantalla 2.2: Registro ("Crea tu cuenta")</div>
                <div class="img-desc"><span class="info-pill">Validaci&oacute;n:</span> Selector de departamentos del Eje Cafetero, t&eacute;rminos de servicio y confirmaci&oacute;n.</div>
            </div>
        </div>

        <p style="margin-top: 8px;"><strong>Archivos fuente:</strong> <code>LoginScreen.kt</code>, <code>LoginViewModel.kt</code>, <code>LoginUiState.kt</code>, <code>RegisterScreen.kt</code>, <code>RegisterViewModel.kt</code>, <code>RegisterUiState.kt</code>.</p>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 3 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 4: RECUPERACI&Oacute;N DE CONTRASE&Ntilde;A ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">5</span> Pantalla de Recuperaci&oacute;n de Contrase&ntilde;a y Feedback</h2>
        <p>Permite al usuario solicitar el restablecimiento de su acceso. Al ingresar el correo y validar el formato, el <code>ForgotPasswordViewModel</code> actualiza el <code>ForgotPasswordUiState</code> a estado de &eacute;xito y despliega la pantalla de confirmaci&oacute;n con enlace a la aplicaci&oacute;n de correo.</p>

        <div class="single-img-center">
            <img src="$imgForgot" alt="Recuperacion de Contrasena">
            <div class="img-caption">Pantalla 2.4: Formulario de Recuperaci&oacute;n y Pantalla 2.5: Feedback "&iexcl;Revisa tu correo!"</div>
            <div class="img-desc">Despliegue del estado de confirmaci&oacute;n con botones para abrir la app de correo o reenviar el enlace.</div>
        </div>

        <div style="background: #F3ECE0; border-left: 4px solid #3D6B4F; padding: 10px; border-radius: 4px; font-size: 8.5pt;">
            <strong>Manejo de Errores y Feedback:</strong><br>
            &bull; Si el campo est&aacute; vac&iacute;o o el formato no es v&aacute;lido, se emite retroalimentaci&oacute;n visual al usuario.<br>
            &bull; Al enviar exitosamente, se muestra el correo destinatario y se habilita la opci&oacute;n de retorno al inicio de sesi&oacute;n.
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 4 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 5: FEED DE PUBLICACIONES ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">6</span> Feed de Publicaciones (Explorar Spots)</h2>
        <p>Implementado en <code>SpotListScreen.kt</code> con su correspondiente <code>SpotListViewModel.kt</code> y <code>SpotListUiState.kt</code>. Presenta los atractivos tur&iacute;sticos divididos en lugares verificados por la comunidad y propuestas ciudadanas en espera de moderaci&oacute;n.</p>

        <div class="single-img-center">
            <img src="$imgFeed" alt="Feed de Publicaciones">
            <div class="img-caption">Pantalla 3.1: Feed Principal (Explorar), 3.2: B&uacute;squeda y Filtros, 3.3: Resultados en Vivo</div>
            <div class="img-desc">Filtro por categor&iacute;as (Naturaleza, Gastronom&iacute;a, Historia), buscador interactivo y bot&oacute;n flotante (FAB) para proponer nuevo Spot.</div>
        </div>

        <div style="background: #F3ECE0; border-left: 4px solid #3D6B4F; padding: 10px; border-radius: 4px; font-size: 8.5pt;">
            <strong>Caracter&iacute;sticas del Feed:</strong><br>
            &bull; <strong>Filtrado Reactivo:</strong> Chips de categor&iacute;as actualizan la lista en memoria mediante StateFlow.<br>
            &bull; <strong>Secciones Diferenciadas:</strong> Lugares verificados vs. propuestas ciudadanas pendientes de moderaci&oacute;n.<br>
            &bull; <strong>Bot&oacute;n Flotante (FAB):</strong> Acceso directo a la pantalla de creaci&oacute;n de nuevas publicaciones.
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 5 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 6: DETALLE DE PUBLICACI&Oacute;N Y EXPERIENCIAS ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">7</span> Detalle de Publicaci&oacute;n con Experiencias de la Comunidad</h2>
        <p>Implementado con <code>SpotDetailScreen.kt</code>, <code>SpotDetailViewModel.kt</code> y <code>SpotDetailUiState.kt</code>. Muestra fotograf&iacute;as en alta resoluci&oacute;n, fichas t&eacute;cnicas seg&uacute;n la categor&iacute;a del lugar y la secci&oacute;n de <strong>"Experiencias de la comunidad"</strong> con rese&ntilde;as de usuarios.</p>

        <div class="single-img-center">
            <img src="$imgDetail" alt="Detalle de Spot con Experiencias">
            <div class="img-caption">Pantalla 4.1: Detalle de Publicaci&oacute;n (Naturaleza, Gastronom&iacute;a y Senderismo)</div>
            <div class="img-desc">Fichas t&eacute;cnicas especializadas, secci&oacute;n comunitaria de rese&ntilde;as con avatares e insignias, y acciones interactivas (+10 XP / +25 XP).</div>
        </div>

        <div style="background: #F3ECE0; border-left: 4px solid #3D6B4F; padding: 10px; border-radius: 4px; font-size: 8.5pt;">
            <strong>Funcionalidades Interactivas:</strong><br>
            &bull; <strong>Fichas T&eacute;cnicas:</strong> Dificultad, altitud, clima y equipamiento para senderos; men&uacute;s y precios para gastronom&iacute;a.<br>
            &bull; <strong>Rese&ntilde;as Comunitarias:</strong> Di&aacute;logo interactivo para calificar (1 a 5 estrellas) y redactar rese&ntilde;as sumando <strong>+25 XP</strong>.<br>
            &bull; <strong>Acciones R&aacute;pidas:</strong> "Marcar como visitado (+10 XP)" con feedback por Snackbar, "Compartir" y "C&oacute;mo llegar (GPS)".
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 6 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 7: CREACI&Oacute;N DE NUEVAS PUBLICACIONES ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">8</span> Creaci&oacute;n de Publicaciones y Feedback de Env&iacute;o</h2>
        <p>Implementado mediante <code>CreateSpotScreen.kt</code>, <code>CreateSpotViewModel.kt</code> y <code>CreateSpotUiState.kt</code>. Incluye el formulario de captura y la pantalla de retroalimentaci&oacute;n de &eacute;xito al someter la propuesta.</p>

        <div class="single-img-center">
            <img src="$imgCreate" alt="Crear Spot y Feedback de Envio">
            <div class="img-caption">Pantalla 5.1: "Nuevo lugar" (Formulario) y Pantalla 5.2: "&iexcl;Punto enviado para revisi&oacute;n!" (&Eacute;xito)</div>
            <div class="img-desc">Captura completa con selector de categor&iacute;a, rango de precio, horario sugerido, ubicaci&oacute;n GPS y confirmaci&oacute;n con +50 XP.</div>
        </div>

        <div style="background: #F3ECE0; border-left: 4px solid #3D6B4F; padding: 10px; border-radius: 4px; font-size: 8.5pt;">
            <strong>Flujo de Creaci&oacute;n y Feedback:</strong><br>
            &bull; <strong>Pantalla 5.1:</strong> Formulario profesional con advertencia de moderaci&oacute;n y validaci&oacute;n de campos obligatorios.<br>
            &bull; <strong>Pantalla 5.2:</strong> Pantalla de &eacute;xito con insignia <code>+50 XP Ganados &bull; En revisi&oacute;n</code>, resumen del spot propuesto y opciones para retornar al inicio o visualizar el nuevo lugar en la lista de propuestas pendientes.
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 7 de 8</span>
        </div>
    </div>

    <!-- ================= P&Aacute;GINA 8: TABLA DE CUMPLIMIENTO ================= -->
    <div class="page">
        <h2 class="section-title"><span class="num">9</span> Matriz de Cumplimiento de Requisitos (Seguimiento Fase 2)</h2>
        <p>A continuaci&oacute;n se resume el estado de cumplimiento de cada uno de los puntos estipulados en la gu&iacute;a de entrega:</p>

        <table class="rubric-table">
            <thead>
                <tr>
                    <th style="width: 5%;">#</th>
                    <th style="width: 35%;">Requisito de la Gu&iacute;a</th>
                    <th style="width: 15%;">Estado</th>
                    <th style="width: 45%;">Archivos / Evidencia</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><strong>1</strong></td>
                    <td><strong>Crear el repositorio del proyecto</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td>Repositorio p&uacute;blico en GitHub: <a href="https://github.com/bombjohn/EJESPOT" target="_blank">github.com/bombjohn/EJESPOT</a> con historial de commits.</td>
                </tr>
                <tr>
                    <td><strong>2</strong></td>
                    <td><strong>Configurar la estructura del proyecto</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td>Paquetes <code>core</code>, <code>domain</code>, <code>features</code> y <code>navigation</code> organizados bajo Clean Architecture.</td>
                </tr>
                <tr>
                    <td><strong>3</strong></td>
                    <td><strong>Crear la pantalla inicial</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>SplashScreen.kt</code> con animaci&oacute;n de logo y <code>OnboardingScreen.kt</code> con carrusel tutorial descriptivo.</td>
                </tr>
                <tr>
                    <td><strong>4</strong></td>
                    <td><strong>Configurar el &iacute;cono de la aplicaci&oacute;n</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td>&Iacute;cono <code>@mipmap/ic_launcher_ejespot</code> en todas las densidades configurado en <code>AndroidManifest.xml</code>.</td>
                </tr>
                <tr>
                    <td><strong>5</strong></td>
                    <td><strong>Pantalla de inicio de sesi&oacute;n</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>LoginScreen.kt</code>, <code>LoginViewModel.kt</code> y <code>LoginUiState.kt</code> con validaci&oacute;n y botones sociales.</td>
                </tr>
                <tr>
                    <td><strong>6</strong></td>
                    <td><strong>Pantalla de registro</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>RegisterScreen.kt</code>, <code>RegisterViewModel.kt</code> y <code>RegisterUiState.kt</code> con dropdown de departamentos.</td>
                </tr>
                <tr>
                    <td><strong>7</strong></td>
                    <td><strong>Recuperaci&oacute;n de contrase&ntilde;a</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>ForgotPasswordScreen.kt</code>, <code>ForgotPasswordViewModel.kt</code> y estado de correo enviado.</td>
                </tr>
                <tr>
                    <td><strong>8</strong></td>
                    <td><strong>Feed y Detalle (cada una con su ViewModel)</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td>Feed: <code>SpotListViewModel</code>. Detalle: <code>SpotDetailViewModel</code> y <code>SpotDetailUiState</code> con rese&ntilde;as de la comunidad.</td>
                </tr>
                <tr>
                    <td><strong>9</strong></td>
                    <td><strong>Pantalla para nuevas publicaciones</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>CreateSpotViewModel</code> con pantalla de captura (5.1) y pantalla de &eacute;xito/feedback (5.2 con +50 XP).</td>
                </tr>
                <tr>
                    <td><strong>10</strong></td>
                    <td><strong>Navegaci&oacute;n y retroalimentaci&oacute;n con Snackbar</strong></td>
                    <td><span class="check-icon">&#10003; Cumplido</span></td>
                    <td><code>AppNavigation</code> y <code>DashboardNavigation</code> seg&uacute;n Gu&iacute;a 11 de clase con <code>SnackbarHostState</code> unificado.</td>
                </tr>
            </tbody>
        </table>

        <div style="margin-top: 25px; text-align: center; border-top: 1px solid #E5DDD0; padding-top: 15px;">
            <p style="font-size: 9pt; color: #6E6252;">
                <strong>EjeSpot &bull; Turismo Colaborativo en Caldas, Quind&iacute;o y Risaralda</strong><br>
                Seguimiento Fase 2 &ndash; Universidad del Quind&iacute;o &bull; 2026
            </p>
        </div>

        <div class="page-footer">
            <span>EjeSpot &bull; Seguimiento Fase 2</span>
            <span>P&aacute;gina 8 de 8</span>
        </div>
    </div>

</body>
</html>
"@

[System.IO.File]::WriteAllText($htmlPath, $htmlContent, [System.Text.Encoding]::UTF8)

# Generar PDF con Edge headless
$edge = "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
if (-not (Test-Path $edge)) {
    $edge = "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
}
$tempDir = Join-Path $env:TEMP "edge_pdf_profile_clean"
$htmlUrl = "file:///" + $htmlPath.Replace("\", "/")

& $edge --headless --disable-gpu --user-data-dir="$tempDir" --no-pdf-header-footer "--print-to-pdf=$pdfPath" "$htmlUrl"
Start-Sleep -Seconds 3

if (Test-Path $pdfPath) {
    Write-Host "PDF generado exitosamente. Tamano: $((Get-Item $pdfPath).Length) bytes"
}
