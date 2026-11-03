"use client";
import { useState } from "react";
import { ContactPayload, FieldErrors, submitContact, validate } from "@/lib/contact";
import { whatsappLink } from "@/lib/config";
import { Button, ButtonLink } from "../ui/Button";
import { Section } from "../ui/Section";

const empty: ContactPayload = { name: "", business: "", contact: "", problem: "" };
type Status = "idle" | "sending" | "sent" | "failed" | "unconfigured";

export function Contact() {
  const [v, setV] = useState(empty);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [status, setStatus] = useState<Status>("idle");

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const errs = validate(v); setErrors(errs);
    if (Object.keys(errs).length) { (document.querySelector("[aria-invalid=true]") as HTMLElement | null)?.focus(); return; }
    setStatus("sending");
    try { await submitContact(v); setStatus("sent"); setV(empty); }
    catch (err) { setStatus((err as Error).message === "NOT_CONFIGURED" ? "unconfigured" : "failed"); }
  }

  const field = (key: keyof ContactPayload, label: string, props: { type?: string; multiline?: boolean; hint?: string } = {}) => {
    const id = `f-${key}`, err = errors[key];
    const common = { id, name: key, value: v[key], "aria-invalid": !!err, "aria-describedby": err ? `${id}-e` : undefined,
      onChange: (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => setV({ ...v, [key]: e.target.value }),
      className: "mt-1 w-full rounded-lg border border-line bg-white px-3 py-2.5 text-sm aria-[invalid=true]:border-red-600" };
    return (
      <div>
        <label htmlFor={id} className="text-sm font-medium">{label}</label>
        {props.multiline ? <textarea rows={4} {...common} /> : <input type={props.type ?? "text"} {...common} />}
        {err && <p id={`${id}-e`} className="mt-1 text-sm text-red-700">{err}</p>}
      </div>
    );
  };

  return (
    <Section id="contact" tone="mist" title="Get it set up for your business" intro="Message us on WhatsApp for the quickest reply, or send the form and we'll get back to you.">
      <div className="grid gap-10 lg:grid-cols-[1fr_1.2fr]">
        <div>
          <ButtonLink href={whatsappLink()} variant="whatsapp" className="w-full !py-4 text-base sm:w-auto" target="_blank" rel="noopener noreferrer">Chat with us on WhatsApp</ButtonLink>
        </div>
        <form onSubmit={onSubmit} noValidate className="space-y-4 rounded-xl border border-line bg-white p-6 shadow-card">
          {field("name", "Your name")}
          {field("business", "Business name")}
          {field("contact", "Email or WhatsApp number")}
          {field("problem", "What problem do you want solved?", { multiline: true })}
          <Button type="submit" disabled={status === "sending"}>{status === "sending" ? "Sending…" : "Send enquiry"}</Button>
          <div role="status" aria-live="polite" className="text-sm">
            {status === "sent" && <p className="text-green-800">Thanks, we&apos;ve received your enquiry and will reply soon.</p>}
            {status === "failed" && <p className="text-red-700">We couldn&apos;t send that. Try again, or message us on WhatsApp.</p>}
            {status === "unconfigured" && <p className="text-red-700">The form isn&apos;t connected yet. Please use WhatsApp.</p>}
          </div>
        </form>
      </div>
    </Section>
  );
}
