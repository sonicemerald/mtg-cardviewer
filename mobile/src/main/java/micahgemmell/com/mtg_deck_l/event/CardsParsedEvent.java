package micahgemmell.com.mtg_deck_l.event;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;

/**
 * Created by sonicemerald on 9/1/14.
 * This will be called when the application has finished parsing, and will hand the cards off.
 */
public class CardsParsedEvent {
    private List<Card> parsedCards;

    public CardsParsedEvent(List<Card> cards){
        this.parsedCards = cards;
    }

    public List<Card> getParsedCards() {
        return parsedCards;
    }

//    public void setParsedCards(List<Card> parsedCards) {
//        this.parsedCards = parsedCards;
//    }
}
