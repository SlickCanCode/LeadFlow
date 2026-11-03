import { site } from "./config";

export interface ContactPayload { name: string; business: string; contact: string; problem: string }
export type FieldErrors = Partial<Record<keyof ContactPayload, string>>;

export function validate(p: ContactPayload): FieldErrors {
  const e: FieldErrors = {};
  if (p.name.trim().length < 2) e.name = "Enter your name.";
  if (p.business.trim().length < 2) e.business = "Enter your business name.";
  const c = p.contact.trim();
  const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(c);
  const isPhone = /^\+?[\d\s()-]{8,}$/.test(c);
  if (!isEmail && !isPhone) e.contact = "Enter a valid email address or WhatsApp number.";
  if (p.problem.trim().length < 10) e.problem = "Describe the problem in at least a sentence.";
  return e;
}

/** Posts JSON to the configurable endpoint. Swap the body shape here when the real endpoint is known. */
export async function submitContact(p: ContactPayload): Promise<void> {
  if (!site.contactEndpoint) throw new Error("NOT_CONFIGURED");
  const res = await fetch(site.contactEndpoint, {
    method: "POST", headers: { "Content-Type": "application/json", Accept: "application/json" }, body: JSON.stringify(p),
  });
  if (!res.ok) throw new Error(`HTTP_${res.status}`);
}
