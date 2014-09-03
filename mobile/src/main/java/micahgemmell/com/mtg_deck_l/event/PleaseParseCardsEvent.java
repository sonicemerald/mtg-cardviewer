package micahgemmell.com.mtg_deck_l.event;

/**
 * Created by sonicemerald on 9/1/14.
 */
public class PleaseParseCardsEvent {
    private String mSet;

    public PleaseParseCardsEvent(String set) {
        mSet = set;
    }
    public String getSet(){
        return mSet;
    }
  }