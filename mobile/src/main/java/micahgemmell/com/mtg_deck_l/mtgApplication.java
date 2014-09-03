package micahgemmell.com.mtg_deck_l;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
import micahgemmell.com.mtg_deck_l.helpers.CardApi;
import micahgemmell.com.mtg_deck_l.helpers.CardApiClient;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;


/**
 * Created by sonicemerald on 9/1/14.
 */
public class mtgApplication extends Application {
        private Bus mBus = BusProvider.getInstance();
        private CardApiClient mCardClient;

        @Override
        public void onCreate() {
            super.onCreate();
            mCardClient = new CardApiClient(buildApi(), mBus);
            mBus.register(mCardClient);
            mBus.register(this);

        }

    private CardApi buildApi(){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        return new RestAdapter.Builder().setEndpoint("http://www.mtgjson.com/json")
                .setConverter(new GsonConverter(gson)).build().create(CardApi.class);
    }
}
