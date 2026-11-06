# Lead-response landing page
1. Copy `.env.example` to `.env.local` and fill in the settings. Create a Web3Forms access key at [web3forms.com](https://web3forms.com/) and set `NEXT_PUBLIC_WEB3FORMS_ACCESS_KEY` to receive contact form submissions.  2. `npm install && npm run dev`.
3. Configure server-side `CHAT_API_BASE_URL` and `CHAT_ORGANIZATION_ID` with the backend origin and ClearFlow Plumbing organization's UUID. The same-origin Next.js handler forwards the live chat to `/api/v1/message/web-chat/send`, avoiding a browser CORS dependency; visitor IDs persist in local storage so the backend can continue each conversation.
4. Fill `BusinessPanel.tsx` from the ClearFlow Plumbing data; verify `Capabilities.tsx`; add `/privacy`.
