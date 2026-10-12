CREATE EXTENSION IF NOT EXISTS pgcrypto;


-- =========================================================
-- ENUM-LIKE CHECK VALUES
-- =========================================================

-- user.role
-- OWNER, ADMIN, STAFF

-- working_hours.day_of_week
-- MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

-- message_templates.type
-- NEW_LEAD_RESPONSE, QUOTE_SENT, QUOTE_FOLLOW_UP,
-- BOOKING_CONFIRMATION, BOOKING_REMINDER, PAYMENT_CONFIRMATION

-- message_templates.channel
-- EMAIL, SMS, WHATSAPP

-- lead.source
-- WEBSITE, WHATSAPP, INSTAGRAM, FACEBOOK, REFERRAL, MANUAL, OTHER

-- lead.status
-- ACTIVE, CONVERTED, LOST

-- lead.qualification_status
-- PENDING, QUALIFIED, NOT_QUALIFIED

-- activity.type
-- NOTE, CALL, EMAIL, SMS, WHATSAPP,
-- STATUS_CHANGED, QUOTE_SENT, QUOTE_ACCEPTED,
-- BOOKING_CREATED, PAYMENT_RECEIVED

-- activity.direction
-- INBOUND, OUTBOUND, INTERNAL

-- pipeline_stage.type
-- NEW, QUALIFIED, QUOTED, WON, LOST

-- follow_up.channel
-- EMAIL, SMS, WHATSAPP

-- follow_up.status
-- PENDING, COMPLETED, CANCELLED, FAILED

-- quote.status
-- DRAFT, SENT, ACCEPTED, REJECTED, EXPIRED, CANCELLED

-- booking.status
-- PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW

-- payment.status
-- PENDING, SUCCESS, FAILED, REFUNDED


-- =========================================================
-- ORGANIZATION
-- =========================================================

CREATE TABLE organization (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(150) NOT NULL,
    slug            VARCHAR(150) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(50),
    address         TEXT,
    logo_url        VARCHAR(500),
    timezone        VARCHAR(50) NOT NULL DEFAULT 'UTC',
    currency        CHAR(3) NOT NULL DEFAULT 'USD',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_organization_slug UNIQUE (slug)
);


-- =========================================================
-- USER
-- =========================================================

CREATE TABLE app_user (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    password_hash   VARCHAR(255),
    role            VARCHAR(20) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_user_organization_email
        UNIQUE (organization_id, email),

    CONSTRAINT chk_user_role
        CHECK (role IN ('OWNER', 'ADMIN', 'STAFF'))
);


-- =========================================================
-- WORKING HOURS
-- =========================================================

CREATE TABLE working_hours (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    day_of_week     VARCHAR(10) NOT NULL,
    open_time       TIME NOT NULL,
    close_time      TIME NOT NULL,

    CONSTRAINT fk_working_hours_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_working_hours_day
        UNIQUE (organization_id, day_of_week),

    CONSTRAINT chk_working_hours_day
        CHECK (
            day_of_week IN (
                'MONDAY',
                'TUESDAY',
                'WEDNESDAY',
                'THURSDAY',
                'FRIDAY',
                'SATURDAY',
                'SUNDAY'
            )
        ),

    CONSTRAINT chk_working_hours_time
        CHECK (open_time < close_time)
);


-- =========================================================
-- SERVICE
-- =========================================================

CREATE TABLE service (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    price           NUMERIC(12,2) NOT NULL DEFAULT 0,
    duration_minutes INTEGER,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_service_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_service_price
        CHECK (price >= 0),

    CONSTRAINT chk_service_duration
        CHECK (duration_minutes IS NULL OR duration_minutes > 0)
);


-- =========================================================
-- MESSAGE TEMPLATE
-- =========================================================

CREATE TABLE message_template (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    type            VARCHAR(40) NOT NULL,
    channel         VARCHAR(20) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    subject         VARCHAR(255),
    body            TEXT NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_message_template_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_message_template_type
        CHECK (
            type IN (
                'NEW_LEAD_RESPONSE',
                'QUOTE_SENT',
                'QUOTE_FOLLOW_UP',
                'BOOKING_CONFIRMATION',
                'BOOKING_REMINDER',
                'PAYMENT_CONFIRMATION'
            )
        ),

    CONSTRAINT chk_message_template_channel
        CHECK (
            channel IN ('EMAIL', 'SMS', 'WHATSAPP')
        )
);


-- =========================================================
-- PIPELINE
-- =========================================================

CREATE TABLE pipeline (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name            VARCHAR(150) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pipeline_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE
);


-- =========================================================
-- PIPELINE STAGE
-- =========================================================

