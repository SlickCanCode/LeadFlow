import { Section } from "../ui/Section";

const steps = [
  ["Consultation", "We learn how your business handles enquiries and what customers usually ask."],
  ["Business configuration", "We enter your services, hours and policies so answers match your business."],
  ["Channel integration", "We connect the assistant to the channels your customers already use."],
  ["Testing", "We test it with you, fix gaps, then take it live."],
];
export function HowItWorks() {
  return (
    <Section id="how-it-works" tone="navy" title="We do the setup. You approve the result." intro="Four steps, handled by us. Your part is telling us about your business and checking the test.">
      <ol className="grid gap-px overflow-hidden rounded-xl border border-white/15 bg-white/15 md:grid-cols-4">
        {steps.map(([t, d], i) => (
          <li key={t} className="bg-navy p-6">
            <p className="text-sm font-semibold text-[#9DB8FF]">Step {i + 1}</p>
            <h3 className="mt-2 text-lg font-semibold">{t}</h3>
            <p className="mt-2 text-sm leading-relaxed text-white/75">{d}</p>
          </li>
        ))}
      </ol>
    </Section>
  );
}
