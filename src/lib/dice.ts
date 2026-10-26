export const DICE = [4, 6, 8, 10, 12, 20] as const

export type Rng = () => number

/** Rolls a die with `sides` faces; every face from 1 to `sides` is equally likely. */
export function roll(sides: number, rng: Rng = Math.random): number {
  return 1 + Math.floor(rng() * sides)
}

export function flipCoin(rng: Rng = Math.random): 'Heads' | 'Tails' {
  return rng() < 0.5 ? 'Heads' : 'Tails'
}
