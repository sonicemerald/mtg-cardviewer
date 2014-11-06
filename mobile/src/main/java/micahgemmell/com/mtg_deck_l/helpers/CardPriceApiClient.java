package micahgemmell.com.mtg_deck_l.helpers;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
import micahgemmell.com.mtg_deck_l.event.CardPricedEvent;
import micahgemmell.com.mtg_deck_l.event.PleaseGetCardPriceEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sonicemerald on 9/1/14.
 *
 */
public class CardPriceApiClient {
    private Bus mBus;
    private CardPriceApi mApi;
    private Card mCard;

    public CardPriceApiClient(CardPriceApi api, Bus bus) {
        mBus = bus;
        mApi = api;
    }

    @Subscribe
    public void loadCardPrice(final PleaseGetCardPriceEvent event){
        mCard = event.getCard();
        mApi.getPrice(event.getCardName(), event.getCardSet(), new Callback<List<String>>() {
            @Override
            public void success(List<String> array, Response response) {
                mBus.post(new CardPricedEvent(array, mCard));
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }
}
