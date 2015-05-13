package micahgemmell.com.mtg_deck_l.helpers;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import micahgemmell.com.mtg_deck_l.Card.Set;
import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;
import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sonicemerald on 9/1/14.
 *
 */
public class CardApiClient {
    private Bus mBus;
    private static CardApi mApi;

    public CardApiClient(CardApi api, Bus bus) {
        mBus = bus;
        mApi = api;
    }

    @Subscribe
    public void loadCards(final PleaseParseCardsEvent event){
        mApi.getSet(event.getSet(), new Callback<Set>() {
            @Override
            public void success(Set set, Response response) {
                mBus.post(new CardsParsedEvent(set.getCards(), set.getCode(), set.getName()));
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }
}
