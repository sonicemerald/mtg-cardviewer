package micahgemmell.com.mtg_deck_l;

import android.app.Application;

import com.squareup.otto.Bus;
import micahgemmell.com.mtg_deck_l.helpers.BusProvider;


/**
 * Created by sonicemerald on 9/1/14.
 */
public class mtgApplication extends Application {
        private Bus mBus = BusProvider.getInstance();

        @Override
        public void onCreate() {
            super.onCreate();
            mBus.register(this);
        }
}
