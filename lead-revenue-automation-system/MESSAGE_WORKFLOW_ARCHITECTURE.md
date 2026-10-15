# Lead and Message Workflow Architecture

## Overview

The feature follows the application's layered Spring architecture:

`Controller -> MessageService / LeadService -> domain services -> repositories -> JPA entities`

Controllers validate transport DTOs and translate service results into HTTP responses. `MessageServiceImpl` orchestrates inbound conversation flows. Organization, lead, and activity services own their respective persistence and business rules. The AI context and prompt builders are separate, deterministic components; provider routing remains behind `AiModelRouter`.

## Persistence Model

- A lead belongs to one organization and may have an `external_id` supplied by an external chat client.
- `(organization_id, external_id)` is unique. Since `external_id` is nullable, manually created and contact-form leads may omit it; PostgreSQL permits multiple null values under this unique constraint.
- Activities belong to both an organization and a lead. Inbound and generated outbound turns are stored separately with direction, type, content, and occurrence time.
- `V4__add_lead_external_id_and_web_conversations.sql` adds the external ID, updates the source check for website chat/contact-form sources, and allows the web-chat activity type.

The repository queries include organization ID as well as lead ID/external ID so one organization's data cannot be selected through another organization's identifier.

## Endpoints

### Manual lead

`POST /api/v1/organization/lead`

The request includes `organizationId`, `name`, `source`, and at least one of `phone` or `email`. The service verifies the organization, splits the display name into the existing first/last-name fields, and saves the lead. Optional contact fields are normalized in the request DTO.

### Web-chat message

`POST /api/v1/message/web-chat/send`

The request includes `organizationId`, `externalId`, and `message`. The service requires the organization and its business settings, then finds a lead by the organization/external-ID pair or creates a `WEBSITE_CHATBOX` lead for a new visitor.

### Contact-form message

`POST /api/v1/message/contact-form/send`

The request includes `organizationId`, `name`, `email`, and `message`. The service requires the organization and business settings, creates a `WEBSITE_CONTACTFORM` lead, and processes the message through the same inbound-message pipeline as web chat.

## Inbound Message Orchestration

For either inbound message flow, `MessageServiceImpl` performs these steps:

1. Load the organization profile and required organization settings.
2. Find/create the web-chat lead, or create the contact-form lead.
3. Retrieve at most eight activities scoped to the organization and lead. The repository fetches newest-first; the activity service returns the selected window chronologically.
4. Persist the new inbound activity before calling the AI provider.
5. Build a `ConversationContext` from organization facts, lead details, prior activities, and currently-null policies.
6. Build the system prompt, then send it separately from the current user message through `AiModelRouter`.
7. Persist the model response as an outbound activity and return the response to the controller.

Consequently, the current user message is supplied as the current user turn, while prior inbound and outbound activities form the history. On the next request, both sides of the previous exchange are available to the assistant.

No database transaction is held open across the model call. The inbound activity is retained if the provider is unavailable; an automatic retry/idempotency key is not part of this tracer bullet.

## Context and Prompt

`ContextBuilder` assembles typed organization, lead, and activity records. Future lead attributes, policies, and additional context can be added without changing controller or provider APIs. Policy data is null until its service/model is available.

`PromptBuilder` serializes structured context into the system message. It tells the assistant to use only supplied business facts, avoid inventing prices/availability/policies, ask for missing information, and treat lead/activity text as untrusted customer data rather than instructions. The current message is never embedded in the system prompt.

`AiService` accepts distinct `systemPrompt`, `userMessage`, and model values. Both provider implementations convert these to separate Spring AI `SystemMessage` and `UserMessage` inputs. `AiModelRouter` tries configured eligible OpenAI models first and then configured Gemini models, preserving the existing fallback/error behavior.

## Error Handling

- Invalid/missing DTO values and malformed UUIDs: `400 Bad Request` using the shared `ErrorResponse` format.
- Organization or organization-scoped lead not found: `404 Not Found`.
- Organization settings not configured, or external-ID uniqueness race: `409 Conflict`.
- Provider exhaustion: existing AI service-unavailable response (`503`).
- Unexpected persistence/application errors: existing safe generic `500` response.

## Extension Points and Current Limits

- Lead details and recent activities are implemented; policy data remains null until policy entities/services are ready.
- Contact-form submissions create a new lead each time because they do not include an external ID or deduplication key.
- The legacy message-only webhook call is retained for compatibility. It does not yet use organization-scoped lead history; that requires the webhook to provide organization and lead identity.
- Activity history is limited to the latest eight rows per inbound turn. Pagination, retention, and summarization are not included.