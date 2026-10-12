CREATE TABLE organization_settings (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id         UUID NOT NULL UNIQUE,
    business_description    TEXT,
    ai_instructions         TEXT,
    ai_tone                 VARCHAR(100),
    auto_reply_enabled      BOOLEAN NOT NULL DEFAULT FALSE,
    require_human_approval  BOOLEAN NOT NULL DEFAULT TRUE,
    settings                JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_organization_settings_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE
);