package micahgemmell.com.mtg_deck_l.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.Card.Set;
import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;
import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by sonicemerald on 9/1/14.
 *
 */
public class CardApiClient {
    private Bus mBus;
    private static CardApiInterface sCardApiInterface;

    public CardApiClient(String set, Bus bus){
        mBus = bus;
        getCardApi();
        sCardApiInterface.getSet(set, new Callback<Set>() {
            @Override
            public void success(Set set, Response response) {
                mBus.post(new CardsParsedEvent(set.getCards()));
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }

    public CardApiInterface getCardApi() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://www.mtgjson.com/json").setConverter(new GsonConverter(gson)).build();
        sCardApiInterface = restAdapter.create(CardApiInterface.class);
        return sCardApiInterface;
    }

    public interface CardApiInterface {
        @GET("/{set}.json")
        void getSet(@Path("set") String set,Callback<Set> response);
    }
}
