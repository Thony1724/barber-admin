# Diagrama entidad-relación — Barber Admin

El siguiente diagrama Mermaid representa las **siete entidades JPA verificadas** en `src/main/java/com/practicando/practicando/model/` y sus cuatro relaciones `@ManyToOne`. Los tipos se muestran de forma conceptual, basados en las clases Java; no sustituyen la definición SQL de MariaDB.

```mermaid
erDiagram
    CLIENTES ||--o{ CITAS : reserva
    BARBEROS ||--o{ CITAS : atiende
    SERVICIOS ||--o{ CITAS : corresponde
    CITAS ||--o{ PAGOS : recibe

    CLIENTES {
        bigint id PK
        varchar nombre
        varchar apellido
        varchar telefono
        varchar email UK
    }

    BARBEROS {
        bigint id PK
        varchar nombre
        varchar apellido
        varchar telefono
        varchar email UK
        varchar especialidad
        boolean activo
    }

    SERVICIOS {
        bigint id PK
        varchar nombre
        varchar descripcion
        decimal precio
        int duracion_minutos
        boolean activo
    }

    CITAS {
        bigint id PK
        bigint cliente_id FK
        bigint barbero_id FK
        bigint servicio_id FK
        date fecha
        time hora
        varchar estado
        int duracion_minutos
        decimal precio
    }

    PAGOS {
        bigint id PK
        bigint cita_id FK
        decimal monto
        varchar metodo_pago
        datetime fecha_pago
    }

    USUARIOS {
        bigint id PK
        varchar username UK
        varchar password_hash
        varchar rol
        boolean activo
    }

    AUDITORIAS {
        bigint id PK
        varchar username
        varchar accion
        varchar modulo
        varchar descripcion
        datetime fecha
    }
```

## Lectura del modelo

- Un cliente, un barbero o un servicio puede aparecer en muchas citas; cada cita exige exactamente una referencia a cada uno.
- Una cita puede recibir cero o más pagos; cada pago pertenece a una cita.
- `USUARIOS` y `AUDITORIAS` aparecen sin conectores porque **no existe una relación JPA explícita** entre ellos. El campo `auditorias.username` guarda texto.
- `EstadoCita`, `MetodoPago`, `Rol` y `EstadoPago` son enumeraciones Java, no entidades; los valores persistidos de los tres primeros se almacenan como texto.
- Las cardinalidades se deducen de `@ManyToOne(optional=false)` y no expresan reglas adicionales de negocio.

## Archivos fuente

- `model/Cliente.java`
- `model/Barbero.java`
- `model/Servicio.java`
- `model/Cita.java`
- `model/Pago.java`
- `model/Usuario.java`
- `model/Auditoria.java`

Para atributos, longitudes, precisión decimal y restricciones, consultar [Diccionario de datos](../base-datos/diccionario-datos.md).

> **Nota:** este diagrama documenta el modelo JPA del repositorio, no una introspección de la base de datos instalada.
