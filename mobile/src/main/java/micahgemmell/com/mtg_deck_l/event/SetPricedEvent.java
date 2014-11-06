package micahgemmell.com.mtg_deck_l.event;

import android.util.Log;
import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.CardPrice;

/**
 * Created by sonicemerald on 9/1/14.
 * This will be called when the application has finished parsing the JSON file.
 */
public class SetPricedEvent {
    private List<CardPrice> array;
    private String set;

    public SetPricedEvent(List<CardPrice> array, String set) {
        this.array = array;
        this.set = set;
        Log.d("event", "set " + set +" has been priced.");
    }

    public List<CardPrice> getPricesArray() { return array; }

}