import type { Config } from "tailwindcss";
// Design tokens live here so colours are changed in ONE place.
const config: Config = {
  content: ["./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        paper: "#FAF9F6",   // warm white page background
        mist: "#F1F0EC",    // warm light grey for alternating sections
        line: "#E3E1DA",    // subtle borders
        navy: { DEFAULT: "#0F1E3D", soft: "#4A5670" }, // text + muted text (soft passes AA on paper)
        accent: { DEFAULT: "#1F4FD8", dark: "#173CA8" }, // blue accent
      },
      fontFamily: { sans: ["var(--font-sans, system-ui)", "system-ui", "sans-serif"] },
      boxShadow: { card: "0 1px 2px rgba(15,30,61,.05), 0 4px 16px rgba(15,30,61,.04)" },
    },
  },
};
export default config;
