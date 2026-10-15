ALTER TABLE lead
    ADD COLUMN external_id VARCHAR(255);

ALTER TABLE lead
    ADD CONSTRAINT uq_lead_organization_external_id
        UNIQUE (organization_id, external_id);

ALTER TABLE lead
    DROP CONSTRAINT chk_lead_source,
    ADD CONSTRAINT chk_lead_source
        CHECK (source IN (
            'WEBSITE',
            'WEBSITE_CONTACTFORM',
            'WEBSITE_CHATBOX',
            'WHATSAPP',
            'INSTAGRAM',
            'FACEBOOK',
            'REFERRAL',
            'MANUAL',
            'OTHER'
        ));

ALTER TABLE activity
    DROP CONSTRAINT chk_activity_type,
    ADD CONSTRAINT chk_activity_type
        CHECK (type IN (
            'NOTE',
            'CALL',
            'EMAIL',
            'SMS',
            'WHATSAPP',
            'SOCIAL_MEDIA_CHAT',
            'WEB_CHAT',
            'STATUS_CHANGED',
            'QUOTE_SENT',
            'QUOTE_ACCEPTED',
            'BOOKING_CREATED',
            'PAYMENT_RECEIVED'
        ));