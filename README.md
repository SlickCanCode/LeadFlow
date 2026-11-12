# LeadFlow — lead response automation tracer bullet

LeadFlow began as an idea for a SaaS product that would help businesses respond to and manage incoming leads. The longer-term vision was a platform that could use each business's information to answer prospect questions and automate parts of the lead journey.

This repository is an **unfinished tracer bullet**, not that complete SaaS product. It validates one narrow path from a marketing landing page to an AI-backed business chat API: when an organization and its business information have been configured in the backend database, a visitor can ask a question and receive an answer grounded in that information. A small amount of lead and conversation data is persisted along the way.

## Why development paused

The project was built to test the idea and make the core experience tangible. While pitching it, the developer found that established products already served this space with stronger, more complete feature sets. Development was therefore paused rather than continuing toward a broad commercial SaaS offering.

The code is left as a starting point for someone who wants to explore the idea, learn from the implementation, or continue building it. The product direction, architecture, and scope are open to change. This README describes the current repository honestly so that the prototype is not mistaken for a finished or production-ready service.

## What is here today

- **`LeadFlow/`** — a Next.js 14 landing page for the proposed product, including a chat UI and a same-origin API route that forwards chat messages to the backend. Contact form delivery can use Web3Forms.
- **`lead-revenue-automation-system/`** — a Java 21 / Spring Boot REST API with organization and business-settings setup, lead creation, web-chat and contact-form message handling, conversation activity persistence, and OpenAI/Gemini model routing.
- **`API_DOCUMENTATION.md`** — backend endpoint details and example payloads.
- **`AI_CHAT_IMPLEMENTATION_NOTE.md`** and **`MESSAGE_WORKFLOW_ARCHITECTURE.md`** — implementation notes for AI routing and the current message/lead flow.

The backend uses PostgreSQL and Flyway in its production profile, and has an H2 in-memory development profile. The domain model contains other concepts, but their presence in code does not mean the corresponding product workflows are complete or exposed.

## Current limits

This is a proof of concept, not a complete multi-tenant SaaS application. It does not currently provide a finished customer dashboard, self-service onboarding, billing, a complete CRM, a production-ready authentication and authorization model, or a complete set of lead-automation workflows. Some integrations and domain concepts are partial or disabled. In particular, the WhatsApp webhook is not enabled for live use.

The public message endpoints need deployment-edge abuse controls before exposure to the internet. Review the security and deployment notes in `lead-revenue-automation-system/API_DOCUMENTATION.md` before running the service anywhere public. Do not put backend API keys in the frontend or commit secrets.

## Repository layout

```text
.
├── LeadFlow/                           # Next.js landing page and chat UI
└── lead-revenue-automation-system/     # Spring Boot API and backend docs
```

## Run locally

You need Node.js and npm for the landing page, and Java 21 for the backend. The backend repository includes the Maven wrapper. AI replies also require a supported provider API key.

### 1. Start the backend

In PowerShell:

```powershell
cd lead-revenue-automation-system
$env:SPRING_PROFILES_ACTIVE = "dev"
$env:GEMINI_API_KEY = "your-gemini-api-key"
./mvnw.cmd spring-boot:run
```

The development profile uses an in-memory H2 database, so data is reset when the application restarts. The default Gemini model candidates are configured in `src/main/resources/application.properties`. Alternatively, configure OpenAI with `OPENAI_API_KEY` and an eligible model list in `OPENAI_MODELS`.

### 2. Configure and start the landing page

In a second PowerShell terminal:

```powershell
cd LeadFlow
Copy-Item .env.example .env.local
npm install
npm run dev
```

Set `CHAT_API_BASE_URL` to the backend origin (normally `http://localhost:8080`) and `CHAT_ORGANIZATION_ID` to an organization UUID that has been provisioned in the backend. Set `NEXT_PUBLIC_WEB3FORMS_ACCESS_KEY` if you want the landing-page contact form to deliver submissions through Web3Forms. Other public landing-page values are listed in `.env.example`.

The landing page is served at `http://localhost:3000`. The chat forwarding route is `/api/chat`; it sends requests to the backend's `/api/v1/message/web-chat/send` endpoint.

### 3. Provision business information

The chat requires an existing organization and organization settings in the backend database, including business description/instructions. Provision those records through a trusted local or administrative process, then use that organization's UUID as `CHAT_ORGANIZATION_ID`. Backend setup routes are not enabled for unauthenticated access by default. See the endpoint guide in `lead-revenue-automation-system/API_DOCUMENTATION.md` for request shapes and the current security behavior.

## Configuration at a glance

| Variable | Used by | Purpose |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | Backend | Selects `dev` (H2) or `prod` (PostgreSQL); the default is `prod`. |
| `GEMINI_API_KEY` | Backend | Google Gemini API key. |
| `GEMINI_MODELS` | Backend | Optional comma-separated Gemini model candidates. |
| `OPENAI_API_KEY` | Backend | OpenAI API key, if using OpenAI. |
| `OPENAI_MODELS` | Backend | Comma-separated eligible OpenAI model candidates. |
| `LF_DB_URL`, `LF_DB_USER`, `LF_DB_PASSWORD` | Backend (`prod`) | PostgreSQL connection settings. |
| `APP_CORS_ALLOWED_ORIGINS` | Backend | Comma-separated allowed frontend origins. |
| `CHAT_API_BASE_URL` | Landing page server | Backend origin used by the Next.js chat proxy. |
| `CHAT_ORGANIZATION_ID` | Landing page server | Organization whose saved business profile powers chat replies. |
| `NEXT_PUBLIC_WEB3FORMS_ACCESS_KEY` | Landing page | Optional Web3Forms access key for the contact form. |

Keep provider keys and database credentials in local environment configuration or a deployment secret manager. Variables prefixed with `NEXT_PUBLIC_` are exposed to the browser and must not contain secrets.

For production deployment, set `SPRING_PROFILES_ACTIVE=prod`, configure PostgreSQL and provider credentials, and set CORS to the exact frontend origin. The backend Dockerfile builds and runs the Spring Boot service. A production deployment still needs a deliberate security, abuse-prevention, privacy, and operational review.

## Continuing the project

Someone taking this further can treat the current chat path as a reference implementation, then decide what product is worth building. Likely areas to investigate include secure tenant onboarding and access control, a business settings interface, lead management, human handoff, integration lifecycle, privacy and retention, observability, and deployment hardening. These are possible next steps, not promises about the original scope.

Start by reading the API and architecture notes, tracing the landing-page proxy through the message controller and service, and checking the security configuration. Confirm each feature in the code before relying on it; this prototype may contain incomplete or evolving pieces.
