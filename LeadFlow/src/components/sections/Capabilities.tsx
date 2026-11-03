import { Section } from "../ui/Section";

// ⚠ VERIFY every item against the shipped product before launch. Only claims derived from your brief are listed.
const items = [
  ["Business-aware answers", "The assistant replies using your services, hours, area coverage, and safety guidance so every answer feels accurate."],
  ["Lead capture and triage", "It qualifies new enquiries, asks the right follow-up questions, and spots urgent issues that need a quick human handoff."],
  ["Follow-up conversations", "The assistant keeps the customer moving by collecting missing details, confirming the problem, and encouraging booking or contact."],
  ["Channel-ready setup", "We connect the assistant to the channels you already use, so conversations start naturally wherever customers reach out."],
  ["Human escalation", "For complaints, refunds, disputes, or emergencies, it knows when to route the conversation to a real person."],
  ["Launch-tested quality", "We test the replies against your business details before the assistant goes live so it sounds polished and trustworthy."],
];

export function Capabilities() {
  return (
    <Section id="capabilities" title="What you get" intro="The setup needed to turn customer questions into qualified leads and quicker follow-up.">
      <ul className="grid gap-px overflow-hidden rounded-xl border border-line bg-line sm:grid-cols-2">
        {items.map(([t, d]) => (
          <li key={t} className="bg-white p-6"><h3 className="font-semibold">{t}</h3><p className="mt-2 text-navy-soft">{d}</p></li>
        ))}
      </ul>
    </Section>
  );
}
