import { site, whatsappLink } from "@/lib/config";

export function Footer() {
  return (
    <footer className="border-t border-line py-10 text-sm text-navy-soft">
      <div className="mx-auto flex max-w-6xl flex-col gap-4 px-5 sm:flex-row sm:justify-between sm:px-8">
        <p>© {new Date().getFullYear()} {site.name}</p>
        <ul className="flex flex-wrap gap-6">
          <li><a href={whatsappLink()} className="hover:text-navy">WhatsApp</a></li>
          {site.email && <li><a href={`mailto:${site.email}`} className="hover:text-navy">{site.email}</a></li>}
          {/* TODO: create /privacy with your real policy before launch */}
          <li><a href="/privacy" className="hover:text-navy">Privacy</a></li>
        </ul>
      </div>
    </footer>
  );
}
