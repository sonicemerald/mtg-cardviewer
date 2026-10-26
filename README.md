# mtg-cardviewer

A Magic: The Gathering card browser, rebuilt for 2026 as a web app. It started life in 2015 as an Android app
(a rework of [mtg-deckbuilder](https://github.com/sonicemerald/mtg-deckbuilder)); the old Android/Wear project
is still in the git history before the `modernize-web-app` change.

Card data, prices and images come from the [Scryfall API](https://scryfall.com/docs/api).

## Features

- **Browse** – pick any set, search with full [Scryfall syntax](https://scryfall.com/docs/syntax), filter by
  color, type and rarity, sort by name, set number, mana value, color, rarity, price or popularity. Grid and list
  views, paged results, and a card detail view with oracle text, flip support for double-faced cards, prices and
  format legality.
- **Deck** – add cards from the detail view, adjust quantities, see an estimated price, and copy the list as text.
  Saved in your browser (localStorage).
- **Mana** – mana source calculator: enter your color symbol counts and land total to get a land split.
- **Life** – two-player life counter (opponent side rotated), plus dice (d4–d20) and a coin flip.

## Development

Requires Node 20+.

```sh
npm install
npm run dev        # local dev server
npm test           # unit tests (Vitest)
npm run build      # type-check + production build into dist/
npm run preview    # serve the production build
```

The build uses a relative base path, so `dist/` can be hosted from any static host or subfolder.

## What changed from the Android version

- Replaced the defunct mtgjson v2 and `appspot` price endpoints with Scryfall (live prices, images, symbols).
- Fixed the old dice roller, which could never roll the highest number on a die.
- Fixed the mana calculator dividing by zero with no symbols entered, and made land counts always add up exactly.
- Dropped the Wear OS companion and the jcenter-era dependencies (Retrofit 1, Otto, Picasso, async-http).

Magic: The Gathering is © Wizards of the Coast. This is an unofficial fan project and is not affiliated with
Wizards of the Coast or Scryfall.
