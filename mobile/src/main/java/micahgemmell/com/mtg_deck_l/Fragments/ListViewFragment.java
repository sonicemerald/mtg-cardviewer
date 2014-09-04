package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
import micahgemmell.com.mtg_deck_l.R;

import java.util.List;

// The ListViewFragment is a fragment which holds a list. In the application, we used to load the full set of magic oldCards into it, as well as the deck.
// In this navigationDrawer implementation, we will not be using this listView_Fragment to load the complete list of magic oldCards.
public class ListViewFragment
        extends Fragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemSelectedListener {
    //region VARIABLES
    Context context;
    public CardListAdapter adapter; //adapts given set of cards to list
    List<Card> cards;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    OnCardView mListener;
    OnCardView sListener;
    private Spinner sortSetSpinner;

    public ArrayAdapter<String> adapterforSetArray;
    ArrayAdapter<String> adapterforRarityArray;
   // public int spinnerposition;
    private AdapterView.OnItemSelectedListener listener;
    //endregion

    //"Constructor" for Fragments;
    public static ListViewFragment newInstance(List<Card> card){
        ListViewFragment f = new ListViewFragment();
        f.cards = card;
        return f;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.context = activity;
        if ((activity instanceof OnCardView))
        {
            this.mListener = ((OnCardView)activity);
            this.sListener = ((OnCardView)activity);
            return;
        }
        throw new ClassCastException(activity.toString() + " is lame!");
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View localView = paramLayoutInflater.inflate(R.layout.fragment_main, paramViewGroup, false);
        this.sortSetSpinner = (Spinner) localView.findViewById(R.id.filterSetSpinner);
        String[] cardSet_array = getResources().getStringArray(R.array.setNames);
        //cardSetCode_array = getResources().getStringArray(R.array.sets);
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

        Spinner sortRaritySpinner = (Spinner) localView.findViewById(R.id.sortRaritySpinner);
        String[] rarity_array = getResources().getStringArray(R.array.rarity);
        adapterforRarityArray = new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, rarity_array);
        sortRaritySpinner.setAdapter(adapterforRarityArray);
        sortRaritySpinner.setOnItemSelectedListener(this);

        this.recyclerView = ((RecyclerView)localView.findViewById(R.id.my_recyclerView));
        this.recyclerView.setHasFixedSize(true);
        this.mLayoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(mLayoutManager);
        this.adapter = new CardListAdapter(this.context, cards);
        //new CardListAdapter(this.context, R.layout.card_list_row, oldCards);

        this.recyclerView.setAdapter(this.adapter);
//        this.recyclerView.setOnItemClickListener(this);
//        this.recyclerView.setOnItemLongClickListener(this);
        return localView;
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int position, long paramLong)
    {
//        if(!(oldCards.get(0).getImageName().equals("Null"))){
//            String calledBy = "set";
//            this.mListener.onCardImageViewUpdate(position, calledBy);
//        }
        //this.mListener.showCardInfo(position);
    }

    public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        if(!(cards.get(0).getImageName().equals("Null"))){
            this.mListener.addCardToDeck(paramInt);
        Toast.makeText(this.context, "added ".concat(((Card)this.cards.get(paramInt)).getName()), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            this.sListener.spinnerItemSelected(position, parent.getId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    public void refresh(){        this.recyclerView;//.invalidateViews();}

    public static abstract interface OnCardView
    {
        public abstract void addCardToDeck(int position);
        public abstract void onCardImageViewUpdate(int paramInt, String calledBy);
        public abstract void spinnerItemSelected(int position, int id);
        public abstract int getSpinnerPosition();
        public abstract void showCardInfo(int position);
    }
}
