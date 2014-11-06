package micahgemmell.com.mtg_deck_l.event;

import micahgemmell.com.mtg_deck_l.Card.Card;

/**
 * Created by sonicemerald on 9/1/14.
 */
public class PleaseGetCardPriceEvent {
    private Card mCard;

    public PleaseGetCardPriceEvent(Card card) {
        mCard = card;
    }
    public String getCardName(){
        return mCard.getName();
    }
    public String getCardSet(){
        return mCard.getSetName();
    }
    public Card getCard(){ return mCard; }
  }
