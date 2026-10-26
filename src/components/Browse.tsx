import { useCallback, useEffect, useRef, useState } from 'react'
import {
  CARD_TYPES,
  COLORS,
  EMPTY_FILTERS,
  RARITIES,
  SORT_OPTIONS,
  buildQuery,
  fetchSets,
  formatPrice,
  getImage,
  searchCards,
  searchUrl,
  type Card,
  type MtgSet,
  type SearchFilters,
  type SortKey,
} from '../lib/scryfall'
import { CardModal } from './CardModal'
import { Symbols } from './Symbols'

interface Props {
  onAdd: (card: Card) => void
  countInDeck: (id: string) => number
}

type View = 'grid' | 'list'

export function Browse({ onAdd, countInDeck }: Props) {
  const [sets, setSets] = useState<MtgSet[]>([])
  const [filters, setFilters] = useState<SearchFilters>(EMPTY_FILTERS)
  const [sort, setSort] = useState<SortKey>('name')
  const [view, setView] = useState<View>('grid')
  const [cards, setCards] = useState<Card[]>([])
  const [total, setTotal] = useState(0)
  const [nextPage, setNextPage] = useState<string>()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string>()
  const [selected, setSelected] = useState<Card>()
  const abortRef = useRef<AbortController | null>(null)

  useEffect(() => {
    const ctrl = new AbortController()
    fetchSets(ctrl.signal)
      .then(setSets)
      .catch((e: unknown) => {
        if ((e as Error).name !== 'AbortError') setError('Could not load the set list. Check your connection.')
      })
    return () => ctrl.abort()
  }, [])

  const query = buildQuery(filters)

  // Re-run the search whenever the query or sort changes (text input is debounced).
  useEffect(() => {
    abortRef.current?.abort()
    if (!query) {
      setCards([])
      setTotal(0)
      setNextPage(undefined)
      setError(undefined)
      setLoading(false)
      return
    }
    const ctrl = new AbortController()
    abortRef.current = ctrl
    setLoading(true)
    setError(undefined)
    const timer = setTimeout(() => {
      searchCards(searchUrl(query, sort), ctrl.signal)
        .then((page) => {
          setCards(page.cards)
          setTotal(page.total)
          setNextPage(page.hasMore ? page.nextPage : undefined)
        })
        .catch((e: unknown) => {
          if ((e as Error).name === 'AbortError') return
          setCards([])
          setTotal(0)
          setNextPage(undefined)
          setError((e as Error).message)
        })
        .finally(() => {
          if (!ctrl.signal.aborted) setLoading(false)
        })
    }, 350)
    return () => {
      clearTimeout(timer)
      ctrl.abort()
    }
  }, [query, sort])

  const loadMore = useCallback(() => {
    if (!nextPage) return
    const ctrl = new AbortController()
    abortRef.current = ctrl
    setLoading(true)
    searchCards(nextPage, ctrl.signal)
      .then((page) => {
        setCards((prev) => [...prev, ...page.cards])
        setNextPage(page.hasMore ? page.nextPage : undefined)
      })
      .catch((e: unknown) => {
        if ((e as Error).name !== 'AbortError') setError((e as Error).message)
      })
      .finally(() => setLoading(false))
  }, [nextPage])

  const update = (patch: Partial<SearchFilters>) => setFilters((f) => ({ ...f, ...patch }))
  const toggleColor = (code: string) =>
    update({
      colors: filters.colors.includes(code) ? filters.colors.filter((c) => c !== code) : [...filters.colors, code],
    })

  return (
    <section>
      <div className="controls">
        <input
          type="search"
          placeholder="Search cards — try “goblin”, “t:dragon”, or “o:flying”"
          value={filters.text}
          onChange={(e) => update({ text: e.target.value })}
          aria-label="Search cards"
        />
        <div className="row">
          <select value={filters.set} onChange={(e) => update({ set: e.target.value })} aria-label="Set">
            <option value="">Any set</option>
            {sets.map((s) => (
              <option key={s.code} value={s.code}>
                {s.name} ({s.code.toUpperCase()})
              </option>
            ))}
          </select>
          <select value={filters.type} onChange={(e) => update({ type: e.target.value })} aria-label="Card type">
            <option value="">Any type</option>
            {CARD_TYPES.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
          <select value={filters.rarity} onChange={(e) => update({ rarity: e.target.value })} aria-label="Rarity">
            <option value="">Any rarity</option>
            {RARITIES.map((r) => (
              <option key={r} value={r}>
                {r[0].toUpperCase() + r.slice(1)}
              </option>
            ))}
          </select>
          <select value={sort} onChange={(e) => setSort(e.target.value as SortKey)} aria-label="Sort by">
            {SORT_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>
                Sort: {o.label}
              </option>
            ))}
          </select>
        </div>
        <div className="row">
          <div className="chips" role="group" aria-label="Colors">
            {COLORS.map((c) => (
              <button
                key={c.code}
                className={`chip color-${c.code}${filters.colors.includes(c.code) ? ' on' : ''}`}
                aria-pressed={filters.colors.includes(c.code)}
                onClick={() => toggleColor(c.code)}
              >
                {c.label}
              </button>
            ))}
          </div>
          <div className="spacer" />
          <button className="secondary" onClick={() => setFilters(EMPTY_FILTERS)} disabled={!query}>
            Clear
          </button>
          <button
            className="secondary"
            onClick={() => setView(view === 'grid' ? 'list' : 'grid')}
            aria-label="Toggle grid or list view"
          >
            {view === 'grid' ? 'List view' : 'Grid view'}
          </button>
        </div>
      </div>

      {error && (
        <p className="notice error" role="alert">
          {error}
        </p>
      )}

      {!query && !error && (
        <p className="notice">Pick a set or start typing to browse cards. Search uses full Scryfall syntax.</p>
      )}

      {query && !loading && !error && cards.length === 0 && <p className="notice">No cards found.</p>}

      {cards.length > 0 && (
        <p className="muted count">
          Showing {cards.length.toLocaleString()} of {total.toLocaleString()} cards
        </p>
      )}

      {view === 'grid' ? (
        <div className="grid">
          {cards.map((card) => (
            <button key={card.id} className="tile" onClick={() => setSelected(card)} aria-label={card.name}>
              {getImage(card, 0, 'normal') ? (
                <img src={getImage(card, 0, 'normal')} alt={card.name} loading="lazy" />
              ) : (
                <span className="no-image">{card.name}</span>
              )}
              <span className="tile-price">{formatPrice(card.prices.usd)}</span>
            </button>
          ))}
        </div>
      ) : (
        <ul className="list">
          {cards.map((card) => (
            <li key={card.id}>
              <button className="list-row" onClick={() => setSelected(card)}>
                <span className="list-name">{card.name}</span>
                <span className="list-cost">
                  <Symbols text={card.mana_cost ?? card.card_faces?.[0]?.mana_cost} />
                </span>
                <span className="list-type muted">{card.type_line}</span>
                <span className="list-price">{formatPrice(card.prices.usd)}</span>
              </button>
            </li>
          ))}
        </ul>
      )}

      {loading && <p className="notice">Loading…</p>}

      {nextPage && !loading && (
        <div className="center">
          <button onClick={loadMore}>Load more</button>
        </div>
      )}

      {selected && (
        <CardModal
          card={selected}
          onClose={() => setSelected(undefined)}
          onAdd={onAdd}
          inDeck={countInDeck(selected.id)}
        />
      )}
    </section>
  )
}
