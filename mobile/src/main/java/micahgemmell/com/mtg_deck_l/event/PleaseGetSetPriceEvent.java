package micahgemmell.com.mtg_deck_l.event;

/**
 * Created by sonicemerald on 9/1/14.
 */
public class PleaseGetSetPriceEvent {
    private String mCardSet;

    public PleaseGetSetPriceEvent(String set) {
        mCardSet = set;
    }
    public String getCardSet(){
        return mCardSet;
    }
  }
