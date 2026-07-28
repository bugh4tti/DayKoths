# DayKoths

Plugin de Koths para Spigot (1.20), creado por Bughatti.

## Comandos
Alias: `/dkoths`, `/dk`

| Comando | Descripción |
|---|---|
| `/dk` | Abre el menú principal |
| `/dk help` | Versión, autor y explicación de todos los comandos |
| `/dk reload` | Recarga config.yml |
| `/dk <koth>` | Abre el menú de ese koth |
| `/dk create <koth> <puntaje\|tiempo> <valor>` | Crea un koth |
| `/dk start <koth>` / `/dk stop <koth>` | Inicia o detiene manualmente |
| `/dk delete <koth>` | Elimina el koth (config incluido) |
| `/dk wand` | Item para marcar la zona (click izq = pos1, der = pos2) |
| `/dk setpos <koth>` | Guarda la zona marcada con la wand |
| `/dk capturetime <koth> <minutos>` | Tiempo de captura (modo tiempo) |
| `/dk duration <koth> <minutos>` | Duración total del koth activo |
| `/dk score <koth> true\|false` | Activa/desactiva modo puntaje (1 punto/seg) |
| `/dk utilities <koth> true\|false` | Permite/bloquea lana y telas |
| `/dk keepinventory <koth> true\|false` | |
| `/dk schedules <koth> <dia\|alldays> <hora 0-23> true\|false` | Horarios de inicio automático (huso Argentina) |
| `/dk rw <koth>` | Menú de recompensa (items) |
| `/dk rw create <koth> <comando>` | Agrega comando de recompensa (ej: `crates key give %player% comun 1`) |
| `/dk arsenal <koth>` | Menú de eventos aleatorios (buenos/malos) |
| `/dk tops` | Top 5 jugadores con más koths ganados (histórico) |

## Flujo para crear un koth
1. `/dk create arena1 puntaje 60`
2. `/dk wand` → click izq y click der para marcar la zona
3. `/dk setpos arena1`
4. `/dk rw arena1` → poner la recompensa en items (opcional)
5. `/dk rw create arena1 crates key give %player% comun 1` → recompensa de comando (opcional)
6. `/dk start arena1` (o programalo con `/dk schedules`)

## Menús
Todos de 54 slots, con paneles grises en los bordes:
- **Principal** (`/dk`): lista los koths (verde=activo, rojo=inactivo) + estrella del nether para crear
- **Koth**: iniciar/detener, info, recompensa, utilities, keepInventory, eliminar
- **Recompensa**: items (fila 2) + comandos de recompensa mostrados como papeles (fila 4)
- **Arsenal**: activa/desactiva el sistema y cada evento individual (lana verde=on, roja=off)
- **Tops**: cabezas de los 5 jugadores con más victorias

## Mecánica
- **Modo puntaje**: 1 punto por segundo dentro de la zona; gana quien más tenga al acabar la duración
- **Modo tiempo**: hay que mantenerse todo el tiempo de captura sin ser expulsado; si te sacan, se resetea
- **Schedules**: cada minuto el plugin revisa la hora de Argentina; si hay un horario activo y el koth tiene zona marcada, se prende solo (mensaje + título incluidos)
- **Arsenal**: cada `arsenal.event-interval-minutes` (default 6), si el koth está corriendo y hay gente adentro, sale un título "¡NUEVO EVENTO!" y se aplica un evento al azar entre los habilitados (buffs, debuffs, o cooldown de manzanas doradas/encantadas)

## BossBar y Scoreboard
On/off desde `config.yml` (`bossbar.enabled` / `scoreboard.enabled`). Aparecen solo mientras el koth está corriendo. Soportan placeholders propios y de PlaceholderAPI.

## Variables de PlaceholderAPI
Necesita PlaceholderAPI instalado (softdepend):
- `%daykoths_<koth>_next_start%`
- `%daykoths_<koth>_top_player_1_name%` / `_amount%` (también 2 y 3)
- `%daykoths_score_<koth>_top_1_name%` / `_value%` (también 2 y 3)

## Configuración
Todo editable en `config.yml`: prefijo, mensajes, títulos/subtítulos, bossbar, scoreboard, eventos del arsenal, y los datos de cada koth (zona, modo, duración, utilities, keepInventory, recompensas, schedules, estado).
