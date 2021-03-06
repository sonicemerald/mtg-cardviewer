package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
import micahgemmell.com.mtg_deck_l.MainActivity;
import micahgemmell.com.mtg_deck_l.R;

import java.util.List;

// The ListViewFragment is a fragment which holds a list. In the application, we used to load the full set of magic oldCards into it, as well as the deck.
// In this navigationDrawer implementation, we will not be using this listView_Fragment to load the complete list of magic oldCards.
public class ListViewFragment
        extends Fragment {
    //region VARIABLES
    Context context;
    public CardListAdapter adapter; //adapts given set of cards to list
    List<Card> cards;
    public ListView listView;
    SwipeRefreshLayout swipeLayout;
    CardListViewInterface mListener;
    CardListViewInterface sListener;
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
        if ((activity instanceof CardListViewInterface))
        {
            this.mListener = ((CardListViewInterface)activity);
            this.sListener = ((CardListViewInterface)activity);
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
        this.refresh();
        adapter.addAll(this.cards);
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View localView = paramLayoutInflater.inflate(R.layout.fragment_main, paramViewGroup, false);
/*
        swipeLayout = (SwipeRefreshLayout)localView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mListener.onSwipeRefresh();
                        swipeLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        swipeLayout.setColorSchemeColors(R.color.gold, R.color.green, R.color.blue, R.color.black, R.color.white, R.color.red);
        swipeLayout.setProgressViewOffset(true, getResources().getDimensionPixelSize(R.dimen.action_bar_height), getResources().getDimensionPixelSize(R.dimen.action_bar_auto_hide_min_y));
        swipeLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.holo_orange_light));
*/
        this.listView = (ListView)localView.findViewById(R.id.listView);
        this.adapter = new CardListAdapter(this.context, R.id.card_list_layout, cards);
        AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(adapter);
        alphaInAnimationAdapter.setAbsListView(this.listView);
        this.listView.setAdapter(alphaInAnimationAdapter);
        this.listView.setDivider(null);
        this.listView.setFastScrollEnabled(true);

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListener.onCardClicked(i);}});
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });
        ((MainActivity) getActivity()).enableActionBarAutoHide(listView);
        return localView;
    }
//
//    public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
//    {
//        if(!(cards.get(0).getImageName().equals("Null"))){
//            this.mListener.addCardToDeck(paramInt);
//        Toast.makeText(this.context, "added ".concat(((Card)this.cards.get(paramInt)).getName()), Toast.LENGTH_SHORT).show();
//        }
//        return true;
//    }

    public void refresh(){
        this.listView.invalidateViews();
    }

    public static abstract interface CardListViewInterface
    {
        public abstract void addCardToDeck(int position);
//        public abstract void onCardImageViewUpdate(int paramInt, String calledBy);
        public abstract void onCardClicked(int position);
        //public abstract void onSwipeRefresh();
    }
}
