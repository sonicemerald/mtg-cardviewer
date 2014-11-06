package micahgemmell.com.mtg_deck_l.helpers;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.CardPrice;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sonicemerald on 9/3/14.
 */
public interface SetPriceApi {
        @GET("/setPrices.json")
        void getPrices(@Query("cardset") String set, Callback<List<CardPrice>> response);
    }
