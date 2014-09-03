package micahgemmell.com.mtg_deck_l.event;

import android.util.Log;

import retrofit.RetrofitError;

/**
 * Created by sonicemerald on 9/2/14.
 */
public class ApiErrorEvent {
    public ApiErrorEvent(RetrofitError error){
        Log.e("apiError", error.toString());
    }
}
