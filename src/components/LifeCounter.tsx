import { useState } from 'react'
import { DICE, flipCoin, roll } from '../lib/dice'

function Player({ life, onChange, flipped }: { life: number; onChange: (d: number) => void; flipped?: boolean }) {
  return (
    <div className={`player${flipped ? ' flipped' : ''}`}>
      <div className="life">{life}</div>
      <div className="life-buttons">
        {[-5, -1, 1, 5].map((d) => (
          <button key={d} className={d < 0 ? 'minus' : 'plus'} onClick={() => onChange(d)}>
            {d > 0 ? `+${d}` : d}
          </button>
        ))}
      </div>
    </div>
  )
}

export function LifeCounter() {
  const [start, setStart] = useState(20)
  const [p1, setP1] = useState(20)
  const [p2, setP2] = useState(20)
  const [result, setResult] = useState<string>('Roll a die or flip a coin')

  const reset = (value = start) => {
    setStart(value)
    setP1(value)
    setP2(value)
  }

  return (
    <section className="panel">
      <h2>Life counter</h2>
      <div className="life-board">
        <Player life={p2} onChange={(d) => setP2((l) => l + d)} flipped />
        <div className="divider">
          <label>
            Starting life
            <select value={start} onChange={(e) => reset(Number(e.target.value))}>
              {[20, 30, 40].map((n) => (
                <option key={n} value={n}>
                  {n}
                </option>
              ))}
            </select>
          </label>
          <button className="secondary" onClick={() => reset()}>
            Reset
          </button>
        </div>
        <Player life={p1} onChange={(d) => setP1((l) => l + d)} />
      </div>

      <h3>Dice &amp; coin</h3>
      <div className="row">
        {DICE.map((s) => (
          <button key={s} className="secondary" onClick={() => setResult(`d${s}: ${roll(s)}`)}>
            d{s}
          </button>
        ))}
        <button className="secondary" onClick={() => setResult(flipCoin())}>
          Coin
        </button>
      </div>
      <p className="roll-result" aria-live="polite">
        {result}
      </p>
    </section>
  )
}
