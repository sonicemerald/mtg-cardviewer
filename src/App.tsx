import { useEffect, useState } from 'react'
import { Browse } from './components/Browse'
import { DeckList } from './components/DeckList'
import { LifeCounter } from './components/LifeCounter'
import { ManaCalculator } from './components/ManaCalculator'
import { deckCount, useDeck } from './lib/deck'

const TABS = [
  { id: 'browse', label: 'Browse' },
  { id: 'deck', label: 'Deck' },
  { id: 'mana', label: 'Mana' },
  { id: 'life', label: 'Life' },
] as const

type Tab = (typeof TABS)[number]['id']

function tabFromHash(): Tab {
  const id = window.location.hash.replace('#', '')
  return TABS.some((t) => t.id === id) ? (id as Tab) : 'browse'
}

export default function App() {
  const [tab, setTab] = useState<Tab>(tabFromHash)
  const { deck, add, adjust, clear } = useDeck()

  useEffect(() => {
    const onHash = () => setTab(tabFromHash())
    window.addEventListener('hashchange', onHash)
    return () => window.removeEventListener('hashchange', onHash)
  }, [])

  const go = (id: Tab) => {
    window.location.hash = id
    setTab(id)
  }

  return (
    <div className="app">
      <header>
        <h1>MTG Library</h1>
        <nav aria-label="Main">
          {TABS.map((t) => (
            <button key={t.id} className={tab === t.id ? 'active' : ''} onClick={() => go(t.id)}>
              {t.label}
              {t.id === 'deck' && deck.length > 0 ? ` (${deckCount(deck)})` : ''}
            </button>
          ))}
        </nav>
      </header>

      <main>
        {/* Browse stays mounted so search results survive switching tabs. */}
        <div hidden={tab !== 'browse'}>
          <Browse onAdd={add} countInDeck={(id) => deck.find((e) => e.id === id)?.qty ?? 0} />
        </div>
        {tab === 'deck' && <DeckList deck={deck} onAdjust={adjust} onClear={clear} />}
        {tab === 'mana' && <ManaCalculator />}
        {tab === 'life' && <LifeCounter />}
      </main>

      <footer>
        Card data and images from <a href="https://scryfall.com">Scryfall</a>. Magic: The Gathering is © Wizards of the
        Coast; this is an unofficial fan project.
      </footer>
    </div>
  )
}
