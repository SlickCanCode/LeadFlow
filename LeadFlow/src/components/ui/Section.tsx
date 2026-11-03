const tones = { paper: "", mist: "bg-mist", navy: "bg-navy text-white" };
// `navy` is the one dark band on the page: it gives rhythm and makes "How it works" feel weighty.
export function Section({ id, title, intro, tone = "paper", children }:
  { id: string; title: string; intro?: string; tone?: keyof typeof tones; children: React.ReactNode }) {
  return (
    <section id={id} aria-labelledby={`${id}-h`} className={`${tones[tone]} border-t border-line py-20 sm:py-28`}>
      <div className="mx-auto max-w-6xl px-5 sm:px-8">
        <h2 id={`${id}-h`} className="max-w-2xl text-3xl font-semibold tracking-tight sm:text-4xl">{title}</h2>
        {intro && <p className={`mt-4 max-w-2xl text-lg ${tone === "navy" ? "text-white/75" : "text-navy-soft"}`}>{intro}</p>}
        <div className="mt-12">{children}</div>
      </div>
    </section>
  );
}
