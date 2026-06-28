# JOX3 TV - IPTV Player for Android

Aplicación nativa de Android para IPTV con soporte completo para Xtream Codes, EPG multi-lista.

## Características

- **Multi-lista Xtream Codes** — Gestiona múltiples servidores/cuentas
- **TV en Vivo** — Grid de canales con categorías, búsqueda y favoritos
- **Películas** — Catálogo con filtros, info detallada y reproductor
- **Series** — Temporadas/episodios con info detallada
- **EPG Completo** — Guía electrónica de programas con grilla horaria
- **Reproductor ExoPlayer** — Soporte HLS/M3U8, selección de pistas de audio/subtítulos
- **TV Box** — Soporte D-pad y focus states para Android TV
- **Dark Theme** — Diseño moderno con acentos cyan y púrpura

## Arquitectura

- **Kotlin** + **Jetpack Compose** + **Material 3**
- **MVVM** + **Clean Architecture**
- **Hilt** (DI) | **Retrofit** (network) | **Room** (cache) | **ExoPlayer** (media)
- **Navigation Compose** para navegación

## Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17+
- Android SDK 34+
- Gradle 8.4+

## Build

```bash
# Clonar el repositorio
git clone https://github.com/TU_USUARIO/jox3tv-app.git
cd jox3tv-app

# Build debug APK
./gradlew assembleDebug

# El APK estará en:
# app/build/outputs/apk/debug/app-debug.apk
```

## Instalación en TV Box

1. Habilitar "Orígenes desconocidos" en Ajustes > Seguridad
2. Transferir el APK por USB o red
3. Instalar con un gestor de archivos

## Estructura del Proyecto

```
app/src/main/java/com/jox3/tv/
├── Jox3App.kt                 # Application (Hilt entry)
├── MainActivity.kt            # Activity principal
├── data/
│   ├── local/                 # Room DB, DAOs, Entities
│   ├── remote/                # XtreamApi, DTOs, EpgParser
│   └── repository/            # Repositorios
├── di/                        # Hilt modules
├── domain/model/              # Modelos de dominio
└── ui/
    ├── theme/                 # Colores, tipografía, tema
    ├── navigation/            # Grafo de navegación
    ├── home/                  # Dashboard
    ├── live/                  # TV en vivo
    ├── movies/                # Películas + detalle
    ├── series/                # Series + detalle
    ├── player/                # ExoPlayer
    ├── epg/                   # Guía TV
    ├── settings/              # Ajustes + Login
    └── components/            # Componentes compartidos
```

## Configuración del Servidor

1. Abrir la app → Ajustes → Añadir servidor
2. Introducir datos del servidor Xtream Codes:
   - URL (sin http://)
   - Puerto
   - Usuario
   - Contraseña
3. Probar conexión → Conectar

## Licencia

Proyecto privado. Todos los derechos reservados.
