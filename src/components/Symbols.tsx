import { symbolUrl, tokenize } from '../lib/symbols'

/** Renders text containing {W}-style mana symbols, swapping each symbol for its icon. */
export function Symbols({ text }: { text?: string }) {
  if (!text) return null
  return (
    <>
      {tokenize(text).map((t, i) =>
        t.kind === 'text' ? (
          <span key={i}>{t.value}</span>
        ) : (
          <img
            key={i}
            className="symbol"
            src={symbolUrl(t.code)}
            alt={`{${t.label}}`}
            width={18}
            height={18}
            loading="lazy"
          />
        ),
      )}
    </>
  )
}
