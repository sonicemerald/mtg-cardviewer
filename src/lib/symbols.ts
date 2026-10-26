// Turns Scryfall mana text like "{2}{W/U}{T}" into tokens we can render as symbol images.

export type Token = { kind: 'text'; value: string } | { kind: 'symbol'; code: string; label: string }

const SYMBOL_RE = /\{([^}]+)\}/g

/** "W/U" -> "WU", "T" -> "T" (matches the file names in Scryfall's symbol SVG set). */
export function symbolCode(raw: string): string {
  return raw.replace(/\//g, '')
}

export function symbolUrl(code: string): string {
  return `https://svgs.scryfall.io/card-symbols/${code}.svg`
}

export function tokenize(text: string): Token[] {
  const tokens: Token[] = []
  let last = 0
  for (const match of text.matchAll(SYMBOL_RE)) {
    const index = match.index ?? 0
    if (index > last) tokens.push({ kind: 'text', value: text.slice(last, index) })
    tokens.push({ kind: 'symbol', code: symbolCode(match[1]), label: match[1] })
    last = index + match[0].length
  }
  if (last < text.length) tokens.push({ kind: 'text', value: text.slice(last) })
  return tokens
}
