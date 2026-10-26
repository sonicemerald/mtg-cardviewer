import { useCallback, useEffect, useState } from 'react'
import { getImage, type Card } from './scryfall'

// A trimmed-down copy of a card, small enough to keep in localStorage.
export interface DeckEntry {
  id: string
  name: string
  set: string
  collector_number: string
  mana_cost?: string
  type_line: string
  cmc: number
  usd?: string | null
  image?: string
  qty: number
}

const STORAGE_KEY = 'mtg-cardviewer:deck:v1'

export function toEntry(card: Card): Omit<DeckEntry, 'qty'> {
  return {
    id: card.id,
    name: card.name,
    set: card.set,
    collector_number: card.collector_number,
    mana_cost: card.mana_cost ?? card.card_faces?.[0]?.mana_cost,
    type_line: card.type_line,
    cmc: card.cmc,
    usd: card.prices.usd,
    image: getImage(card, 0, 'small'),
  }
}

export function addEntry(deck: DeckEntry[], card: Omit<DeckEntry, 'qty'>): DeckEntry[] {
  const existing = deck.find((e) => e.id === card.id)
  if (existing) return deck.map((e) => (e.id === card.id ? { ...e, qty: e.qty + 1 } : e))
  return [...deck, { ...card, qty: 1 }]
}

export function changeQty(deck: DeckEntry[], id: string, delta: number): DeckEntry[] {
  return deck
    .map((e) => (e.id === id ? { ...e, qty: e.qty + delta } : e))
    .filter((e) => e.qty > 0)
}

export function deckCount(deck: DeckEntry[]): number {
  return deck.reduce((sum, e) => sum + e.qty, 0)
}

export function deckValue(deck: DeckEntry[]): number {
  return deck.reduce((sum, e) => sum + (Number(e.usd) || 0) * e.qty, 0)
}

/** Plain-text export in the "1 Card Name (SET) 123" format most deck tools import. */
export function exportDeck(deck: DeckEntry[]): string {
  return [...deck]
    .sort((a, b) => a.name.localeCompare(b.name))
    .map((e) => `${e.qty} ${e.name} (${e.set.toUpperCase()}) ${e.collector_number}`)
    .join('\n')
}

function load(): DeckEntry[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    const parsed: unknown = raw ? JSON.parse(raw) : []
    return Array.isArray(parsed) ? (parsed as DeckEntry[]) : []
  } catch {
    return []
  }
}

export function useDeck() {
  const [deck, setDeck] = useState<DeckEntry[]>(load)

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(deck))
    } catch {
      /* storage unavailable (private mode, quota) – keep working in memory */
    }
  }, [deck])

  const add = useCallback((card: Card) => setDeck((d) => addEntry(d, toEntry(card))), [])
  const adjust = useCallback((id: string, delta: number) => setDeck((d) => changeQty(d, id, delta)), [])
  const clear = useCallback(() => setDeck([]), [])

  return { deck, add, adjust, clear }
}
