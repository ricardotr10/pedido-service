-- =========================
-- TABLA CLIENTES
-- =========================
CREATE TABLE clientes (
    id VARCHAR PRIMARY KEY,
    activo BOOLEAN NOT NULL
);

-- =========================
-- TABLA ZONAS
-- =========================
CREATE TABLE zonas (
    id VARCHAR PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL
);

-- =========================
-- TABLA PEDIDOS
-- =========================
CREATE TABLE pedidos (
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR NOT NULL UNIQUE,
    cliente_id VARCHAR NOT NULL,
    zona_id VARCHAR NOT NULL,
    fecha_entrega DATE NOT NULL,
    estado VARCHAR NOT NULL CHECK (estado IN ('PENDIENTE','CONFIRMADO','ENTREGADO')),
    requiere_refrigeracion BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 🔥 ÍNDICE IMPORTANTE (requisito técnico)
CREATE INDEX idx_pedidos_estado_fecha 
ON pedidos (estado, fecha_entrega);

-- =========================
-- TABLA IDEMPOTENCIA
-- =========================
CREATE TABLE cargas_idempotencia (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR NOT NULL,
    archivo_hash VARCHAR NOT NULL,
    response_json TEXT, -- 🔥 importante para devolver misma respuesta
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(idempotency_key, archivo_hash)
);

-- =========================
-- DATOS DE PRUEBA
-- =========================
INSERT INTO clientes (id, activo) VALUES 
('CLI-123', true),
('CLI-999', true);

INSERT INTO zonas (id, soporte_refrigeracion) VALUES 
('ZONA1', true),
('ZONA5', false);