package micahgemmell.com.mtg_deck_l.helpers;
import com.squareup.otto.Bus;

// Provided by Square under the Apache License
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}