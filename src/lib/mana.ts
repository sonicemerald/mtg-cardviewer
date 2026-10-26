// Mana source calculator: spreads a deck's land count across colors in proportion
// to how many colored mana symbols each color has.

export type ManaColor = 'W' | 'U' | 'B' | 'R' | 'G'

export const MANA_COLORS: { code: ManaColor; label: string; css: string }[] = [
  { code: 'W', label: 'White', css: '#fff59d' },
  { code: 'U', label: 'Blue', css: '#2196f3' },
  { code: 'B', label: 'Black', css: '#9e9e9e' },
  { code: 'R', label: 'Red', css: '#f44336' },
  { code: 'G', label: 'Green', css: '#4caf50' },
]

export const DECK_PRESETS = [
  { id: 'limited', label: 'Limited – 40 cards', lands: 17 },
  { id: 'constructed', label: 'Constructed – 60 cards', lands: 24 },
  { id: 'commander', label: 'Commander – 100 cards', lands: 40 },
] as const

export type Symbols = Record<ManaColor, number>

export interface ManaResult {
  totalSymbols: number
  lands: Record<ManaColor, number>
}

function clean(n: number): number {
  return Number.isFinite(n) && n > 0 ? Math.floor(n) : 0
}

/**
 * Splits `lands` between colors using the largest-remainder method so the
 * per-color land counts always add up to exactly `lands` (when any symbols exist).
 */
export function calculateMana(symbols: Symbols, lands: number): ManaResult {
  const totalLands = clean(lands)
  const counts = MANA_COLORS.map((c) => clean(symbols[c.code]))
  const total = counts.reduce((a, b) => a + b, 0)
  const result = {} as Record<ManaColor, number>

  if (total === 0 || totalLands === 0) {
    MANA_COLORS.forEach((c) => (result[c.code] = 0))
    return { totalSymbols: total, lands: result }
  }

  const exact = counts.map((n) => (n * totalLands) / total)
  const floors = exact.map(Math.floor)
  let remaining = totalLands - floors.reduce((a, b) => a + b, 0)
  const order = exact
    .map((v, i) => ({ i, frac: v - Math.floor(v) }))
    .sort((a, b) => b.frac - a.frac || counts[b.i] - counts[a.i])
  for (const { i } of order) {
    if (remaining <= 0) break
    floors[i] += 1
    remaining -= 1
  }
  MANA_COLORS.forEach((c, i) => (result[c.code] = floors[i]))
  return { totalSymbols: total, lands: result }
}
