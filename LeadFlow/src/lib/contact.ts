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

const WEB3FORMS_ENDPOINT = "https://api.web3forms.com/submit";

export async function submitContact(p: ContactPayload): Promise<void> {
  if (!site.web3FormsAccessKey) throw new Error("NOT_CONFIGURED");

  const contact = p.contact.trim();
  const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(contact);
  const res = await fetch(WEB3FORMS_ENDPOINT, {
    method: "POST",
    headers: { "Content-Type": "application/json", Accept: "application/json" },
    body: JSON.stringify({
      access_key: site.web3FormsAccessKey,
      name: p.name.trim(),
      ...(isEmail ? { email: contact } : {}),
      business: p.business.trim(),
      "Email or WhatsApp number": contact,
      subject: `New LeadFlow enquiry from ${p.name.trim()}`,
      message: p.problem.trim(),
    }),
  });

  const result: { success?: boolean; message?: string } = await res.json();
  if (!res.ok || result.success !== true) {
    throw new Error(result.message || `Web3Forms request failed (${res.status})`);
  }
}
