package micahgemmell.com.mtg_deck_l.event;

import android.util.Log;
import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;

/**
 * Created by sonicemerald on 9/1/14.
 * This will be called when the application has finished parsing, and will hand the cards off.
 */
public class CardsParsedEvent {
    private List<Card> parsedCards;

    public CardsParsedEvent(List<Card> cards, String set, String setName) {
        this.parsedCards = cards;
        for (Card a : this.parsedCards) {
            a.setSet(set);
            a.setSetName(setName);
        }
        Log.d("event", "cards have been parsed!");
    }

//        this.cardPrices = new ArrayList<JSONArray>();
//        int i = 0;
//        for (Card a : this.parsedCards) {
//            String url = "http://magictcgprices.appspot.com/api/tcgplayer/price.json?cardname=";
//            a.setSet(set);
//            a.setSetName(setName);
//            url = url+a.getName()+"&cardset="+setName;
//
//            AsyncHttpClient client = new AsyncHttpClient();
//            // Spin up a new thread, and fetch the JSON
//
//            client.get(url, new AsyncHttpResponseHandler() {
//                // When the JSON has been fetched, we will process it
//                @Override
//                public void onSuccess(String response) {
//                    try {
//                        JSONArray j = new JSONArray(response);
//                        cardPrices.add(j);
//                        setCardPrices(cardPrices);
//                    } catch (JSONException e) {
//                        Log.d("JSON Parse", e.toString());
//                    }
//
//                    Log.d("event", "cards have been parsed!");
//                }
//            });
//        }

//    private void setCardPrices(List<JSONArray> array) {
//            try {
//                this.parsedCards.get(i).setLowPrice(array.get(i).get(0).toString());
//                this.parsedCards.get(i).setMedPrice(array.get(i).get(1).toString());
//                this.parsedCards.get(i).setHighPrice(array.get(i).get(2).toString());
//            } catch (JSONException e) {
//                Log.d("JSON Parse", e.toString());
//            }
//        }

    public List<Card> getParsedCards() {
          return parsedCards;
                }

}