CREATE TABLE pipeline_stage (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id UUID NOT NULL,
    name        VARCHAR(100) NOT NULL,
    position    INTEGER NOT NULL,
    type        VARCHAR(20) NOT NULL,

    CONSTRAINT fk_pipeline_stage_pipeline
        FOREIGN KEY (pipeline_id)
        REFERENCES pipeline(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_pipeline_stage_position
        UNIQUE (pipeline_id, position),

    CONSTRAINT uq_pipeline_stage_name
        UNIQUE (pipeline_id, name),

    CONSTRAINT chk_pipeline_stage_position
        CHECK (position >= 0),

    CONSTRAINT chk_pipeline_stage_type
        CHECK (
            type IN (
                'NEW',
                'QUALIFIED',
                'QUOTED',
                'WON',
                'LOST'
            )
        )
);


-- =========================================================
-- LEAD
-- =========================================================

CREATE TABLE lead (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id       UUID NOT NULL,
    first_name            VARCHAR(100) NOT NULL,
    last_name             VARCHAR(100),
    email                 VARCHAR(255),
    phone                 VARCHAR(50),
    source                VARCHAR(30) NOT NULL,
    status                VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    qualification_status  VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    notes                 TEXT,
    assigned_to           UUID,
    pipeline_stage_id     UUID,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    stage_changed_at      TIMESTAMPTZ,

    CONSTRAINT fk_lead_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_lead_assigned_user
        FOREIGN KEY (assigned_to)
        REFERENCES app_user(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_lead_pipeline_stage
        FOREIGN KEY (pipeline_stage_id)
        REFERENCES pipeline_stage(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_lead_source
        CHECK (
            source IN (
                'WEBSITE',
                'WHATSAPP',
                'INSTAGRAM',
                'FACEBOOK',
                'REFERRAL',
                'MANUAL',
                'OTHER'
            )
        ),

    CONSTRAINT chk_lead_status
        CHECK (
            status IN (
                'ACTIVE',
                'CONVERTED',
                'LOST'
            )
        ),

    CONSTRAINT chk_lead_qualification_status
        CHECK (
            qualification_status IN (
                'PENDING',
                'QUALIFIED',
                'NOT_QUALIFIED'
            )
        )
);


-- =========================================================
-- LEAD <-> SERVICE
-- =========================================================

CREATE TABLE lead_service (
    lead_id    UUID NOT NULL,
    service_id UUID NOT NULL,

    PRIMARY KEY (lead_id, service_id),

    CONSTRAINT fk_lead_service_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_lead_service_service
        FOREIGN KEY (service_id)
        REFERENCES service(id)
        ON DELETE CASCADE
);


-- =========================================================
-- CUSTOMER
-- =========================================================

CREATE TABLE customer (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    lead_id         UUID NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100),
    email           VARCHAR(255),
    phone           VARCHAR(50),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_customer_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_customer_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_customer_lead
        UNIQUE (lead_id)
);


-- =========================================================
-- ACTIVITY
-- =========================================================

CREATE TABLE activity (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    lead_id         UUID NOT NULL,
    user_id         UUID,
    type            VARCHAR(30) NOT NULL,
    direction       VARCHAR(20),
    subject         VARCHAR(255),
    content         TEXT,
    occurred_at     TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_activity_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_activity_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_activity_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_activity_type
        CHECK (
            type IN (
                'NOTE',
                'CALL',
                'EMAIL',
                'SMS',
                'WHATSAPP',
                'STATUS_CHANGED',
                'QUOTE_SENT',
                'QUOTE_ACCEPTED',
                'BOOKING_CREATED',
                'PAYMENT_RECEIVED'
            )
        ),

    CONSTRAINT chk_activity_direction
        CHECK (
            direction IS NULL OR
            direction IN ('INBOUND', 'OUTBOUND', 'INTERNAL')
        )
);


-- =========================================================
-- FOLLOW UP
-- =========================================================

CREATE TABLE follow_up (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id     UUID NOT NULL,
    lead_id             UUID NOT NULL,
    type                VARCHAR(50) NOT NULL,
    scheduled_at        TIMESTAMPTZ NOT NULL,
    channel             VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message_template_id UUID,
    completed_at        TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_follow_up_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_follow_up_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_follow_up_message_template
        FOREIGN KEY (message_template_id)
        REFERENCES message_template(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_follow_up_channel
        CHECK (
            channel IN ('EMAIL', 'SMS', 'WHATSAPP')
        ),

    CONSTRAINT chk_follow_up_status
        CHECK (
            status IN (
                'PENDING',
                'COMPLETED',
                'CANCELLED',
                'FAILED'
            )
        )
);


-- =========================================================
-- QUOTE
-- =========================================================

CREATE TABLE quote (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    lead_id         UUID NOT NULL,
    quote_number    VARCHAR(50) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    subtotal        NUMERIC(12,2) NOT NULL DEFAULT 0,
    discount        NUMERIC(12,2) NOT NULL DEFAULT 0,
    tax             NUMERIC(12,2) NOT NULL DEFAULT 0,
    total           NUMERIC(12,2) NOT NULL DEFAULT 0,
    currency        CHAR(3) NOT NULL,
    valid_until     DATE,
    notes           TEXT,
    sent_at         TIMESTAMPTZ,
    responded_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_quote_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_quote_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_quote_number
        UNIQUE (organization_id, quote_number),

    CONSTRAINT chk_quote_status
        CHECK (
            status IN (
                'DRAFT',
                'SENT',
                'ACCEPTED',
                'REJECTED',
                'EXPIRED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_quote_amounts
        CHECK (
            subtotal >= 0 AND
            discount >= 0 AND
            tax >= 0 AND
            total >= 0
        )
);


-- =========================================================
-- QUOTE ITEM
-- =========================================================

CREATE TABLE quote_item (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id    UUID NOT NULL,
    service_id  UUID,
    description TEXT NOT NULL,
    quantity    NUMERIC(10,2) NOT NULL DEFAULT 1,
    unit_price  NUMERIC(12,2) NOT NULL,
    line_total  NUMERIC(12,2) NOT NULL,

    CONSTRAINT fk_quote_item_quote
        FOREIGN KEY (quote_id)
        REFERENCES quote(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_quote_item_service
        FOREIGN KEY (service_id)
        REFERENCES service(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_quote_item_quantity
        CHECK (quantity > 0),

    CONSTRAINT chk_quote_item_unit_price
        CHECK (unit_price >= 0),

    CONSTRAINT chk_quote_item_line_total
        CHECK (line_total >= 0)
);


-- =========================================================
-- BOOKING
-- =========================================================

CREATE TABLE booking (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    lead_id         UUID NOT NULL,
    service_id      UUID NOT NULL,
    assigned_user_id UUID,
    start_time      TIMESTAMPTZ NOT NULL,
    end_time        TIMESTAMPTZ NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes           TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_booking_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_booking_service
        FOREIGN KEY (service_id)
        REFERENCES service(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_booking_assigned_user
        FOREIGN KEY (assigned_user_id)
        REFERENCES app_user(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_booking_time
        CHECK (start_time < end_time),

    CONSTRAINT chk_booking_status
        CHECK (
            status IN (
                'PENDING',
                'CONFIRMED',
                'COMPLETED',
                'CANCELLED',
                'NO_SHOW'
            )
        )
);


-- =========================================================
-- PAYMENT
-- =========================================================

CREATE TABLE payment (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id   UUID NOT NULL,
    lead_id           UUID NOT NULL,
    quote_id          UUID,
    booking_id        UUID,
    amount            NUMERIC(12,2) NOT NULL,
    currency          CHAR(3) NOT NULL,
    provider          VARCHAR(50) NOT NULL,
    provider_reference VARCHAR(255),
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    paid_at           TIMESTAMPTZ,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_payment_lead
        FOREIGN KEY (lead_id)
        REFERENCES lead(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_payment_quote
        FOREIGN KEY (quote_id)
        REFERENCES quote(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_payment_booking
        FOREIGN KEY (booking_id)
        REFERENCES booking(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_payment_amount
        CHECK (amount > 0),

    CONSTRAINT chk_payment_status
        CHECK (
            status IN (
                'PENDING',
                'SUCCESS',
                'FAILED',
                'REFUNDED'
            )
        ),

    CONSTRAINT uq_payment_provider_reference
        UNIQUE (provider, provider_reference)
);


-- =========================================================
-- AUTOMATION RULE
-- =========================================================

CREATE TABLE automation_rule (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id     UUID NOT NULL,
    name                VARCHAR(150) NOT NULL,
    trigger             VARCHAR(100) NOT NULL,
    delay_seconds       INTEGER NOT NULL DEFAULT 0,
    action              VARCHAR(100) NOT NULL,
    message_template_id UUID,
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_automation_rule_organization
        FOREIGN KEY (organization_id)
        REFERENCES organization(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_automation_rule_message_template
        FOREIGN KEY (message_template_id)
        REFERENCES message_template(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_automation_rule_delay
        CHECK (delay_seconds >= 0)
);


-- =========================================================
-- INDEXES
-- =========================================================

CREATE INDEX idx_user_organization
    ON app_user(organization_id);

CREATE INDEX idx_service_organization
    ON service(organization_id);

CREATE INDEX idx_lead_organization
    ON lead(organization_id);

CREATE INDEX idx_lead_assigned_to
    ON lead(assigned_to);

CREATE INDEX idx_lead_pipeline_stage
    ON lead(pipeline_stage_id);

CREATE INDEX idx_lead_created_at
    ON lead(organization_id, created_at DESC);

CREATE INDEX idx_activity_lead
    ON activity(lead_id, occurred_at DESC);

CREATE INDEX idx_activity_organization
    ON activity(organization_id);

CREATE INDEX idx_follow_up_scheduled
    ON follow_up(organization_id, scheduled_at);

CREATE INDEX idx_quote_lead
    ON quote(lead_id);

CREATE INDEX idx_booking_lead
    ON booking(lead_id);

CREATE INDEX idx_booking_start_time
    ON booking(organization_id, start_time);

CREATE INDEX idx_payment_lead
    ON payment(lead_id);

CREATE INDEX idx_automation_rule_organization
    ON automation_rule(organization_id);