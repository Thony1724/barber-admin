# Diccionario de datos — Barber Admin

> Fuente: entidades JPA en `src/main/java/com/practicando/practicando/model/` de la rama `main`. Este documento describe **el mapeo declarado en Java**, no una inspección directa del esquema físico de MariaDB. Tipos SQL exactos, índices generados automáticamente y restricciones de la base de datos deben contrastarse con `SHOW CREATE TABLE`.

## Convenciones

- **PK**: clave primaria; **FK**: clave foránea; **NN**: `nullable=false`; **UQ**: unicidad declarada.
- Todos los `id` son `Long`, con `@Id` y `@GeneratedValue(strategy = IDENTITY)`.
- `String` representa texto, `BigDecimal` importes decimales, `LocalDate` fecha, `LocalTime` hora y `LocalDateTime` fecha y hora.
- Los nombres de columnas sin `name` explícito siguen la estrategia de nombres de Hibernate; los nombres listados son los esperados según la configuración usual de Spring Boot.
- **NN** se refiere a la anotación JPA, no implica que todos los campos tengan `@NotNull` en formularios.

## Tabla `clientes` — entidad `Cliente`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| nombre | String(100) | NN | Nombre |
| apellido | String(100) | NN | Apellido |
| telefono | String(20) | — | Teléfono |
| email | String(150) | UQ | Correo electrónico |

Fuente: `model/Cliente.java`.

## Tabla `barberos` — entidad `Barbero`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| nombre | String(100) | NN | Nombre |
| apellido | String(100) | NN | Apellido |
| telefono | String(20) | — | Teléfono |
| email | String(150) | UQ | Correo electrónico |
| especialidad | String(150) | — | Especialidad |
| activo | Boolean | NN, valor inicial `true` en Java | Estado activo |

Fuente: `model/Barbero.java`.

## Tabla `servicios` — entidad `Servicio`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| nombre | String(100) | NN | Servicio |
| descripcion | String(500) | — | Descripción |
| precio | BigDecimal(10,2) | NN | Precio |
| duracion_minutos | Integer | NN | Duración |
| activo | Boolean | NN, valor inicial `true` en Java | Disponibilidad |

Fuente: `model/Servicio.java`.

## Tabla `citas` — entidad `Cita`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| cliente_id | Long (relación) | FK → clientes.id, NN | Cliente de la cita |
| barbero_id | Long (relación) | FK → barberos.id, NN | Barbero asignado |
| servicio_id | Long (relación) | FK → servicios.id, NN | Servicio reservado |
| fecha | LocalDate | NN | Fecha |
| hora | LocalTime | NN | Hora |
| estado | Enum EstadoCita, String(20) | NN; inicial `PROGRAMADA` en Java | Estado |
| duracion_minutos | Integer | NN | Duración registrada en cita |
| precio | BigDecimal(10,2) | NN | Precio registrado en cita |

Las tres relaciones son `@ManyToOne(fetch = LAZY, optional = false)`.

Fuente: `model/Cita.java`.

## Tabla `pagos` — entidad `Pago`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| cita_id | Long (relación) | FK → citas.id, NN | Cita pagada |
| monto | BigDecimal(10,2) | NN | Importe |
| metodo_pago | Enum MetodoPago, String(20) | NN | Medio de pago |
| fecha_pago | LocalDateTime | NN | Momento del pago |

La relación con `Cita` es `@ManyToOne(fetch = LAZY, optional = false)`; esto permite varios registros de pago para una misma cita.

Fuente: `model/Pago.java`.

## Tabla `usuarios` — entidad `Usuario`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| username | String(50) | NN; UQ `uk_usuarios_username` | Usuario de acceso |
| password_hash | String(255) | NN | Hash de contraseña |
| rol | Enum Rol, String(20) | NN; inicial `BARBERO` en Java | Rol |
| activo | Boolean | NN; inicial `true` en Java | Cuenta habilitada |

Fuente: `model/Usuario.java`. Nunca documentar valores reales de `password_hash`.

## Tabla `auditorias` — entidad `Auditoria`

| Columna | Java / longitud | Restricciones JPA | Significado |
|---|---|---|---|
| id | Long | PK, IDENTITY | Identificador |
| username | String(50) | NN | Nombre de usuario asociado al evento |
| accion | String(60) | NN | Acción |
| modulo | String(60) | NN | Módulo |
| descripcion | String(255) | — | Detalle |
| fecha | LocalDateTime | NN, no actualizable | Fecha del evento |

Índice explícito: `idx_auditorias_fecha` sobre `fecha`. El método `@PrePersist asignarFecha()` asigna `LocalDateTime.now()` si la fecha es nula. `username` es texto y **no** una relación `@ManyToOne` con `usuarios`.

Fuente: `model/Auditoria.java`.

## Enumeraciones Java (no son tablas)

| Enum | Valores | Uso verificado |
|---|---|---|
| EstadoCita | PROGRAMADA, CONFIRMADA, COMPLETADA, CANCELADA | `citas.estado` |
| MetodoPago | EFECTIVO, YAPE, PLIN, TARJETA | `pagos.metodo_pago` |
| Rol | ADMIN, BARBERO | `usuarios.rol` |
| EstadoPago | PENDIENTE, PARCIAL, PAGADO | Declarado en Java; **no** aparece como columna de las siete entidades JPA examinadas |

Fuentes: `model/EstadoCita.java`, `model/MetodoPago.java`, `model/Rol.java`, `model/EstadoPago.java`.

## Relaciones JPA verificadas

| Origen | Destino | Cardinalidad conceptual | Campo de unión |
|---|---|---|---|
| Cliente | Cita | 1 a 0..N | `citas.cliente_id` |
| Barbero | Cita | 1 a 0..N | `citas.barbero_id` |
| Servicio | Cita | 1 a 0..N | `citas.servicio_id` |
| Cita | Pago | 1 a 0..N | `pagos.cita_id` |

No se declara una FK desde `auditorias` hacia `usuarios`. Tampoco se declara relación JPA entre `Usuario` y `Barbero`.

## Pendiente de verificar en el esquema físico

- Comparar columnas, tipos, índices, claves foráneas y restricciones reales con `SHOW CREATE TABLE` para cada tabla.
- Revisar las reglas de negocio y validaciones de servicios/DTO por separado; no inferirlas únicamente de las anotaciones de entidades.
- Verificar migraciones o cambios históricos de estructura si se incorporan posteriormente.

Ver también [Diagrama entidad-relación](../diagramas/entidad-relacion.md).
