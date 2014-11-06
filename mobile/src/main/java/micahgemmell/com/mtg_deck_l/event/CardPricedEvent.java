package micahgemmell.com.mtg_deck_l.event;

import android.util.Log;
import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;

/**
 * Created by sonicemerald on 9/1/14.
 * This will be called when the application has finished parsing the Json file.
 */
public class CardPricedEvent {
    private List<String> array;
    private Card card;
    private String lowPrice;
    private String medPrice;
    private String highPrice;

    public CardPricedEvent(List<String> array, Card card) {
        this.array = array;
        this.card = card;

        try{
            this.lowPrice = array.get(0).toString();
            this.medPrice = array.get(1).toString();
            this.highPrice = array.get(2).toString();
        } catch (Exception e) {
            Log.d("exception", "Error");
        }

        this.card.setLowPrice(lowPrice);
        this.card.setMedPrice(medPrice);
        this.card.setHighPrice(highPrice);

        Log.d("event", "card " + this.card.getName() + "has been priced!");
    }

    public List<String> getPricesArray() { return array; }
    public Card getCard() { return card; }

}