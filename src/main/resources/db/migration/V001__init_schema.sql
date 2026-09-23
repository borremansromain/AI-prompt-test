-- ============================================================
-- V001__init_schema.sql (Prompt 1.4)
-- Flyway = source de verite du schema. Hibernate: ddl-auto=validate.
-- Noms de colonnes a faire correspondre aux entities JPA (phase 5.3).
-- ============================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE categories (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku             VARCHAR(50)   NOT NULL UNIQUE,
    name            VARCHAR(255)  NOT NULL,
    category_id     UUID          NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    price           NUMERIC(10,2) NOT NULL CHECK (price > 0),
    quantity        INT           NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    alert_threshold INT           NOT NULL DEFAULT 0 CHECK (alert_threshold >= 0),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE TABLE stock_movements (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id      UUID          NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    type            VARCHAR(10)   NOT NULL CHECK (type IN ('ENTRY','EXIT')),
    quantity        INT           NOT NULL CHECK (quantity > 0),
    reason          TEXT,
    author_username VARCHAR(255)  NOT NULL,
    movement_date   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_stock_movements_product_date ON stock_movements(product_id, movement_date DESC);
