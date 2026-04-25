# Sprint 1 – Consumo y Almacenamiento de Datos

## Descripción general
En este primer sprint se desarrollan dos módulos independientes capaces de consumir información desde fuentes externas, procesarla y almacenarla en una base de datos SQLite.  
Este trabajo servirán como base para los sprints posteriores, donde se ampliará la arquitectura y se construirá un dashboard.

El proyecto sigue una arquitectura inspirada en el patrón **MVC**, implementando en este sprint únicamente las capas de **Modelo** y **Controlador**.

---

## Fuentes externas utilizadas
- **Steam Web API** → Información sobre jugadores actuales en videojuegos .  
- **Twitch API** → Información sobre streams en directo.

Cada fuente se procesa en un módulo independiente.

---

## Arquitectura del Sprint 1
El diseño se basa en un modelo común para ambos módulos, inspirado en el siguiente esquema:
<img width="1878" height="770" alt="Diagrama de clases" src="https://github.com/user-attachments/assets/bb2d47ce-e78c-4ea8-8bac-46a3173a84ac" />


Este diseño define:
- Un consumidor de datos (API Consumer)  
- Un controlador que orquesta el proceso  
- Un almacén SQLite para persistencia  
- Un modelo de datos específico para cada fuente  

---

## Estado del Sprint 1
En este sprint se ha completado:

- Configuración inicial del repositorio y estructura del proyecto  
- Implementación de los módulos de consumo de APIs (Steam y Twitch)  
- Procesamiento y transformación de los datos obtenidos  
- Persistencia en bases de datos SQLite independientes  
- Ejecución periódica del proceso de extracción y almacenamiento  

---

## Ramas del proyecto
- **develop** → Rama principal de desarrollo (Sprint 1 completo).  
- **master** → Reservada para versiones finales del proyecto.
---
## Cómo ejecutar el proyecto

Cada módulo del proyecto se ejecuta de forma independiente y requiere distintos argumentos según la fuente de datos que consume.

### Ejecutar el módulo de Steam
El módulo **steam-feeder** solo necesita recibir la ruta a la base de datos SQLite donde se almacenarán los datos.

**Uso:**
java -jar steam-feeder.jar <ruta_base_de_datos>


### Ejecutar el módulo de Twitch
El módulo **twitch-feeder** requiere tres argumentos:

1. **Ruta de la base de datos SQLite**
2. **Token de acceso de Twitch**
3. **Client ID de Twitch**

**Uso:**
java -jar twitch-feeder.jar &lt;ruta_base_de_datos&gt; &lt;token&gt; &lt;client_id&gt;

> Nota: El token y el Client ID deben obtenerse desde la plataforma de desarrolladores de Twitch.



