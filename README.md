# DayKoths

Plugin de Koths para Spigot, creado por Bughatti.

## Comandos
- `/daykoths help` — versión y autor
- `/daykoths reload`
- `/daykoths {koth}` — abre el menú del koth
- `/daykoths create {koth} {puntaje|tiempo}`
- `/daykoths start {koth}` / `/daykoths stop {koth}`
- `/daykoths wand` — wand para marcar zona (click izq = pos1, click der = pos2)
- `/daykoths setpos {koth}` — guarda la zona marcada con la wand en ese koth
- `/daykoths schedules {koth} {dia|alldays} {hora 0-23} {true|false}`
- `/daykoths utilities {koth} true/false`
- `/daykoths keepinventory {koth} true/false`
- `/daykoths duration {koth} {minutos}`
- `/daykoths rw {koth}` — menú de recompensa (solo para el ganador)

Alias: `/dkoths`, `/dk`

## Flujo para crear un koth
1. `/daykoths create arena1 60`
2. `/daykoths wand` → click izq y click der para marcar la zona
3. `/daykoths setpos arena1`
4. `/daykoths rw arena1` → poner la recompensa
5. `/daykoths start arena1`

Todo se configura desde `config.yml` (mensajes, títulos, prefijos, koths).
