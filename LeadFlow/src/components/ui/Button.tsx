import Link from "next/link";
import type { ComponentProps } from "react";

// Motion: colour/transform eased with our strong ease-out; :active gives a tactile 0.97 press.
// Hover styles are wrapped in [@media(hover:hover)] so touch screens don't get "stuck" hover states.
const base = "inline-flex min-h-[44px] items-center justify-center rounded-lg px-5 py-3 text-sm font-semibold transition-[background-color,transform,box-shadow] duration-200 ease-[cubic-bezier(0.23,1,0.32,1)] active:scale-[0.97]";
const variants = {
  primary: "bg-accent text-white shadow-[0_1px_0_rgba(255,255,255,.2)_inset,0_6px_16px_-6px_rgba(31,79,216,.55)] [@media(hover:hover)]:hover:bg-accent-dark",
  secondary: "border border-navy/25 bg-white text-navy [@media(hover:hover)]:hover:border-navy/60",
  whatsapp: "bg-[#0B6B3A] text-white [@media(hover:hover)]:hover:bg-[#095630]",
};
type Variant = keyof typeof variants;

export function ButtonLink({ variant = "primary", className = "", ...p }: ComponentProps<typeof Link> & { variant?: Variant }) {
  return <Link className={`${base} ${variants[variant]} ${className}`} {...p} />;
}
export function Button({ variant = "primary", className = "", ...p }: ComponentProps<"button"> & { variant?: Variant }) {
  return <button className={`${base} ${variants[variant]} disabled:opacity-60 disabled:active:scale-100 ${className}`} {...p} />;
}
