import { ButtonLink } from "../ui/Button";
import { WhatsAppDemo } from "../demo/WhatsAppDemo";

const points = ["Set up for you, start to finish", "Built around your business details", "Tested with you before it goes live"];
const Check = () => <svg aria-hidden viewBox="0 0 20 20" className="h-5 w-5 flex-none text-accent"><path d="M4 10.5l4 4 8-9" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" /></svg>;

export function Hero() {
  return (
    <div className="relative border-b border-line bg-[radial-gradient(60rem_30rem_at_80%_-10%,rgba(31,79,216,.07),transparent)]">
      <div className="mx-auto grid max-w-6xl items-center gap-14 px-5 py-16 sm:px-8 lg:grid-cols-[1.1fr_.9fr] lg:py-28">
        <div>
          <h1 className="text-[2.5rem] font-semibold leading-[1.05] tracking-[-0.03em] sm:text-6xl">
            Never leave a customer message waiting again.
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-relaxed text-navy-soft">
            When you&apos;re on a job, closed for the day or just busy, your assistant answers customer questions using your business details. We build, connect and test it for you.
          </p>
          <div className="mt-9 flex flex-col gap-3 sm:flex-row">
            <ButtonLink href="#demo" className="!px-7 !py-3.5">Try the Live Demo</ButtonLink>
            <ButtonLink href="#contact" variant="secondary" className="!px-7 !py-3.5">Get It Set Up for My Business</ButtonLink>
          </div>
          <ul className="mt-10 space-y-3 text-[15px]">
            {points.map((p) => <li key={p} className="flex items-center gap-3"><Check />{p}</li>)}
          </ul>
        </div>
        <WhatsAppDemo />
      </div>
    </div>
  );
}
