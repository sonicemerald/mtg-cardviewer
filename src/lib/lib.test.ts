import { describe, expect, it } from 'vitest'
import { addEntry, changeQty, deckCount, deckValue, exportDeck, type DeckEntry } from './deck'
import { flipCoin, roll } from './dice'
import { calculateMana } from './mana'
import {
  buildQuery,
  EMPTY_FILTERS,
  filterBrowsableSets,
  formatPrice,
  getFaces,
  getImage,
  searchUrl,
  type Card,
  type MtgSet,
} from './scryfall'
import { symbolCode, tokenize } from './symbols'

describe('calculateMana', () => {
  const none = { W: 0, U: 0, B: 0, R: 0, G: 0 }

  it('splits lands proportionally and always sums to the land count', () => {
    const r = calculateMana({ W: 7, U: 0, B: 0, R: 5, G: 4 }, 17)
    expect(Object.values(r.lands).reduce((a, b) => a + b, 0)).toBe(17)
    expect(r.totalSymbols).toBe(16)
    expect(r.lands.W).toBeGreaterThan(r.lands.R)
    expect(r.lands.R).toBeGreaterThanOrEqual(r.lands.G)
  })

  it('handles a single color', () => {
    expect(calculateMana({ ...none, G: 9 }, 24).lands.G).toBe(24)
  })

  it('returns zeros instead of dividing by zero', () => {
    const r = calculateMana(none, 17)
    expect(r.totalSymbols).toBe(0)
    expect(Object.values(r.lands)).toEqual([0, 0, 0, 0, 0])
  })

  it('ignores negative and non-numeric input', () => {
    const r = calculateMana({ W: -3, U: NaN, B: 2, R: 0, G: 0 }, 10)
    expect(r.lands.B).toBe(10)
  })
})

describe('dice', () => {
  it('covers every face from 1 to sides (the old app never rolled the top number)', () => {
    expect(roll(20, () => 0)).toBe(1)
    expect(roll(20, () => 0.999999)).toBe(20)
    expect(roll(10, () => 0.95)).toBe(10)
  })

  it('flips both outcomes', () => {
    expect(flipCoin(() => 0.1)).toBe('Heads')
    expect(flipCoin(() => 0.9)).toBe('Tails')
  })
})

describe('symbols', () => {
  it('tokenizes mixed text and symbols', () => {
    expect(tokenize('{2}{W/U}: draw')).toEqual([
      { kind: 'symbol', code: '2', label: '2' },
      { kind: 'symbol', code: 'WU', label: 'W/U' },
      { kind: 'text', value: ': draw' },
    ])
  })

  it('strips slashes from hybrid and phyrexian symbols', () => {
    expect(symbolCode('G/P')).toBe('GP')
    expect(symbolCode('2/W')).toBe('2W')
  })

  it('returns plain text untouched', () => {
    expect(tokenize('Flying')).toEqual([{ kind: 'text', value: 'Flying' }])
  })
})

describe('scryfall helpers', () => {
  it('builds queries from filters', () => {
    expect(buildQuery(EMPTY_FILTERS)).toBe('')
    expect(buildQuery({ ...EMPTY_FILTERS, set: 'mh3' })).toBe('set:mh3')
    expect(
      buildQuery({ text: ' goblin ', set: 'mh3', colors: ['r'], type: 'Creature', rarity: 'rare' }),
    ).toBe('goblin set:mh3 t:creature r:rare c:r')
    expect(buildQuery({ ...EMPTY_FILTERS, colors: ['w', 'u'] })).toBe('(c:w or c:u)')
  })

  it('encodes the search url', () => {
    const url = new URL(searchUrl('t:elf c:g', 'cmc'))
    expect(url.pathname).toBe('/cards/search')
    expect(url.searchParams.get('q')).toBe('t:elf c:g')
    expect(url.searchParams.get('order')).toBe('cmc')
  })

  it('keeps only browsable sets, newest first', () => {
    const mk = (code: string, set_type: string, released_at: string, card_count = 10): MtgSet => ({
      code,
      name: code,
      set_type,
      released_at,
      card_count,
      icon_svg_uri: '',
    })
    const out = filterBrowsableSets([
      mk('old', 'expansion', '1999-01-01'),
      mk('tok', 'token', '2020-01-01'),
      mk('new', 'core', '2024-07-01'),
      mk('empty', 'expansion', '2025-01-01', 0),
    ])
    expect(out.map((s) => s.code)).toEqual(['new', 'old'])
  })

  it('formats prices', () => {
    expect(formatPrice('1.5')).toBe('$1.50')
    expect(formatPrice(null)).toBe('—')
    expect(formatPrice(undefined)).toBe('—')
  })

  it('reads images from double-faced cards', () => {
    const base = { prices: {}, legalities: {} } as unknown as Card
    const dfc = {
      ...base,
      name: 'Front // Back',
      card_faces: [
        { name: 'Front', image_uris: { normal: 'front.jpg' } },
        { name: 'Back', image_uris: { normal: 'back.jpg' } },
      ],
    } as unknown as Card
    expect(getFaces(dfc)).toHaveLength(2)
    expect(getImage(dfc, 0)).toBe('front.jpg')
    expect(getImage(dfc, 1)).toBe('back.jpg')

    const single = { ...base, image_uris: { normal: 'one.jpg' } } as unknown as Card
    expect(getFaces(single)).toHaveLength(1)
    expect(getImage(single)).toBe('one.jpg')
  })
})

describe('deck helpers', () => {
  const card = {
    id: 'a',
    name: 'Lightning Bolt',
    set: 'm10',
    collector_number: '146',
    type_line: 'Instant',
    cmc: 1,
    usd: '2.00',
  }

  it('adds, increments, and removes entries', () => {
    let deck: DeckEntry[] = []
    deck = addEntry(deck, card)
    deck = addEntry(deck, card)
    expect(deck).toHaveLength(1)
    expect(deck[0].qty).toBe(2)
    deck = changeQty(deck, 'a', -1)
    expect(deckCount(deck)).toBe(1)
    deck = changeQty(deck, 'a', -1)
    expect(deck).toEqual([])
  })

  it('totals value and exports text', () => {
    const deck = [{ ...card, qty: 3 }, { ...card, id: 'b', name: 'Counterspell', usd: null, qty: 1 }]
    expect(deckValue(deck)).toBe(6)
    expect(exportDeck(deck)).toBe('1 Counterspell (M10) 146\n3 Lightning Bolt (M10) 146')
  })
})
