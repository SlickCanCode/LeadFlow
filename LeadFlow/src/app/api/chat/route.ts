const CHAT_ENDPOINT = "/api/v1/message/web-chat/send";

function errorResponse(message: string, status: number): Response {
  return Response.json({ errors: [message] }, { status });
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

export async function POST(request: Request): Promise<Response> {
  let payload: unknown;
  try {
    payload = await request.json();
  } catch {
    return errorResponse("Request body must be valid JSON.", 400);
  }

  if (!isRecord(payload)
    || typeof payload.message !== "string"
    || !payload.message.trim()
    || typeof payload.externalId !== "string"
    || !payload.externalId.trim()
    || payload.externalId.length > 255) {
    return errorResponse("A non-blank message and externalId of at most 255 characters are required.", 400);
  }

  const backendUrl = process.env.CHAT_API_BASE_URL;
  const organizationId = process.env.CHAT_ORGANIZATION_ID;
  if (!backendUrl || !organizationId) {
    return errorResponse("The live demo isn't connected yet. Please contact us directly.", 503);
  }

  let url: URL;
  try {
    url = new URL(CHAT_ENDPOINT, backendUrl);
  } catch {
    return errorResponse("Chat service configuration is invalid.", 500);
  }

  let backendResponse: Response;
  try {
    backendResponse = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json", Accept: "application/json" },
      body: JSON.stringify({
        organizationId,
        externalId: payload.externalId,
        message: payload.message,
      }),
    });
  } catch {
    return errorResponse("Could not reach the chat service. Please try again later.", 502);
  }

  let backendPayload: unknown;
  try {
    backendPayload = await backendResponse.json();
  } catch {
    return errorResponse("The chat service returned an invalid response.", 502);
  }

  return Response.json(backendPayload, { status: backendResponse.status });
}
