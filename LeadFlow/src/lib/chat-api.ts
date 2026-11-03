/**
 * Chat API adapter — keeps the UI independent from the chat transport.
 */

export type ChatRole = "user" | "assistant";
export interface ChatMessage { id: string; role: ChatRole; text: string }

export class ChatRequestError extends Error {}

const VISITOR_ID_STORAGE_KEY = "leadflow-chat-visitor-id";

function getVisitorId(): string {
  try {
    const storedId = window.localStorage.getItem(VISITOR_ID_STORAGE_KEY);
    if (storedId) return storedId;

    const visitorId = `leadflow-${crypto.randomUUID()}`;
    window.localStorage.setItem(VISITOR_ID_STORAGE_KEY, visitorId);
    return visitorId;
  } catch {
    throw new ChatRequestError("Chat requires browser storage to remember your conversation. Enable local storage and try again.");
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null;
}

function parseReply(payload: unknown): string {
  if (isRecord(payload) && typeof payload.response === "string" && payload.response.trim()) {
    return payload.response;
  }
  throw new ChatRequestError("The chat service returned an invalid response.");
}

function getApiError(payload: unknown): string | undefined {
  if (!isRecord(payload) || !Array.isArray(payload.errors)) return undefined;
  const messages = payload.errors.filter((error): error is string => typeof error === "string" && Boolean(error.trim()));
  return messages.length ? messages.join(" ") : undefined;
}

async function readJson(response: Response): Promise<unknown> {
  try {
    return await response.json();
  } catch {
    throw new ChatRequestError("The chat service returned an invalid response.");
  }
}

export async function sendChatMessage(message: string): Promise<string> {
  let response: Response;
  try {
    response = await fetch("/api/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ externalId: getVisitorId(), message }),
    });
  } catch (error) {
    if (error instanceof Error && error.name === "AbortError") throw error;
    throw new ChatRequestError("Could not reach the chat service. Check your connection and try again.");
  }

  const payload = await readJson(response);

  if (!response.ok) {
    throw new ChatRequestError(getApiError(payload) ?? `The chat service returned an error (${response.status}).`);
  }

  return parseReply(payload);
}
