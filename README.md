# 📦 API de Pedidos - Procesador Batch

## 📌 Descripción

Microservicio desarrollado en **Java 17 + Spring Boot 3** para la carga masiva de pedidos desde un archivo CSV.
Implementa **arquitectura hexagonal**, validaciones de negocio, procesamiento eficiente en batch e **idempotencia** para evitar duplicidad de operaciones.

---

## 🎯 Objetivo

Construir un servicio que:

* Reciba un archivo CSV de pedidos
* Valide cada registro según reglas de negocio
* Persista únicamente los pedidos válidos
* Retorne un resumen detallado del procesamiento
* Evite reprocesamiento mediante idempotencia

---

## 🏗️ Arquitectura

Se implementa **arquitectura hexagonal (Ports & Adapters)**:

```
com.rest.api
│
├── domain          → Modelo de dominio y reglas de negocio
├── application     → Casos de uso (orquestación)
├── infrastructure  → Adaptadores (REST, JPA, config)
└── util            → Utilidades (hash SHA-256)
```

### Separación:

* **Domain:** entidades (`Pedido`, `Cliente`, `Zona`) + `PedidoValidator`
* **Application:** lógica batch (`PedidoBatchService`)
* **Ports:** interfaces de persistencia
* **Adapters:**

  * REST (entrada)
  * JPA (salida)

---

## 📥 Formato de Entrada

* Archivo: CSV (UTF-8)
* Delimitador: `,`

### Columnas:

```
numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion
```

### Valores válidos:

* estado: `PENDIENTE`, `CONFIRMADO`, `ENTREGADO`
* requiereRefrigeracion: `true` | `false`

### Ejemplo:

```
numeroPedido,clienteId,fechaEntrega,estado,zonaEntrega,requiereRefrigeracion
P001,CLI-123,2025-08-10,PENDIENTE,ZONA1,true
P002,CLI-999,2025-08-12,ENTREGADO,ZONA5,false
```

---

## 🚀 Endpoint

### POST `/pedidos/cargar`

#### Headers:

```
Idempotency-Key: <valor-unico>
Authorization: Bearer <token>
```

#### Request:

* Tipo: `multipart/form-data`
* Campo: `file`

#### Response:

```json
{
  "totalProcesados": 2,
  "guardados": 1,
  "conError": 1,
  "errores": [
    {
      "linea": 2,
      "motivo": "CLIENTE_NO_ENCONTRADO"
    }
  ],
  "erroresAgrupados": {
    "CLIENTE_NO_ENCONTRADO": 1
  }
}
```

---

## ✅ Reglas de Negocio

Se validan las siguientes condiciones:

* `numeroPedido`: alfanumérico y único
* `clienteId`: debe existir y estar activo
* `fechaEntrega`: no puede ser pasada (zona America/Lima)
* `estado`: valores permitidos
* `zonaEntrega`: debe existir
* Cadena de frío:

  * Si `requiereRefrigeracion = true`
  * Entonces `zona.soporte_refrigeracion = true`

---

## ⚡ Procesamiento Batch

Se implementa procesamiento eficiente:

* Lectura masiva de clientes y zonas (`findAllByIds`)
* Detección de duplicados en BD (`findAllByNumeroPedidoIn`)
* Inserción en bloques (`saveAll`)

### Configuración:

```yaml
app:
  batch:
    size: 500
```

### Optimización Hibernate:

```yaml
hibernate:
  jdbc:
    batch_size: 500
  order_inserts: true
  order_updates: true
```

---

## 🔁 Idempotencia

Se garantiza que una misma petición no se procese más de una vez.

### Estrategia:

* Header obligatorio: `Idempotency-Key`
* Hash SHA-256 del archivo
* Persistencia en tabla `cargas_idempotencia`
* Si ya existe:
  → se retorna la misma respuesta almacenada

### Manejo de concurrencia:

* Control mediante constraint único `(idempotency_key, archivo_hash)`
* Retry en caso de colisión

---

## 🗄️ Base de Datos

### Motor: PostgreSQL

### Tablas:

#### pedidos

* id (UUID)
* numero_pedido (UNIQUE)
* cliente_id
* zona_id
* fecha_entrega
* estado
* requiere_refrigeracion
* created_at, updated_at

#### clientes

* id
* activo

#### zonas

* id
* soporte_refrigeracion

#### cargas_idempotencia

* id
* idempotency_key
* archivo_hash
* response_json
* created_at

### Índices:

* `numero_pedido` (unique)
* `(estado, fecha_entrega)`

---

## 🔐 Seguridad

* OAuth2 Resource Server (JWT)
* Todas las rutas protegidas
* Swagger habilitado sin autenticación para pruebas

---

## 📊 Observabilidad

* Logs en formato JSON
* Uso de `correlationId` por request
* Filtro HTTP (`CorrelationIdFilter`)

Ejemplo:

```json
{
  "timestamp": "...",
  "level": "INFO",
  "correlationId": "...",
  "message": "Procesando pedidos"
}
```

---

## 📄 API Documentation

* OpenAPI: `/v3/api-docs`
* Swagger UI: `/swagger-ui.html`

---

## 🧪 Testing

* Tests unitarios en `PedidoValidator`
* Cobertura de reglas de negocio principales
* Cobertura estimada: ≥ 80% en dominio

---

## ▶️ Ejecución Local

### 1. Requisitos

* Java 17+
* Maven
* PostgreSQL

### 2. Crear base de datos

```sql
CREATE DATABASE pedidos_db;
```

### 3. Configurar `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pedidos_db
    username: postgres
    password: 1234
```

### 4. Ejecutar aplicación

```bash
mvn spring-boot:run
```

---

## 📁 Archivos de Prueba

Ubicados en `/samples`:

* CSV válido
* CSV con errores

---

## 🧰 Colección Postman

Se incluye colección para probar:

* POST `/pedidos/cargar`
* Configuración de headers
* Ejemplos de carga

---

## ⚠️ Supuestos

* Los catálogos (`clientes`, `zonas`) ya existen
* No se manejan relaciones físicas (FK)
* Tamaño máximo del archivo: 5MB
* Máximo esperado: ~1000 registros

---

## 🚧 Limitaciones

* El archivo se carga completamente en memoria
* Validación de headers no estricta (orden)
* No se implementa streaming del CSV
* Seguridad JWT configurada con issuer dummy

---

## 🧠 Decisiones de Diseño

* Uso de arquitectura hexagonal para desacoplamiento
* Validaciones en dominio (`PedidoValidator`)
* Orquestación en capa application
* Batch processing para eficiencia
* Idempotencia a nivel de infraestructura

---

## 📈 Criterios Cumplidos

✔ Arquitectura hexagonal
✔ Validaciones completas
✔ Procesamiento batch real
✔ Idempotencia robusta
✔ Seguridad con JWT
✔ Observabilidad con logs estructurados
✔ Testing de dominio

---

## 👨‍💻 Autor

Desarrollado como parte de prueba técnica Backend Java.
