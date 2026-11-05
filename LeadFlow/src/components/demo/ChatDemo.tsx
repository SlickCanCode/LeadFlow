"use client";
import { useEffect, useRef, useState } from "react";
import { ChatMessage, sendChatMessage } from "@/lib/chat-api";
import { Button } from "../ui/Button";
import { BusinessPanel } from "./BusinessPanel";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";


const SUGGESTIONS = [
  "What services do you offer?",
  "What are your opening hours?",
  "How do I request a visit?",
  "Do you handle emergencies?",
];
const uid = () => crypto.randomUUID();

function TypingBubble() {
  return (
    <div className="msg-in flex w-fit max-w-[85%] items-center gap-3 rounded-2xl bg-mist px-4 py-3 text-navy-soft shadow-sm">
      <div className="flex items-center gap-1.5" aria-label="Assistant is typing">
        {[0, 1, 2].map((dot) => (
          <span key={dot} className="dot h-2.5 w-2.5 rounded-full bg-navy-soft/70" />
        ))}
      </div>

    </div>
  );
}

export function ChatDemo() {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [usedSuggestions, setUsedSuggestions] = useState<string[]>([]);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const endRef = useRef<HTMLDivElement>(null);

  useEffect(() => { endRef.current?.scrollIntoView({ block: "nearest" }); }, [messages, loading]);

  const availableSuggestions = SUGGESTIONS.filter((suggestion) => !usedSuggestions.includes(suggestion));

  async function send(text: string) {
    const message = text.trim();
    if (!message || loading) return;
    if (SUGGESTIONS.includes(message)) {
      setUsedSuggestions((current) => [...current, message]);
    }
    setMessages((current) => [...current, { id: uid(), role: "user", text: message }]);
    setInput(""); setError(null); setLoading(true);
    try {
      const reply = await sendChatMessage(message);
      setMessages((m) => [...m, { id: uid(), role: "assistant", text: reply }]);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Something went wrong reaching the assistant. Check your connection and try again.");
    } finally { setLoading(false); }
  }

  const panelClassName = isFullScreen
    ? "fixed inset-3 z-50 flex h-[calc(100vh-1.5rem)] flex-col overflow-hidden rounded-2xl border border-line bg-white shadow-[0_24px_60px_-20px_rgba(10,28,42,0.36)] md:static md:inset-auto md:h-[520px] md:rounded-xl"
    : "flex h-[520px] flex-col overflow-hidden rounded-xl border border-line bg-white shadow-card";

  return (
    <div>
      <div className="grid gap-4 lg:grid-cols-[1fr_220px]">
        <div className={panelClassName}>
          <div className="flex items-center justify-between border-b border-line px-4 py-3">
            <div>
              <p className="text-sm font-semibold">ClearFlow Plumbing</p>
            </div>
            <div className="flex items-center gap-2">
              <button
                type="button"
                aria-label={isFullScreen ? "Exit full screen" : "Expand chat to full screen"}
                onClick={() => setIsFullScreen((value) => !value)}
                className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-line bg-mist text-navy-soft transition-colors [@media(hover:hover)]:hover:text-accent md:hidden"
              >
                {isFullScreen ? (
                  <svg viewBox="0 0 24 24" aria-hidden="true" className="h-4 w-4 fill-none stroke-current stroke-[2]">
                    <path d="M8 8h8v8" />
                    <path d="M8 16 4 20M16 8l4-4" />
                  </svg>
                ) : (
                  <svg viewBox="0 0 24 24" aria-hidden="true" className="h-4 w-4 fill-none stroke-current stroke-[2]">
                    <path d="M8 3H3v5M16 21h5v-5M3 8V3h5M21 16v5h-5" />
                  </svg>
                )}
              </button>
              <span className="rounded-full bg-mist px-2.5 py-1 text-[10px] font-medium text-navy-soft">Live Demo</span>
            </div>
          </div>

          <div role="log" aria-live="polite" aria-label="Conversation" className="flex-1 space-y-3 overflow-y-auto p-4">
            {messages.length === 0 && !loading && (
              <div className="py-8 text-center">
                <p className="font-medium">Ask ClearFlow Plumbing a question</p>
                <p className="mt-2 text-sm text-navy-soft">Replies come from the live assistant. Pick a prompt or type your own.</p>
              </div>
            )}
            {messages.map((m) => (
              <div key={m.id} className={`msg-in flex ${m.role === "user" ? "justify-end" : ""}`}>
                <div
  className={`max-w-[85%] rounded-2xl px-4 py-2.5 text-sm ${
    m.role === "user"
      ? "bg-accent text-white"
      : "bg-mist text-navy"
  }`}
>
  <ReactMarkdown
    remarkPlugins={[remarkGfm]}
    components={{
      p: ({ children }) => <p className="mb-2 last:mb-0">{children}</p>,
      ul: ({ children }) => (
        <ul className="my-2 list-disc space-y-1 pl-5">{children}</ul>
      ),
      ol: ({ children }) => (
        <ol className="my-2 list-decimal space-y-1 pl-5">{children}</ol>
      ),
      li: ({ children }) => <li>{children}</li>,
      strong: ({ children }) => <strong className="font-bold">{children}</strong>,
    }}
  >
    {m.text}
  </ReactMarkdown>
</div>
              </div>
            ))}
            {loading && <TypingBubble />}
            {error && <p role="alert" className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-900">{error}</p>}
            <div ref={endRef} />
          </div>

          <div className="border-t border-line p-3">
            {availableSuggestions.length > 0 && (
              <div className="mb-2 flex flex-wrap gap-2">
                {availableSuggestions.map((suggestion) => (
                  <button
                    key={suggestion}
                    type="button"
                    onClick={() => send(suggestion)}
                    disabled={loading}
                    className="min-h-[36px] rounded-full border border-line bg-white px-3 text-xs text-navy transition-[transform,border-color] duration-150 ease-[cubic-bezier(0.23,1,0.32,1)] active:scale-[0.97] [@media(hover:hover)]:hover:border-accent [@media(hover:hover)]:hover:text-accent disabled:opacity-50"
                  >
                    {suggestion}
                  </button>
                ))}
              </div>
            )}
            <form onSubmit={(e) => { e.preventDefault(); send(input); }} className="flex gap-2">
              <label htmlFor="chat-input" className="sr-only">Your message</label>
              <input
                id="chat-input"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                maxLength={500}
                placeholder="Type a message"
                className="min-h-[44px] flex-1 rounded-lg border border-line bg-white px-3 text-sm text-navy placeholder:text-navy-soft"
              />
              <Button type="submit" disabled={loading || !input.trim()}>Send</Button>
            </form>
          </div>
        </div>
        <BusinessPanel />
      </div>
      <p className="mt-3 text-xs text-navy-soft">ClearFlow Plumbing is the demo business used in this live assistant. Conversations are not shared with other visitors.</p>
    </div>
  );
}
