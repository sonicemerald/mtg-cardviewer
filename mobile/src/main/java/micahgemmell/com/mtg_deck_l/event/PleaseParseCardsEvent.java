package micahgemmell.com.mtg_deck_l.event;

import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
import micahgemmell.com.mtg_deck_l.helpers.CardApiClient;

/**
 * Created by sonicemerald on 9/1/14.
 */
public class PleaseParseCardsEvent {
    private String mSet;

    public PleaseParseCardsEvent(String set) {
        mSet = set;
        CardApiClient c = new CardApiClient(mSet, BusProvider.getInstance());
    }
    public String getURL(){
        return mSet;
    }
  }