import { useState } from 'react'
import { deckCount, deckValue, exportDeck, type DeckEntry } from '../lib/deck'
import { formatPrice } from '../lib/scryfall'
import { Symbols } from './Symbols'

interface Props {
  deck: DeckEntry[]
  onAdjust: (id: string, delta: number) => void
  onClear: () => void
}

export function DeckList({ deck, onAdjust, onClear }: Props) {
  const [copied, setCopied] = useState(false)
  const sorted = [...deck].sort((a, b) => a.cmc - b.cmc || a.name.localeCompare(b.name))

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(exportDeck(deck))
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    } catch {
      /* clipboard blocked – nothing else to do */
    }
  }

  return (
    <section className="panel">
      <h2>Deck list</h2>
      {deck.length === 0 ? (
        <p className="notice">Your deck is empty. Open a card in Browse and choose “Add to deck”.</p>
      ) : (
        <>
          <p className="muted">
            {deckCount(deck)} cards · est. {formatPrice(deckValue(deck).toFixed(2))}
          </p>
          <ul className="deck">
            {sorted.map((e) => (
              <li key={e.id}>
                <span className="qty-controls">
                  <button className="secondary" onClick={() => onAdjust(e.id, -1)} aria-label={`Remove one ${e.name}`}>
                    −
                  </button>
                  <span className="qty">{e.qty}</span>
                  <button className="secondary" onClick={() => onAdjust(e.id, 1)} aria-label={`Add one ${e.name}`}>
                    +
                  </button>
                </span>
                <span className="deck-name">{e.name}</span>
                <span className="deck-cost">
                  <Symbols text={e.mana_cost} />
                </span>
                <span className="muted deck-type">{e.type_line}</span>
              </li>
            ))}
          </ul>
          <div className="row">
            <button onClick={copy}>{copied ? 'Copied!' : 'Copy as text'}</button>
            <button
              className="secondary"
              onClick={() => {
                if (window.confirm('Remove every card from your deck?')) onClear()
              }}
            >
              Clear deck
            </button>
          </div>
        </>
      )}
    </section>
  )
}
