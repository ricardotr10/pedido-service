# Pedido Service - Procesador Batch

## Instrucciones de Ejecución

### Requisitos
- Java 17+
- Maven
- PostgreSQL

### Configuración
1. Crear base de datos:
```sql
CREATE DATABASE pedidos_db;

2. Configurar application.yml:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pedidos_db
    username: postgres
    password: 1234

3. Ejecutar:

```cmd
mvn clean install
```cmd
mvn spring-boot:run

## Verificacion 

API: http://localhost:8080

API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI JSON: http://localhost:8080/v3/api-docs

# Estrategia de Batch
Configuración
```yaml
app.batch.size: 500  # Configurable entre 500-1000

## Proceso
1. Lectura streaming del CSV (no carga completa en memoria)

2. Carga de catálogos en una consulta (findAllByIds para clientes y zonas)

3. Detección de duplicados en BD (findAllByNumeroPedidoIn)

4. Validación en memoria de cada pedido

5. Inserción por lotes con saveAll cada N registros

# Colección Postman
Importar: postman/Pedidos Api.postman_collection.json

## Pruebas
1. Carga exitosa - samples/v1_pedidos_validos.csv

2. Carga con errores - samples/v2_pedidos_con_errores.csv

3. Idempotencia - samples/v3_pedidos_idempotencia.csv (misma key fija)

4. Sin Idempotency-Key (debe fallar)

5. Carga masiva 1000 registros - samples/v4_pedidos_validos_1000.csv


Autor
Ricardo Terrazas Ramos

