package micahgemmell.com.mtg_deck_l.helpers;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.CardPrice;
import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
import micahgemmell.com.mtg_deck_l.event.PleaseGetSetPriceEvent;
import micahgemmell.com.mtg_deck_l.event.SetPricedEvent;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sonicemerald on 9/1/14.
 *
 */
public class SetPriceApiClient {
    private Bus mBus;
    private SetPriceApi mApi;
    private String mSet;

    public SetPriceApiClient(SetPriceApi api, Bus bus) {
        mBus = bus;
        mApi = api;
    }

    @Subscribe
    public void loadCardPrice(final PleaseGetSetPriceEvent event){
        mSet = event.getCardSet();
        mApi.getPrices(event.getCardSet(), new Callback<List<CardPrice>>() {
            @Override
            public void success(List<CardPrice> array, Response response) {
                mBus.post(new SetPricedEvent(array, mSet));
            }

            @Override
            public void failure(RetrofitError error) {
                mBus.post(new ApiErrorEvent(error));
            }
        });
    }
}
