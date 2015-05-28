package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;

// The ListViewFragment is a fragment which holds a list. In the application, we used to load the full set of magic oldCards into it, as well as the deck.
// In this navigationDrawer implementation, we will not be using this listView_Fragment to load the complete list of magic oldCards.
public class SpinnerFragment
        extends Fragment
        implements AdapterView.OnItemSelectedListener {
    //region VARIABLES
    Context context;
    SpinnerInterface mListener;
    SpinnerInterface sListener;
    private Spinner sortSetSpinner;

    public ArrayAdapter<String> adapterforSetArray;
    ArrayAdapter<String> adapterforSortArray;
    private AdapterView.OnItemSelectedListener listener;
    private AdapterView.OnItemClickListener clickListener;
    //endregion

    //"Constructor" for Fragments;
    public static SpinnerFragment newInstance(){
        SpinnerFragment f = new SpinnerFragment();
        return f;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.context = activity;
        if ((activity instanceof SpinnerInterface))
        {
            this.mListener = ((SpinnerInterface)activity);
            this.sListener = ((SpinnerInterface)activity);
            return;
        }
        throw new ClassCastException(activity.toString() + " is lame!");
    }

    public void onDetach(){
        super.onDetach();
        mListener = null;
        sListener = null;
        Log.d("3", "detached");
    }

    public void onPause(){
        super.onPause();
        Log.d("3", "paused");
        getFragmentManager().saveFragmentInstanceState(this);
    }

    public void onResume(){
        super.onResume();
        Log.d("", "resuming list_f"); //+ cards.toString());
    }



    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View localView = paramLayoutInflater.inflate(R.layout.fragment_spinners, paramViewGroup, false);
        this.sortSetSpinner = (Spinner) localView.findViewById(R.id.filterSetSpinner);
        String[] cardSet_array = getResources().getStringArray(R.array.setNames);
        adapterforSetArray = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, cardSet_array);
        this.sortSetSpinner.setAdapter(adapterforSetArray);
        listener = this;
        this.sortSetSpinner.post(new Runnable() {
            public void run() {
                sortSetSpinner.setOnItemSelectedListener(listener);
                int initialposition = (adapterforSetArray.getCount()-1);
                int spinnerposition = mListener.getSpinnerPosition();
                //if(mListener.getSpinnerPosition() = initialposition) { spinnerposition = initialposition; }
                sortSetSpinner.setSelection(spinnerposition);
            }
        });

        Spinner sortSpinner = (Spinner) localView.findViewById(R.id.sortBySpinner);
        String[] rarity_array = getResources().getStringArray(R.array.sortby);
        adapterforSortArray = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, rarity_array);
        sortSpinner.setAdapter(adapterforSortArray);
        sortSpinner.setOnItemSelectedListener(this);
//
//        this.listView = (ListView)localView.findViewById(R.id.listView);
//        this.adapter = new CardListAdapter(this.context, R.id.card_title, cards);
//        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
//        alphaInAnimationAdapter.setAbsListView(this.listView);
//        this.listView.setAdapter(alphaInAnimationAdapter);
//        this.listView.setDivider(null);
//        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                mListener.onCardClicked(i);}});
//        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                return false;
//            }
//        });
        return localView;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            this.sListener.spinnerItemSelected(position, parent.getId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public static abstract interface SpinnerInterface
    {
        public abstract void spinnerItemSelected(int position, int id);
        public abstract int getSpinnerPosition();
    }
}
