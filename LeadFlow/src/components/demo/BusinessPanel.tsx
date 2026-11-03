const rows = [
  ["Business", "ClearFlow Plumbing"],
  ["Services", "Leak detection, drain clearing, pipe repair, fixture installs, water heater service"],
  ["Hours", "Mon–Fri 8:00–18:00 · Sat 9:00–15:00 · Sun closed"],
  ["Area", "Springfield, IL and nearby communities within a 25-mile radius"],
  ["Emergency", "24/7 call handling for burst pipes, flooding, sewage backups, and major leaks"],
];

export function BusinessPanel() {
  return (
    <aside aria-label="Demo business information" className="rounded-xl border border-line bg-white p-5 shadow-card">
      <div className="mb-4">
        <p className="text-xs font-semibold uppercase tracking-[0.16em] text-[#4D6BFF]">Business snapshot</p>
        <h3 className="mt-2 text-lg font-semibold text-navy">What the assistant knows</h3>
      </div>
      <dl className="grid gap-3 text-sm">
        {rows.map(([k, v]) => (
          <div key={k} className="rounded-lg border border-line bg-mist/50 p-3">
            <dt className="text-[11px] font-semibold uppercase tracking-[0.12em] text-navy-soft">{k}</dt>
            <dd className="mt-1 font-medium leading-relaxed text-navy">{v}</dd>
          </div>
        ))}
      </dl>
    </aside>
  );
}
