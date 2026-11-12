"use client";
import { useEffect, useRef, useState } from "react";

// ILLUSTRATION ONLY: scripted on purpose and labelled as such. The real, live chat is the section below.
const SCRIPT = [
  { me: false, text: "Hi, do you fix leaking pipes?", time: "9:41 PM" },
  { me: true, text: "Yes, we do. Which area are you in, and is it urgent?", time: "9:41 PM" },
  { me: false, text: "Springfield. Water is dripping under the kitchen sink.", time: "9:42 PM" },
  { me: true, text: "Thanks. Please send a photo if you can, and tell us when you are free.", time: "9:42 PM" },
];
const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms));

export function WhatsAppDemo() {
  const [shown, setShown] = useState(0);
  const [typing, setTyping] = useState(false);
  const [visible, setVisible] = useState(false);
  const [reduced, setReduced] = useState(false);
  const root = useRef<HTMLElement>(null);

  useEffect(() => {
    const mq = window.matchMedia("(prefers-reduced-motion: reduce)");
    setReduced(mq.matches);
    // Only run while on screen: no wasted work, and visitors see the start of the story.
    const io = new IntersectionObserver(([e]) => setVisible(e.isIntersecting), { threshold: 0.4 });
    if (root.current) io.observe(root.current);
    return () => io.disconnect();
  }, []);

  useEffect(() => {
    if (reduced) { setShown(SCRIPT.length); return; } // static full conversation, no motion
    if (!visible) return;
    let cancelled = false;
    (async () => {
      while (!cancelled) {
        setShown(0); await sleep(700);
        for (let i = 0; i < SCRIPT.length && !cancelled; i++) {
          if (SCRIPT[i].me) { setTyping(true); await sleep(1100); setTyping(false); } // assistant "types" first
          else await sleep(600);
          if (cancelled) return;
          setShown(i + 1); await sleep(900);
        }
        await sleep(3500); // let the finished conversation be read before looping
      }
    })();
    return () => { cancelled = true; setTyping(false); };
  }, [visible, reduced]);

  return (
    <figure ref={root} className="mx-auto w-full max-w-[340px]">
      <div className="rounded-[2.4rem] border border-navy/15 bg-navy p-2.5 shadow-[0_30px_60px_-30px_rgba(15,30,61,.5)]">
        <div className="overflow-hidden rounded-[1.9rem] bg-[#EFEAE2]">
          <div className="flex items-center gap-3 bg-[#075E54] px-4 py-3 text-white">
            <span aria-hidden className="grid h-9 w-9 place-items-center rounded-full bg-white/20 text-sm font-semibold">A</span>
            <div><p className="text-sm font-semibold leading-tight">ClearFlow Plumbing</p><p className="text-xs text-white/80">Demo business</p></div>
          </div>
          {/* Fixed height so the phone never changes size as messages arrive (no layout shift). */}
          <ol className="flex h-[360px] flex-col justify-end gap-2 p-3" aria-label="Example WhatsApp conversation">
            {SCRIPT.slice(0, shown).map((m, i) => (
              <li key={i} className={`msg-in max-w-[82%] rounded-xl px-3 py-2 text-[13px] leading-snug text-[#111B21] shadow-sm ${m.me ? "self-end rounded-tr-sm bg-[#D9FDD3]" : "self-start rounded-tl-sm bg-white"}`}>
                {m.text}<span className="mt-1 block text-right text-[10px] text-[#667781]">{m.time}</span>
              </li>
            ))}
            {typing && (
              <li aria-hidden className="msg-in flex w-fit gap-1 self-end rounded-xl rounded-tr-sm bg-[#D9FDD3] px-3 py-3">
                <span className="dot h-1.5 w-1.5 rounded-full bg-[#667781]" /><span className="dot h-1.5 w-1.5 rounded-full bg-[#667781]" /><span className="dot h-1.5 w-1.5 rounded-full bg-[#667781]" />
              </li>
            )}
          </ol>
        </div>
      </div>
      <figcaption className="mt-4 text-center text-xs text-navy-soft">Illustration of a customer conversation. Scripted, not a live chat. Try the real assistant below.</figcaption>
    </figure>
  );
}
