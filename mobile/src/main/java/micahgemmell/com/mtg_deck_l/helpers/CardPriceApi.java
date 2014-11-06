package micahgemmell.com.mtg_deck_l.helpers;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by sonicemerald on 9/3/14.
 */
public interface CardPriceApi {
        @GET("/price.json")
        void getPrice(@Query("cardname") String name, @Query("cardset") String set, Callback<List<String>> response);
    }
