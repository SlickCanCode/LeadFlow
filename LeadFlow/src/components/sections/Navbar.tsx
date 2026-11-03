import Link from "next/link";
import { site } from "@/lib/config";
import { ButtonLink } from "../ui/Button";

const links = [["Demo", "#demo"], ["Capabilities", "#capabilities"], ["How it works", "#how-it-works"]];

export function Navbar() {
  return (
    <header className="sticky top-0 z-30 border-b border-line bg-paper/95 backdrop-blur-sm">
      <nav aria-label="Main" className="mx-auto flex h-16 max-w-6xl items-center justify-between px-5 sm:px-8">
        <Link href="/" className="font-semibold tracking-tight">{site.name}</Link>
        <ul className="hidden items-center gap-8 text-sm md:flex">
          {links.map(([l, h]) => <li key={h}><a href={h} className="text-navy-soft hover:text-navy">{l}</a></li>)}
        </ul>
        {/* On mobile the section links are dropped to keep the bar uncluttered; the page is one short scroll. */}
        <ButtonLink href="#contact" className="!min-h-[40px] !px-4">Contact us</ButtonLink>
      </nav>
    </header>
  );
}
