# Sprint 2 – Message Broker & Event Store

## Descripción general
En este sprint se amplía la arquitectura incorporando un sistema de **procesamiento de eventos** mediante **ActiveMQ**.  
Los módulos dejan de escribir directamente en bases de datos y pasan a publicar eventos, que son procesados y almacenados por el nuevo componente **Event Store Builder**.

---

## Fuentes externas utilizadas
- **Steam Web API** → Datos de jugadores concurrentes.
- **Twitch API** → Datos de streams en directo.

Ambas fuentes publican ahora sus datos como eventos.

---

## Arquitectura del Sprint 2
El sistema evoluciona hacia un flujo orientado a eventos:

<img width="1989" height="1299" alt="Diagrama Store Builder " src="https://github.com/user-attachments/assets/49ca1b86-4c28-4930-b0b2-336b449a61e8" />

- **steam-feeder** y **twitch-feeder** consumen datos externos y los publican en ActiveMQ.
- **Event Store Builder** recibe los eventos, los transforma y los almacena como archivos `.events`.
- Se reorganiza el proyecto siguiendo el patrón **MVC**.

---

## Estado del Sprint 2
En este sprint se ha completado:

- Integración de ActiveMQ como broker de mensajería
- Publicación de eventos desde Steam y Twitch
- Implementación del Event Store Builder
- Persistencia de eventos en formato inmutable
- Reorganización de paquetes y controladores siguiendo MVC

---

## Ramas del proyecto
- **develop** → Rama principal de desarrollo (Sprint 1 y 2 completo).
- **master** → Reservada para versiones finales del proyecto.

---

## Cómo ejecutar el proyecto

### Ejecutar el Event Store Builder
Usage: java Main &lt;brokerUrl&gt; &lt;steamTopic&gt; &lt;twitchTopic&gt;
### Ejecutar el módulo de Steam
Usage: java Main &lt;brokerUrl&gt; &lt;steamTopic&gt;
### Ejecutar el módulo de Twitch
Usage: java Main &lt;TwitchToken&gt; &lt;TwitchClientId&gt; &lt;brokerUrl&gt; &lt;TwitchTopicName&gt;
