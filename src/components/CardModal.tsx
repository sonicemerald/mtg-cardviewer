import { useEffect, useState } from 'react'
import { formatPrice, getFaces, getImage, type Card } from '../lib/scryfall'
import { Symbols } from './Symbols'

interface Props {
  card: Card
  onClose: () => void
  onAdd: (card: Card) => void
  inDeck: number
}

const FORMATS = ['standard', 'pioneer', 'modern', 'legacy', 'vintage', 'commander', 'pauper']

export function CardModal({ card, onClose, onAdd, inDeck }: Props) {
  const [face, setFace] = useState(0)
  const faces = getFaces(card)
  const shown = faces[face] ?? faces[0]
  const image = getImage(card, face, 'large')

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [onClose])

  return (
    <div className="backdrop" onClick={onClose} role="presentation">
      <div
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-label={card.name}
        onClick={(e) => e.stopPropagation()}
      >
        <button className="close" onClick={onClose} aria-label="Close">
          ×
        </button>

        <div className="modal-art">
          {image ? <img src={image} alt={shown.name} /> : <div className="no-image">No image</div>}
          {faces.length > 1 && (
            <button className="secondary" onClick={() => setFace((face + 1) % faces.length)}>
              Flip card
            </button>
          )}
        </div>

        <div className="modal-body">
          <h2>{shown.name}</h2>
          <p className="muted">
            <Symbols text={shown.mana_cost ?? card.mana_cost} />
          </p>
          <p>
            <strong>{shown.type_line ?? card.type_line}</strong>
          </p>
          {shown.oracle_text && (
            <div className="oracle">
              {shown.oracle_text.split('\n').map((line, i) => (
                <p key={i}>
                  <Symbols text={line} />
                </p>
              ))}
            </div>
          )}
          {shown.flavor_text && <p className="flavor">{shown.flavor_text}</p>}
          {shown.power && shown.toughness && (
            <p>
              <strong>
                {shown.power}/{shown.toughness}
              </strong>
            </p>
          )}
          {shown.loyalty && (
            <p>
              <strong>Loyalty {shown.loyalty}</strong>
            </p>
          )}

          <p className="muted">
            {card.set_name} · #{card.collector_number} · {card.rarity}
            {shown.artist ? ` · Illus. ${shown.artist}` : ''}
          </p>

          <dl className="prices">
            <div>
              <dt>USD</dt>
              <dd>{formatPrice(card.prices.usd)}</dd>
            </div>
            <div>
              <dt>Foil</dt>
              <dd>{formatPrice(card.prices.usd_foil)}</dd>
            </div>
          </dl>

          <div className="legalities">
            {FORMATS.map((f) => (
              <span key={f} className={`legal ${card.legalities[f] ?? 'not_legal'}`}>
                {f}
              </span>
            ))}
          </div>

          <div className="actions">
            <button onClick={() => onAdd(card)}>Add to deck{inDeck > 0 ? ` (${inDeck})` : ''}</button>
            <a className="button secondary" href={card.scryfall_uri} target="_blank" rel="noreferrer">
              View on Scryfall
            </a>
          </div>
        </div>
      </div>
    </div>
  )
}
