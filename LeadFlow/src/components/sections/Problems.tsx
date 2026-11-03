import { Section } from "../ui/Section";

// Problem framing only: no statistics or claims about results.
const items = [
  ["Messages arrive when you can't reply", "You're on a job, driving or closed for the day, and the enquiry sits unanswered."],
  ["The same questions, every day", "Services, hours, location. You answer them again and again by hand."],
  ["Enquiries get buried", "Calls, WhatsApp and DMs pile up, and the customer moves on to whoever replied first."],
];
export function Problems() {
  return (
    <Section id="problems" title="Busy days shouldn't mean unanswered customers.">
      <ul className="grid gap-10 md:grid-cols-3">
        {items.map(([t, d]) => (
          <li key={t}><h3 className="text-lg font-semibold">{t}</h3><p className="mt-2 leading-relaxed text-navy-soft">{d}</p></li>
        ))}
      </ul>
    </Section>
  );
}
