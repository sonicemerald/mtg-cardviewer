package micahgemmell.com.mtg_deck_l.helpers;

import micahgemmell.com.mtg_deck_l.Card.Set;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by sonicemerald on 9/3/14.
 */
public interface CardApi {
        @GET("/{set}.json")
        void getSet(@Path("set") String set, Callback<Set> response);

    }
