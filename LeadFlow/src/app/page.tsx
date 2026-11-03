import { Navbar } from "@/components/sections/Navbar";
import { Hero } from "@/components/sections/Hero";
import { Problems } from "@/components/sections/Problems";
import { ChatDemo } from "@/components/demo/ChatDemo";
import { Section } from "@/components/ui/Section";
import { Capabilities } from "@/components/sections/Capabilities";
import { HowItWorks } from "@/components/sections/HowItWorks";
import { Contact } from "@/components/sections/Contact";
import { Footer } from "@/components/sections/Footer";

export default function Home() {
  return (
    <>
      <Navbar />
      <main id="main">
        <Hero />
        <Problems />
        <Section id="demo" tone="mist" title="Talk to a real assistant." intro="This is a live demo for ClearFlow Plumbing. Ask it anything a customer might ask about service, scheduling, or urgent plumbing issues.">
          <ChatDemo />
        </Section>
        <Capabilities />
        <HowItWorks />
        <Contact />
      </main>
      <Footer />
    </>
  );
}
