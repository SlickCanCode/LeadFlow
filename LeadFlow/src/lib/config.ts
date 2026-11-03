// Single source of truth for editable site settings (read from env, with safe placeholders).
export const site = {
  name: process.env.NEXT_PUBLIC_PRODUCT_NAME ?? "LeadFlow",
  whatsapp: process.env.NEXT_PUBLIC_WHATSAPP_NUMBER ?? "",
  email: process.env.NEXT_PUBLIC_CONTACT_EMAIL ?? "",
  contactEndpoint: process.env.NEXT_PUBLIC_CONTACT_ENDPOINT ?? "",
};
export const whatsappLink = (text = "Hi, I'd like to get an AI lead-response assistant set up for my business.") =>
  site.whatsapp ? `https://wa.me/${site.whatsapp}?text=${encodeURIComponent(text)}` : "#contact";
