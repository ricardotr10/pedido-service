CREATE TABLE IF NOT EXISTS clientes (
    id VARCHAR PRIMARY KEY,
    activo BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE IF NOT EXISTS zonas (
    id VARCHAR PRIMARY KEY,
    soporte_refrigeracion BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS pedidos (
    id UUID PRIMARY KEY,
    numero_pedido VARCHAR(50) UNIQUE NOT NULL,
    cliente_id VARCHAR NOT NULL,
    zona_id VARCHAR NOT NULL,
    fecha_entrega DATE NOT NULL,
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'ENTREGADO')),
    requiere_refrigeracion BOOLEAN NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cargas_idempotencia (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) NOT NULL,
    archivo_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(idempotency_key, archivo_hash)
);

CREATE INDEX IF NOT EXISTS idx_pedidos_estado_fecha ON pedidos(estado, fecha_entrega);
CREATE INDEX IF NOT EXISTS idx_pedidos_numero_pedido ON pedidos(numero_pedido);
CREATE INDEX IF NOT EXISTS idx_pedidos_cliente_id ON pedidos(cliente_id);

INSERT INTO clientes (id, activo) VALUES 
    ('CLI-123', true),
    ('CLI-999', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO zonas (id, soporte_refrigeracion) VALUES 
    ('ZONA1', true),
    ('ZONA5', false)
ON CONFLICT (id) DO NOTHING;