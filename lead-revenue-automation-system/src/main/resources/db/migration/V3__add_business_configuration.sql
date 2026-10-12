UPDATE organization_settings
SET ai_tone = 'PROFESSIONAL'
WHERE ai_tone IS NULL
   OR ai_tone NOT IN ('PROFESSIONAL', 'FRIENDLY', 'CONCISE', 'FORMAL', 'CASUAL');

ALTER TABLE organization_settings
    ALTER COLUMN ai_tone TYPE VARCHAR(30),
    ALTER COLUMN ai_tone SET DEFAULT 'PROFESSIONAL',
    ALTER COLUMN ai_tone SET NOT NULL,
    ADD CONSTRAINT chk_organization_settings_ai_tone
        CHECK (ai_tone IN ('PROFESSIONAL', 'FRIENDLY', 'CONCISE', 'FORMAL', 'CASUAL'));

CREATE TABLE quote_policies (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id         UUID NOT NULL UNIQUE,
    require_inspection      BOOLEAN NOT NULL DEFAULT FALSE,
    require_approval        BOOLEAN NOT NULL DEFAULT TRUE,
    default_validity_days   INTEGER NOT NULL DEFAULT 7,
    default_tax_rate        NUMERIC(7, 4) NOT NULL DEFAULT 0,
    allow_discounts         BOOLEAN NOT NULL DEFAULT FALSE,
    max_discount_percent    NUMERIC(5, 2) NOT NULL DEFAULT 0,
    terms_and_conditions    TEXT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quote_policies_organization
        FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT chk_quote_validity_days CHECK (default_validity_days > 0),
    CONSTRAINT chk_quote_tax_rate CHECK (default_tax_rate >= 0 AND default_tax_rate <= 100),
    CONSTRAINT chk_quote_max_discount CHECK (max_discount_percent >= 0 AND max_discount_percent <= 100),
    CONSTRAINT chk_quote_discount_policy CHECK (allow_discounts OR max_discount_percent = 0)
);

CREATE TABLE booking_policies (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id             UUID NOT NULL UNIQUE,
    booking_enabled             BOOLEAN NOT NULL DEFAULT TRUE,
    slot_duration_minutes       INTEGER NOT NULL DEFAULT 60,
    buffer_minutes              INTEGER NOT NULL DEFAULT 0,
    minimum_notice_minutes      INTEGER NOT NULL DEFAULT 60,
    maximum_advance_days        INTEGER NOT NULL DEFAULT 30,
    require_confirmation        BOOLEAN NOT NULL DEFAULT TRUE,
    cancellation_notice_hours   INTEGER NOT NULL DEFAULT 24,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_policies_organization
        FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_slot_duration CHECK (slot_duration_minutes > 0),
    CONSTRAINT chk_booking_buffer CHECK (buffer_minutes >= 0),
    CONSTRAINT chk_booking_minimum_notice CHECK (minimum_notice_minutes >= 0),
    CONSTRAINT chk_booking_advance_days CHECK (maximum_advance_days > 0),
    CONSTRAINT chk_booking_cancellation_notice CHECK (cancellation_notice_hours >= 0)
);

CREATE TABLE follow_up_policies (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id             UUID NOT NULL UNIQUE,
    enabled                     BOOLEAN NOT NULL DEFAULT FALSE,
    max_attempts                INTEGER NOT NULL DEFAULT 3,
    minimum_interval_minutes    INTEGER NOT NULL DEFAULT 1440,
    stop_on_reply               BOOLEAN NOT NULL DEFAULT TRUE,
    stop_on_won_or_lost         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follow_up_policies_organization
        FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT chk_follow_up_max_attempts CHECK (max_attempts >= 0),
    CONSTRAINT chk_follow_up_interval CHECK (minimum_interval_minutes > 0)
);

CREATE TABLE custom_field_definitions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id     UUID NOT NULL,
    entity_type         VARCHAR(20) NOT NULL,
    field_key           VARCHAR(100) NOT NULL,
    label               VARCHAR(150) NOT NULL,
    data_type           VARCHAR(30) NOT NULL,
    required            BOOLEAN NOT NULL DEFAULT FALSE,
    options             JSONB NOT NULL DEFAULT '[]'::jsonb,
    validation_rules   JSONB NOT NULL DEFAULT '{}'::jsonb,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_custom_field_definitions_organization
        FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT uq_custom_field_key UNIQUE (organization_id, entity_type, field_key),
    CONSTRAINT chk_custom_field_entity_type CHECK (entity_type IN ('LEAD', 'SERVICE')),
    CONSTRAINT chk_custom_field_data_type
        CHECK (data_type IN ('TEXT', 'NUMBER', 'BOOLEAN', 'DATE', 'SELECT', 'MULTI_SELECT')),
    CONSTRAINT chk_custom_field_options CHECK (jsonb_typeof(options) = 'array'),
    CONSTRAINT chk_custom_field_validation_rules CHECK (jsonb_typeof(validation_rules) = 'object')
);

CREATE TABLE qualification_questions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id     UUID NOT NULL,
    service_id          UUID,
    question_text       TEXT NOT NULL,
    answer_type         VARCHAR(30) NOT NULL,
    options             JSONB NOT NULL DEFAULT '[]'::jsonb,
    linked_field_key    VARCHAR(100),
    required            BOOLEAN NOT NULL DEFAULT FALSE,
    display_order       INTEGER NOT NULL DEFAULT 0,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_qualification_questions_organization
        FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE,
    CONSTRAINT fk_qualification_questions_service
        FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE,
    CONSTRAINT chk_qualification_answer_type
        CHECK (answer_type IN ('TEXT', 'NUMBER', 'BOOLEAN', 'SINGLE_CHOICE', 'MULTIPLE_CHOICE')),
    CONSTRAINT chk_qualification_options CHECK (jsonb_typeof(options) = 'array'),
    CONSTRAINT chk_qualification_question_text CHECK (length(trim(question_text)) > 0)
);

CREATE INDEX idx_custom_fields_org_entity
    ON custom_field_definitions (organization_id, entity_type, active);

CREATE INDEX idx_qualification_questions_org
    ON qualification_questions (organization_id, active, display_order);

CREATE INDEX idx_qualification_questions_service
    ON qualification_questions (service_id)
    WHERE service_id IS NOT NULL;