import { useMemo, useState } from 'react'
import { DECK_PRESETS, MANA_COLORS, calculateMana, type ManaColor, type Symbols } from '../lib/mana'

const EMPTY: Symbols = { W: 0, U: 0, B: 0, R: 0, G: 0 }

export function ManaCalculator() {
  const [symbols, setSymbols] = useState<Symbols>(EMPTY)
  const [lands, setLands] = useState<number>(DECK_PRESETS[0].lands)
  const [preset, setPreset] = useState<string>(DECK_PRESETS[0].id)

  const result = useMemo(() => calculateMana(symbols, lands), [symbols, lands])

  const setSymbol = (code: ManaColor, raw: string) =>
    setSymbols((s) => ({ ...s, [code]: raw === '' ? 0 : Math.max(0, parseInt(raw, 10) || 0) }))

  return (
    <section className="panel">
      <h2>Mana calculator</h2>
      <p className="muted">
        Count how many times each color’s mana symbol appears across your spells to see how many lands of each color
        you should run.
      </p>

      <div className="row">
        <label>
          Deck type
          <select
            value={preset}
            onChange={(e) => {
              const p = DECK_PRESETS.find((d) => d.id === e.target.value)
              if (!p) return
              setPreset(p.id)
              setLands(p.lands)
            }}
          >
            {DECK_PRESETS.map((p) => (
              <option key={p.id} value={p.id}>
                {p.label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Lands in deck
          <input
            type="number"
            min={0}
            value={lands}
            onChange={(e) => setLands(Math.max(0, parseInt(e.target.value, 10) || 0))}
          />
        </label>
      </div>

      <table className="mana-table">
        <thead>
          <tr>
            <th>Color</th>
            <th>Symbols in deck</th>
            <th>Lands</th>
          </tr>
        </thead>
        <tbody>
          {MANA_COLORS.map((c) => (
            <tr key={c.code}>
              <td style={{ color: c.css }}>{c.label}</td>
              <td>
                <input
                  type="number"
                  min={0}
                  inputMode="numeric"
                  aria-label={`${c.label} symbols`}
                  value={symbols[c.code] || ''}
                  placeholder="0"
                  onChange={(e) => setSymbol(c.code, e.target.value)}
                />
              </td>
              <td className="big">{result.lands[c.code]}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <td>Total</td>
            <td>{result.totalSymbols}</td>
            <td className="big">{Object.values(result.lands).reduce((a, b) => a + b, 0)}</td>
          </tr>
        </tfoot>
      </table>
    </section>
  )
}
