package micahgemmell.com.mtg_deck_l;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
import micahgemmell.com.mtg_deck_l.helpers.CardApi;
import micahgemmell.com.mtg_deck_l.helpers.CardApiClient;
//import micahgemmell.com.mtg_deck_l.helpers.CardPriceApi;
//import micahgemmell.com.mtg_deck_l.helpers.CardPriceApiClient;
import micahgemmell.com.mtg_deck_l.helpers.SetPriceApi;
import micahgemmell.com.mtg_deck_l.helpers.SetPriceApiClient;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


/**
 * Created by sonicemerald on 9/1/14.
 */
public class mtgApplication extends Application {
        private Bus mBus = BusProvider.getInstance();
        private CardApiClient mCardClient;
        private SetPriceApiClient mSetPriceClient;

        @Override
        public void onCreate() {
            super.onCreate();
            mCardClient = new CardApiClient(buildCardApi(), mBus);
            mSetPriceClient = new SetPriceApiClient(buildPriceApi(), mBus);
            mBus.register(mCardClient);
            mBus.register(mSetPriceClient);
            mBus.register(this);

        }

    private CardApi buildCardApi(){

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return new RestAdapter.Builder().setEndpoint("http://www.mtgjson.com/json")
                .setConverter(new GsonConverter(gson)).build().create(CardApi.class);
    }

    private SetPriceApi buildPriceApi(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return new RestAdapter.Builder().setEndpoint("http://notional-buffer-750.appspot.com/api/tcgplayer")
                .setConverter(new GsonConverter(gson)).build().create(SetPriceApi.class);
    }

}
