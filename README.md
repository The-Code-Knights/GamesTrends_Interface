# GamesTrends

## Descripción general

**GamesTrends** es un sistema de análisis de tendencias del sector videojuegos que combina datos en tiempo real de **Steam** y **Twitch** para identificar qué juegos tienen mayor potencial de streaming.

La propuesta de valor: hay juegos donde la gente prefiere ver a otros jugar antes que jugar ellos mismos, lo que los convierte en una gran oportunidad para streamers. El sistema detecta estos juegos cruzando datos de jugadores activos en Steam con espectadores en Twitch, y presenta una puntuación de recomendación por juego en un dashboard web en tiempo real.


---

## Fuentes externas utilizadas

### Steam Web API
- **Endpoint rankings**: `https://api.steampowered.com/ISteamChartsService/GetMostPlayedGames/v1/`
- **Endpoint jugadores**: `https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid={id}`
- **Endpoint nombre**: `https://store.steampowered.com/api/appdetails?appids={id}`
- **Motivo de elección**: API pública, sin autenticación para consultas básicas, con datos de jugadores concurrentes reales.

### Twitch Helix API
- **Endpoint streams**: `https://api.twitch.tv/helix/streams`
- **Endpoint juegos**: `https://api.twitch.tv/helix/games`
- **Motivo de elección**: Única fuente oficial con datos en tiempo real de espectadores por stream, paginada y con soporte para hasta 100 resultados por llamada.

---

## Estructura del Datamart

El datamart usa **SQLite** con dos tablas que se cruzan mediante `LOWER(name)` para tolerar diferencias de capitalización:

```
steam_snapshot
├── app_id      TEXT  PRIMARY KEY
├── name        TEXT
├── players     INTEGER
└── updated_at  TEXT

twitch_stream
├── stream_id    TEXT  PRIMARY KEY
├── user_name    TEXT
├── game_name    TEXT
├── title        TEXT
├── viewer_count INTEGER
└── updated_at   TEXT
```

**Justificación**: SQLite es suficiente para el volumen de datos (cientos de juegos, miles de streams) y no requiere infraestructura adicional. La query analítica agrega viewers y streams por juego mediante un `LEFT JOIN`, calculando en SQL el `ratio` y el `espectadores_por_stream`.

**Normalización de nombres**: Steam y Twitch usan nombres distintos para el mismo juego (ej. `Counter-Strike 2` en Steam vs `Counter-Strike` en Twitch). El módulo `EventRouter` aplica limpieza de caracteres especiales (™, ®, apóstrofes tipográficos) y un pequeño mapa de alias conocidos antes de persistir.

---

## Arquitectura del sistema

<img width="1723" height="900" alt="estructura_business_Unit" src="https://github.com/user-attachments/assets/d86329a2-b661-40c3-9515-9b14fb8d4d3e" />

Los feeders (Steam y Twitch) actúan como publishers: consultan sus respectivas APIs y publican eventos en el broker (ActiveMQ), cada uno en su propio topic.

El Event Store Builder actúa como subscriber: consume ambos topics y persiste los eventos de forma inmutable en el Event Store (ficheros .events en disco).

La Business Unit recibe los eventos por dos vías: en tiempo real suscribiéndose directamente al broker, y al arranque cargando el histórico del Event Store. Con esos datos construye y actualiza el Datamart (SQLite) y expone el dashboard web

---

## Funcionalidades del diseño

| Módulo | Funcionalidad | Descripción |
|---|---|---|
| Todos | **MVC** | Separación entre modelo, controlador y vista |
| Todos | **SLF4J Facade** | Gestión de logging |
| steam/twitch-feeder | **Publisher/Subscriber** | Publicación de eventos en topics de ActiveMQ |
| steam/twitch-feeder | **Strategy** | `SteamConsumer` / `TwitchConsumer` como interfaces intercambiables |
| steam/twitch-feeder | **Scheduled Execution** | `ScheduledExecutorService` para ejecución periódica cada hora |
| event-store-builder | **Event Sourcing** | Eventos almacenados de forma inmutable en ficheros `.events` |
| event-store-builder | **Durable Subscription** | Suscripción durable en ActiveMQ para no perder eventos |
| business-unit | **Repository** | `DatamartRepository` encapsula todo el acceso a SQLite |
| business-unit | **Event-Driven** | El datamart se actualiza reactivamente al recibir cada evento |
| business-unit | **Alias Map** | Mapa de normalización para unificar nombres entre fuentes |

---

## Requisitos previos

- **Java 21** o superior
- **Maven 3.8** o superior
- **Apache ActiveMQ** ejecutándose
- Credenciales de la **Twitch API** (token de acceso + client ID): [dev.twitch.tv](https://dev.twitch.tv)

---

## Cómo ejecutar cada módulo

Cada módulo se ejecuta desde IntelliJ lanzando su clase `Main` con los siguientes **Program Arguments**:

### 1. Event Store Builder
```
<brokerUrl> <steamTopic> <twitchTopic>
```
**Ejemplo:**
```
tcp://localhost:61616 steam.games twitch.streams
```

---

### 2. Steam Feeder
```
<brokerUrl> <topicName>
```
**Ejemplo:**
```
tcp://localhost:61616 steam.games
```

---

### 3. Twitch Feeder
```
<TwitchToken> <TwitchClientId> <brokerUrl> <TwitchTopicName> <gameNameCacheDb>
```
**Ejemplo:**
```
abc123token myclientid tcp://localhost:61616 twitch.streams ./cache.db
```
`<gameNameCacheDb>` sería la ruta a un fichero SQLite local que cachea los nombres de los juegos para reducir llamadas a la API.

---

### 4. Business Unit
```
<brokerUrl> <steamTopic> <twitchTopic> <eventStorePath> <datamartPath> <port>
```
**Ejemplo:**
```
tcp://localhost:61616 steam.games twitch.streams ./eventstore ./datamart.db 8080
```
Al arrancar carga el histórico del event store, luego se suscribe a eventos en vivo y levanta el dashboard.

---

## Dashboard

Una vez arrancada la business-unit, abrir en el navegador:

```
http://localhost:<port>
```

El dashboard muestra:
- **TOP 3** juegos recomendados con mayor Puntuación
- **Tabla completa** ordenada por Puntuación, con filtros de número de juegos
- **Selector de pesos** para ajustar la fórmula en tiempo real:

```
Puntuación = (W1 × Ratio) + (W2 × Espectadores / Stream)
```

donde `Ratio = Espectadores Twitch / Jugadores Steam`.

El color de la columna **Puntuación** indica el nivel de oportunidad:
- 🟢 Verde — Puntuación alto (> 0.35)
- 🟤 Marrón — Puntuación medio (0.17 – 0.35)
- 🔴 Rojo — Puntuación bajo (< 0.17)

---

## Ramas del proyecto

- **develop** → Rama principal de desarrollo (Sprint 1, 2 y 3 completo).
- **master** → Versión final del proyecto (Sprint 3).
