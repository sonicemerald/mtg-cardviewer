// Minimal typed client for the Scryfall API (https://scryfall.com/docs/api).
// Scryfall asks clients to send an Accept header and to stay under ~10 requests/second.

const API = 'https://api.scryfall.com'

export interface ImageUris {
  small: string
  normal: string
  large: string
  png: string
  art_crop: string
  border_crop: string
}

export interface CardFace {
  name: string
  mana_cost?: string
  type_line?: string
  oracle_text?: string
  flavor_text?: string
  power?: string
  toughness?: string
  loyalty?: string
  artist?: string
  image_uris?: ImageUris
}

export interface Prices {
  usd?: string | null
  usd_foil?: string | null
  usd_etched?: string | null
}

export interface Card extends CardFace {
  id: string
  layout: string
  set: string
  set_name: string
  collector_number: string
  rarity: string
  cmc: number
  type_line: string
  colors?: string[]
  card_faces?: CardFace[]
  prices: Prices
  legalities: Record<string, string>
  scryfall_uri: string
}

export interface MtgSet {
  code: string
  name: string
  set_type: string
  released_at?: string
  card_count: number
  icon_svg_uri: string
}

interface List<T> {
  data: T[]
  has_more: boolean
  next_page?: string
  total_cards?: number
}

export interface SearchPage {
  cards: Card[]
  hasMore: boolean
  nextPage?: string
  total: number
}

export class ScryfallError extends Error {
  constructor(message: string, readonly status?: number) {
    super(message)
    this.name = 'ScryfallError'
  }
}

async function getJson<T>(url: string, signal?: AbortSignal): Promise<T> {
  const res = await fetch(url, { headers: { Accept: 'application/json' }, signal })
  if (res.status === 404) throw new ScryfallError('No cards matched that search.', 404)
  if (!res.ok) {
    let detail = ''
    try {
      detail = ((await res.json()) as { details?: string }).details ?? ''
    } catch {
      /* ignore body parse errors */
    }
    throw new ScryfallError(detail || `Scryfall request failed (${res.status}).`, res.status)
  }
  return (await res.json()) as T
}

// Set types worth browsing; skips tokens, memorabilia, minigames and similar.
const BROWSABLE_SET_TYPES = new Set([
  'core',
  'expansion',
  'masters',
  'draft_innovation',
  'commander',
  'masterpiece',
  'eternal',
  'starter',
  'box',
  'arsenal',
  'duel_deck',
  'from_the_vault',
  'spellbook',
  'premium_deck',
  'planechase',
  'archenemy',
  'vanguard',
  'funny',
  'alchemy',
  'remaster',
])

export function filterBrowsableSets(sets: MtgSet[]): MtgSet[] {
  return sets
    .filter((s) => BROWSABLE_SET_TYPES.has(s.set_type) && s.card_count > 0)
    .sort((a, b) => (b.released_at ?? '').localeCompare(a.released_at ?? ''))
}

export async function fetchSets(signal?: AbortSignal): Promise<MtgSet[]> {
  const list = await getJson<List<MtgSet>>(`${API}/sets`, signal)
  return filterBrowsableSets(list.data)
}

export type SortKey = 'name' | 'set' | 'cmc' | 'color' | 'rarity' | 'usd' | 'edhrec'

export const SORT_OPTIONS: { value: SortKey; label: string }[] = [
  { value: 'name', label: 'Name (A–Z)' },
  { value: 'set', label: 'Set number' },
  { value: 'cmc', label: 'Mana value' },
  { value: 'color', label: 'Color' },
  { value: 'rarity', label: 'Rarity' },
  { value: 'usd', label: 'Price (USD)' },
  { value: 'edhrec', label: 'EDHREC popularity' },
]

export const COLORS = [
  { code: 'w', label: 'White' },
  { code: 'u', label: 'Blue' },
  { code: 'b', label: 'Black' },
  { code: 'r', label: 'Red' },
  { code: 'g', label: 'Green' },
  { code: 'c', label: 'Colorless' },
] as const

export const CARD_TYPES = [
  'Creature',
  'Planeswalker',
  'Artifact',
  'Enchantment',
  'Sorcery',
  'Instant',
  'Land',
  'Battle',
] as const

export const RARITIES = ['common', 'uncommon', 'rare', 'mythic'] as const

export interface SearchFilters {
  text: string
  set: string
  colors: string[]
  type: string
  rarity: string
}

export const EMPTY_FILTERS: SearchFilters = { text: '', set: '', colors: [], type: '', rarity: '' }

/** Builds a Scryfall search query string from the UI filters. Returns '' when nothing is selected. */
export function buildQuery(f: SearchFilters): string {
  const parts: string[] = []
  const text = f.text.trim()
  if (text) parts.push(text)
  if (f.set) parts.push(`set:${f.set}`)
  if (f.type) parts.push(`t:${f.type.toLowerCase()}`)
  if (f.rarity) parts.push(`r:${f.rarity}`)
  if (f.colors.length === 1) {
    parts.push(`c:${f.colors[0]}`)
  } else if (f.colors.length > 1) {
    parts.push(`(${f.colors.map((c) => `c:${c}`).join(' or ')})`)
  }
  return parts.join(' ')
}

export function searchUrl(query: string, order: SortKey, dir: 'auto' | 'asc' | 'desc' = 'auto'): string {
  const params = new URLSearchParams({ q: query, order, dir, unique: 'cards' })
  return `${API}/cards/search?${params.toString()}`
}

export async function searchCards(url: string, signal?: AbortSignal): Promise<SearchPage> {
  const list = await getJson<List<Card>>(url, signal)
  return {
    cards: list.data,
    hasMore: list.has_more,
    nextPage: list.next_page,
    total: list.total_cards ?? list.data.length,
  }
}

export function getFaces(card: Card): CardFace[] {
  return card.card_faces && card.card_faces.length > 0 && !card.image_uris ? card.card_faces : [card]
}

export function getImage(card: Card, face = 0, size: keyof ImageUris = 'normal'): string | undefined {
  const faces = getFaces(card)
  return (faces[face] ?? faces[0]).image_uris?.[size] ?? card.image_uris?.[size]
}

export function formatPrice(value?: string | null): string {
  if (!value) return '—'
  const n = Number(value)
  return Number.isFinite(n) ? `$${n.toFixed(2)}` : '—'
}
