    package micahgemmell.com.mtg_deck_l;

    import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
    import micahgemmell.com.mtg_deck_l.Card.*;
    import micahgemmell.com.mtg_deck_l.Fragments.CardImageFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.CardViewFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DeckFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DiceRollerFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ErrorDialogFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.LifeCounterFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ManaCalculatorFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.SpinnerFragment;
    import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
    import micahgemmell.com.mtg_deck_l.event.PleaseGetSetPriceEvent;
    import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
    import micahgemmell.com.mtg_deck_l.event.SetPricedEvent;
    import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
    import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;
    import micahgemmell.com.mtg_deck_l.helpers.CropTransform;
    import micahgemmell.com.mtg_deck_l.helpers.CropTransform_gatherer;
    import micahgemmell.com.mtg_deck_l.helpers.InstantAutoComplete;

    import android.app.AlertDialog;
    import android.app.Fragment;
    import android.app.FragmentTransaction;
    import android.app.ProgressDialog;
    import android.app.SearchManager;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.net.Uri;
    import android.os.Bundle;
    import android.support.design.widget.Snackbar;
    import android.support.v4.app.DialogFragment;
    import android.support.v4.view.GravityCompat;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v4.widget.DrawerLayout;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.support.v7.widget.SearchView;
    import android.util.Log;
    import android.view.Gravity;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.animation.DecelerateInterpolator;
    import android.widget.AbsListView;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ImageView;
    import android.widget.ListView;
    import android.widget.RelativeLayout;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.text.Normalizer;
    import java.util.*;

    import com.google.android.gms.common.server.converter.StringToIntConverter;
    import com.squareup.otto.Bus;
    import com.squareup.otto.Subscribe;
    import com.squareup.picasso.Picasso;

    import org.w3c.dom.Text;

    import static micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment.newInstance;

    public class MainActivity extends AppCompatActivity implements ListViewFragment.CardListViewInterface,
            DiceRollerFragment.OnDiceRoll,
            CardViewFragment.OnCardViewFragmentInteraction,
            SpinnerFragment.SpinnerInterface,
            ManaCalculatorFragment.ManaCalcInterface,
            CardImageFragment.OnCardImageClicked,
            LifeCounterFragment.OnLifeCounterInteraction {
        //region VARIABLES
        //general
        private Bus mBus;
        private SharedPreferences sharedPrefs;
        private ProgressDialog Dialog;
        private Boolean gettingPrices = false;
        public String mSet;
        private Toolbar mActionBarToolbar;
        String favImage;

        //Fragments
        DiceRollerFragment dice;
        ListViewFragment listView_f;
        SpinnerFragment spinners_f;
        DeckFragment deckView_f;
        CardImageFragment cardImageView_f;
        CardViewFragment cardView_f;
        ManaCalculatorFragment manaCalc_f;
        LifeCounterFragment lifeCount_f;

        //ListView container_listView;
        String listview_tag = "listviewFragment";

        //Lists
        private List<Card> masterCardList; //masterCardList keeps a copy of the original parsed list.
        List<Card> displayedCards;
        List<Card> sortedCards;
        List<Card> displayedTypes;
        List<Card> deck;
        List<Card> SearchResults;
        List<Card> temp; // used for rarity adapter
        List<Card> rare;
        List<Card> type;

        String[] cardSet_array; // Set Names
        String[] cardSetCode_array; // Set Codes (used in URL)

        //Adapters
        CardListAdapter adapter;
        ArrayAdapter<String> adapterforStringArray; // currently used for the set list
        private ArrayAdapter<String> adapterforStringArray1;

        //ListViewFragment container_listView;

        //Spinners
        //Spinner addSetSpinner; // dropdown list of magic card sets.
        public int spinnerPosition;
        public Spinner sortbySpinner;
//        public InstantAutoComplete mtgSetTextView; //future implementation of setSelection?

        //Navigation Drawer
        ListView NavigationDrawer_listView_Left; // used for the "navigation"
        ActionBarDrawerToggle mDrawerToggle;
        DrawerLayout mDrawerLayout;
        ImageView mDrawerImage;
        DrawerItemClickListener dListener;
        RelativeLayout mDrawerRelativeLeft;
        RelativeLayout mDrawerRelativeRight;
        CharSequence mDrawerTitle = "Menu";
        String[] navMenuItems;

        //Search Overlay

        // variables that control the Action Bar auto hide behavior (aka "quick recall")
        private boolean mActionBarAutoHideEnabled = false;
        private int mActionBarAutoHideSensivity = 0;
        private int mActionBarAutoHideMinY = 0;
        private int mActionBarAutoHideSignal = 0;
        private boolean mActionBarShown = true;

        // Durations for certain animations we use:
        private static final int HEADER_HIDE_ANIM_DURATION = 300;

        // When set, these components will be shown/hidden in sync with the action bar
        // to implement the "quick recall" effect (the Action Bar and the header views disappear
        // when you scroll down a list, and reappear quickly when you scroll up).
        private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

        // Constants for diceroller & lifeviewer
        TextView rollResult;
        TextView livesLeft;
        int lives = 20;

        //Random number generator
        long randomSeed = System.currentTimeMillis();
        Random generator = new Random(randomSeed);

        //Search
        private String query;
        private String cQuery;
        private String[] SortingItems;

        //Details Sorting
        //endregion

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(savedInstanceState != null) {

            }
                setContentView(R.layout.activity_main);
            //region Toolbar
            mActionBarToolbar = getActionBarToolbar();
            //endregion
            Dialog = new ProgressDialog(MainActivity.this);
            sharedPrefs = this.getSharedPreferences("micahgemmell.com.mtg_deck_l", Context.MODE_PRIVATE);
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);


            // region Arrays
            masterCardList = new ArrayList<Card>();
            displayedCards = new ArrayList<Card>();
            sortedCards = new ArrayList<Card>();
            deck = new ArrayList<Card>();
            SearchResults = new ArrayList<Card>();
            temp = new ArrayList<Card>();
            rare = new ArrayList<Card>();
            type = new ArrayList<Card>();
            //endregion

            //region Set up main container for the displayedCards.
            cardSetCode_array = getResources().getStringArray(R.array.sets);
            List<String> list = Arrays.asList(cardSetCode_array);
            //next, reverse the list using Collections.reverse method
            Collections.reverse(list);
            //next, convert the list back to String array
            cardSetCode_array = (String[]) list.toArray();

            adapter = new CardListAdapter(this, R.id.card_list_layout, displayedCards);
            listView_f = newInstance(displayedCards);
            spinners_f = SpinnerFragment.newInstance();

            if (savedInstanceState == null){
                getFragmentManager().beginTransaction()
                        .add(R.id.spinnerContainer, spinners_f)
                        .add(R.id.listviewContainer, listView_f, listview_tag)
                        .commit();
            }
            sortbySpinner = (Spinner) findViewById(R.id.sortBy_spinner);
            //spinnerAdapter = spinners_f.adapterforSortDetailArray;
//            spinnerAdapter.add("");
//            sortbyDetailSpinner.setEnabled(false);
//            sortbyDetailSpinner.setClickable(false);
//            sortbyDetailSpinner.setAdapter(spinnerAdapter);

            // endregion set up main container for the displayedCards
            //region NavDrawer
            dListener = new DrawerItemClickListener();
            //also need to spin up a listView for navDrawer. Left SIDE
           NavigationDrawer_listView_Left = (ListView) findViewById(R.id.left_drawer); //Find where we want to put the list
           navMenuItems = getResources().getStringArray(R.array.nav_drawer_items); // Get the Array of items.
           adapterforStringArray1 = new ArrayAdapter<String>(this, R.layout.drawer_list_item, navMenuItems); // need to adapt the array of items
           //Now set the adapter.
           NavigationDrawer_listView_Left.setAdapter(adapterforStringArray1);
           NavigationDrawer_listView_Left.setOnItemClickListener(dListener);
           //NavigationDrawer_listView_Left.setOnItemLongClickListener(new DrawerItemLongClickListener());

           //Right Side Setup, not used anymore
//           NavigationDrawer_listView_Right = (ListView) findViewById(R.id.right_drawer); //Find where we want to put the list
//           SortingItems = getResources().getStringArray(R.array.nav_drawer_sorting_items); // Get the Array of items.
//           adapterforStringArray = new ArrayAdapter<String>(this, R.layout.drawer_list_item, SortingItems); // need to adapt the array of items
//           //Now set the adapter.
//           NavigationDrawer_listView_Right.setAdapter(adapterforStringArray);
//           NavigationDrawer_listView_Right.setOnItemCli,ckListener(dListener);

           //setting up for open close drawer
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerRelativeLeft = (RelativeLayout) findViewById(R.id.drawer_layout_container_left);
           //mDrawerRelativeRight = (RelativeLayout) findViewById(R.id.drawer_layout_container_right);

           mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar, R.string.drawer_open, R.string.drawer_close){
              public void onDrawerClosed(View view) {
                  getSupportActionBar().setTitle(R.string.app_name);
              }
                   //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
              public void onDrawerOpened(View drawerView) {
                   getSupportActionBar().setTitle(mDrawerTitle);
                   //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
               }
           };
           mDrawerToggle.setDrawerIndicatorEnabled(true);
           mDrawerLayout.setDrawerListener(mDrawerToggle);

           //set up drawer image
           mDrawerImage = (ImageView) findViewById(R.id.drawerImageView);
           favImage = sharedPrefs.getString("favImage", favImage);
            if(favImage == null){
                favImage = "https://upload.wikimedia.org/wikipedia/en/a/aa/Magic_the_gathering-card_back.jpg";
            }
            if(favImage.startsWith("http://gatherer")) {
                Picasso.with(this).load(favImage).placeholder(R.color.black).into(mDrawerImage);
                Picasso.with(this).load(favImage).transform(new CropTransform_gatherer()).into(mDrawerImage);
            } else {
                Picasso.with(this).load(favImage).into(mDrawerImage);
                Picasso.with(this).load(favImage).transform(new CropTransform()).into(mDrawerImage);
            }
            mDrawerImage.setClickable(true);
           //endregion
            //region searchOverlay
            //endregion
        }

        //region actionbar
        protected void setActionBarIcon(int iconRes) {
            mActionBarToolbar.setNavigationIcon(iconRes);
        }

        /**
         * Initializes the Action Bar auto-hide (aka Quick Recall) effect.
         */
        private void initActionBarAutoHide() {
            mActionBarAutoHideEnabled = true;
            mActionBarAutoHideMinY = getResources().getDimensionPixelSize(
                    R.dimen.action_bar_auto_hide_min_y);
            mActionBarAutoHideSensivity = getResources().getDimensionPixelSize(
                    R.dimen.action_bar_auto_hide_sensivity);
        }

        /**
         * Indicates that the main content has scrolled (for the purposes of showing/hiding
         * the action bar for the "action bar auto hide" effect). currentY and deltaY may be exact
         * (if the underlying view supports it) or may be approximate indications:
         * deltaY may be INT_MAX to mean "scrolled forward indeterminately" and INT_MIN to mean
         * "scrolled backward indeterminately".  currentY may be 0 to mean "somewhere close to the
         * start of the list" and INT_MAX to mean "we don't know, but not at the start of the list"
         */
        private void onMainContentScrolled(int currentY, int deltaY) {
            if (deltaY > mActionBarAutoHideSensivity) {
                deltaY = mActionBarAutoHideSensivity;
            } else if (deltaY < -mActionBarAutoHideSensivity) {
                deltaY = -mActionBarAutoHideSensivity;
            }

            if (Math.signum(deltaY) * Math.signum(mActionBarAutoHideSignal) < 0) {
                // deltaY is a motion opposite to the accumulated signal, so reset signal
                mActionBarAutoHideSignal = deltaY;
            } else {
                // add to accumulated signal
                mActionBarAutoHideSignal += deltaY;
            }

            boolean shouldShow = currentY < mActionBarAutoHideMinY ||
                    (mActionBarAutoHideSignal <= -mActionBarAutoHideSensivity);
            autoShowOrHideActionBar(shouldShow);
        }

        protected Toolbar getActionBarToolbar() {
            if (mActionBarToolbar == null) {
                mActionBarToolbar = (Toolbar) findViewById(R.id.toolbarinclude);
                if (mActionBarToolbar != null) {
                    setSupportActionBar(mActionBarToolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
                }
            }
            return mActionBarToolbar;
        }

        protected void autoShowOrHideActionBar(boolean show) {
            if (show == mActionBarShown) {
                return;
            }

            mActionBarShown = show;
            onActionBarAutoShowOrHide(show);
        }

        public void enableActionBarAutoHide(final ListView listView) {
            initActionBarAutoHide();
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                final static int ITEMS_THRESHOLD = 1;
                int lastFvi = 0;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    onMainContentScrolled(firstVisibleItem <= ITEMS_THRESHOLD ? 0 : Integer.MAX_VALUE,
                            lastFvi - firstVisibleItem > 0 ? Integer.MIN_VALUE :
                                    lastFvi == firstVisibleItem ? 0 : Integer.MAX_VALUE
                    );
                    lastFvi = firstVisibleItem;
                }
            });
        }

        protected void onActionBarAutoShowOrHide(boolean shown) {

            mHideableHeaderViews.add(findViewById(R.id.toolbarinclude));
            mHideableHeaderViews.add(findViewById(R.id.spinnerContainer));

            for (View view : mHideableHeaderViews) {
                if (shown) {
                    view.animate()
                            .translationY(0)
                            .alpha(1)
                            .setDuration(HEADER_HIDE_ANIM_DURATION)
                            .setInterpolator(new DecelerateInterpolator());
                } else {
                    view.animate()
                            .translationY(-view.getBottom())
                            .alpha(0)
                            .setDuration(HEADER_HIDE_ANIM_DURATION)
                            .setInterpolator(new DecelerateInterpolator());
                }
            }
        }
        //endregion

        @Subscribe
        public void onCardsLoaded(CardsParsedEvent event) {
            masterCardList.clear();
            displayedCards.clear();
            sortedCards.clear(); // clear the sorted list of cards
            masterCardList.addAll(event.getParsedCards());
            displayedCards.addAll(masterCardList);
            SearchResults.clear();
            listView_f.adapter.addAll(displayedCards);
            listView_f.adapter.indexcardsAlphabetically();
            if(mSet.contains("Duel Decks Anthology,"))
                mSet = "Duel Decks: Anthology";
            if(mSet.contains("vs."))
                    mSet = "Duel Decks: ".concat(mSet);
            getBus().post(new PleaseGetSetPriceEvent(mSet));
            gettingPrices = true;
            Dialog.dismiss();
            listView_f.refresh();
        }

        @Subscribe
        public void onCardsPriced(SetPricedEvent event){
            List<CardPrice> PricesArray = event.getPricesArray();
            int cardsnotpriced = 0;
            try {
                if (PricesArray.size() > 1) {
                    int i = 0;
                    for (Card a : masterCardList) {
                        String name = a.getName();

                        if(name.charAt(0) == 'Æ'){
                            name = "AE".concat(name.substring(1, name.length())); }
                        name = Normalizer.normalize(name, Normalizer.Form.NFD)
                                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                        //region doublefaced
                        if(a.getNames().size()==2){ // double faced cards.
                            if(a.getNames().contains(PricesArray.get(i).getName())) {
                                //catches new planeswalkers, innistrad block
                                a.setHighPrice(PricesArray.get(i).getHigh());
                                a.setMedPrice(PricesArray.get(i).getMed());
                                a.setLowPrice(PricesArray.get(i).getLow());
                                a.setPriceHidden(false);
                                i++;
                                continue;
                            } else if (PricesArray.get(i).getName().contains(name)){
                                // for Dragon's Maze (Alive // Well)
                                a.setHighPrice(PricesArray.get(i).getHigh());
                                a.setMedPrice(PricesArray.get(i).getMed());
                                a.setLowPrice(PricesArray.get(i).getLow());
                                a.setPriceHidden(false);
                                i++;
                                continue;
                            } else {
                                Log.d("", name + "has two names, and does not match" + PricesArray.get(i).getName());
                                a.setMedPrice("$0.0");
                                a.setPriceHidden(false);
                                continue;
                            }

                        }//endregion

                        if (name.toLowerCase().equals(PricesArray.get(i).getName().toLowerCase())) {
                            a.setHighPrice(PricesArray.get(i).getHigh());
                            a.setMedPrice(PricesArray.get(i).getMed());
                            a.setLowPrice(PricesArray.get(i).getLow());
                            a.setPriceHidden(false);
                            i++;
                        } else if(a.getName().toLowerCase().substring(0,2).equals(PricesArray.get(i).getName().toLowerCase().substring(0, 2))) {
                            a.setHighPrice(PricesArray.get(i).getHigh());
                            a.setMedPrice(PricesArray.get(i).getMed());
                            a.setLowPrice(PricesArray.get(i).getLow());
                            a.setPriceHidden(false);
                            i++;
//                        } else if(a.getName().toLowerCase().substring(a.getName().length()-3, a.getName().length())
//                                .equals(PricesArray.get(i).getName().toLowerCase().substring(PricesArray.get(i).getName().length()
//                                        - 3, PricesArray.get(i).getName().length())))
//                        {
//                            a.setHighPrice(PricesArray.get(i).getHigh());
//                            a.setMedPrice(PricesArray.get(i).getMed());
//                            a.setLowPrice(PricesArray.get(i).getLow());
//                            a.setPriceHidden(false);
//                            i++;
                        } else if (!name.toLowerCase().equals(PricesArray.get(i).getName().toLowerCase())){
                            Log.d("A: ", name + " is trying to be matched with P: " + PricesArray.get(i).getName());
                            cardsnotpriced++;
                        }
                    }
                    listView_f.adapter.notifyDataSetChanged();
                    gettingPrices = false;

                    if(cardsnotpriced > 10){
                        Snackbar.make(mDrawerLayout, "Well, this is embarrassing...", Snackbar.LENGTH_LONG).setAction("Report", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // ACTION_SENDTO filters for email apps (discard bluetooth and others)
                                String uriText =
                                        "mailto:sonicemerald@gmail.com" +
                                                "?subject=" + Uri.encode("MTG Library Price Error") +
                                                "&body=" + Uri.encode( "There's probably an error here. Look into the set " + mSet);

                                Uri uri = Uri.parse(uriText);
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(uri);

                                startActivity(Intent.createChooser(intent, "Email the developer..."));
                            }
                        }).show();
                    } else {
                    Snackbar.make(mDrawerLayout, "I found all the prices for you.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            } catch (RuntimeException e) {
                Log.d("priceError","can't parse " + mSet);
                if(mSet.equals("Innistrad") || mSet.equals("Dragon's Maze")){
                    Toast.makeText(this, "Sorry, this sets price has trouble, I'm working on it", Toast.LENGTH_SHORT).show();
                }

                Snackbar.make(mDrawerLayout, "Well, this is embarrassing...", Snackbar.LENGTH_LONG).setAction("Report", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ACTION_SENDTO filters for email apps (discard bluetooth and others)
                        String uriText =
                                "mailto:sonicemerald@gmail.com" +
                                        "?subject=" + Uri.encode("MTG Library Price Error") +
                                        "&body=" + Uri.encode( "There's an error here. Look into the set " + mSet + "please.");

                        Uri uri = Uri.parse(uriText);
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(uri);

                        startActivity(Intent.createChooser(intent, "Email the developer..."));
                    }
                }).show();

            }
        }

        //region LIFECYCLE
        @Override
        protected void onResume(){
            super.onResume();
            //this gets called?
            Log.d("", "MainActivity onResume called, " + displayedCards.toString());
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
            favImage = sharedPrefs.getString("favImage", favImage);
            getBus().register(this);
        }

        @Override
        protected void onPause() {
            Log.d("", "MainActivity onPause");
            sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
            sharedPrefs.edit().putString("favImage", favImage).apply();
            getBus().unregister(this);
            super.onPause();
        }

        @Override
        protected void onStop(){
            super.onStop();
            Log.d("", "is this the crash?");
        }

        @Override
        protected void onRestart() {
            super.onRestart();  // Always call the superclass method first
            Log.d("s", "maybe this is crash.");
            // Activity being restarted from stopped state
        }


        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
            Log.d("saved", "saved");//.concat(String.valueOf(spinnerPosition)));
            // Save UI state changes to the savedInstanceState.
            // This bundle will be passed to onCreate if the process is
            // killed and restarted.
            sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
        }

        @Override
        public void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
            // Restore UI state from the savedInstanceState.
            // This bundle has also been passed to onCreate.
            spinnerPosition = sharedPrefs.getInt("spinnerPos", spinnerPosition);
            Log.d("restore", "restored");
        }

        @Override
        public void onBackPressed() {
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
                mDrawerLayout.closeDrawers();
            else if(getFragmentManager().getBackStackEntryCount() > 0)
                getFragmentManager().popBackStack();
            else
                super.onBackPressed();
        }
        //endregion

        public void performSearch(String query){

            Log.d("enteredSearch", "hi");
            SearchResults.clear();

            cQuery = "";
            cQuery = query;

            if (cQuery.length()>1)
                cQuery = cQuery.substring(0,1).toUpperCase() + cQuery.substring(1);
            else
                cQuery = cQuery.toUpperCase();

            for (Card card : displayedCards) {
                if (card.getName().toLowerCase().contains(query) || card.getColors().contains(cQuery)
                || card.getTypes().contains(cQuery) || card.getSubtypes().contains(cQuery))
                {
                    SearchResults.add(card);
                }
            }
            if(SearchResults.size() == 0){
                listView_f.adapter.clear();
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Sorry, no search results were found in the current set.")
                        .setTitle("No Results Found");

                // Add the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            Log.d("searchResults " +SearchResults.size(), SearchResults.toString());
            Snackbar.make(mDrawerLayout, "I found " + SearchResults.size() + " results.", Snackbar.LENGTH_LONG).show();


            listView_f.adapter.clear();
            displayedCards.clear();
            displayedCards.addAll(SearchResults);
            //listView_f.adapter.addAll(displayedCards);
            listView_f.adapter.notifyDataSetChanged();
//            listView_f.refresh();
         }

        //region SPINNERS
        @Override
        public void spinnerItemSelected(int position, int id) {
            switch(id){
                case R.id.filterSetSpinner: //sets
                    //region filter
                    spinnerPosition = position;
                    sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
                    mSet = spinners_f.adapterforSetArray.getItem(spinnerPosition);

                    if(mSet.equals("Select a Set"))
                        break;
                    displayedCards.clear(); //clear the activities currently displayed displayedCards
                    listView_f.adapter.clear(); //clear the adapters list.
                    masterCardList.clear(); //clear master list of displayedCards
                    getBus().post(new PleaseParseCardsEvent(cardSetCode_array[position]));
                    Dialog.setMessage("loading " + spinners_f.adapterforSetArray.getItem(spinnerPosition) + " cards.");
//                    Dialog.setCanceledOnTouchOutside(false);
                    Dialog.setCancelable(false);
                    Dialog.show();
                    spinners_f.adapterforSortDetailArray.clear();
                    spinners_f.adapterforSortDetailArray.add("");
                    spinners_f.sortbyDetailSpinner.setEnabled(false);
                    spinners_f.sortbyDetailSpinner.setClickable(false);
                    spinners_f.sortbySpinner.setSelection(0);
                    spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                    break;
                //endregion
                case R.id.sortBy_spinner:
                    //region sorting
                    switch (position){
                        case 0:
                            //show list as normal.
                            listView_f.listView.setFastScrollEnabled(true);
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("");
                            setUpSortDetailSpinner(false);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            break;
                        case 1: //"A-Z
                            listView_f.adapter.clear();
                            displayedCards.clear();
                            displayedCards.addAll(masterCardList);
                            listView_f.adapter.addAll(displayedCards);
                            listView_f.adapter.indexcardsAlphabetically();
                            listView_f.listView.setFastScrollEnabled(true);
                            addSortedCardstoList();
                            //populate detail spinner and show it
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            spinners_f.adapterforSortDetailArray.addAll(listView_f.adapter.sectionList);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            setUpSortDetailSpinner(true);
                            break;
                        case 2: //"Set #"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            sortCardsBySetNumber();
                            listView_f.adapter.addAll(displayedCards);
                            //hide detail spinner
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            setUpSortDetailSpinner(false);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            break;
                        case 3: //"Card Type"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            sortCardsByCardType();
                            listView_f.adapter.addAll(displayedCards);
                            listView_f.adapter.indexCardsByType();
                            listView_f.listView.setFastScrollEnabled(true);
                            addSortedCardstoList();
                            //populate detail spinner and show
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            spinners_f.adapterforSortDetailArray.addAll(listView_f.adapter.sectionList);
                            setUpSortDetailSpinner(true);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            break;
                        case 4: //"Color"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            sortCardsByColor();
                            listView_f.adapter.addAll(displayedCards);
                            listView_f.adapter.indexCardsByColor();
                            listView_f.listView.setFastScrollEnabled(true);
                            addSortedCardstoList();
                            //populate detail spinner and show it);
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            spinners_f.adapterforSortDetailArray.addAll(listView_f.adapter.sectionList);
                            setUpSortDetailSpinner(true);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            break;
                        case 5: //"Mana Cost"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            sortCardsByCMC();
                            listView_f.adapter.addAll(displayedCards);
                            listView_f.adapter.indexCardsByManaCost();
                            listView_f.listView.setFastScrollEnabled(true);
                            //populate detail spinner and show it
                            addSortedCardstoList();
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            spinners_f.adapterforSortDetailArray.addAll(listView_f.adapter.sectionList);
                            setUpSortDetailSpinner(true);
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            break;
                        case 6: //"Rarity"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            sortCardsByRarity();
                            listView_f.adapter.addAll(displayedCards);
                            listView_f.adapter.indexCardsByRarity();
                            listView_f.listView.setFastScrollEnabled(true);
                            addSortedCardstoList();
                            //populate detail spinner and show it
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("All");
                            spinners_f.adapterforSortDetailArray.addAll(listView_f.adapter.cardIndex.keySet());
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            setUpSortDetailSpinner(true);
                            break;
                        case 7: //"Price"
                            listView_f.adapter.clear();
                            if(displayedCards.size() != masterCardList.size()) {
                                displayedCards.clear();
                                displayedCards.addAll(masterCardList);
                            }
                            listView_f.adapter.addAll(displayedCards);
                            //hide detail spinner
                            spinners_f.adapterforSortDetailArray.clear();
                            spinners_f.adapterforSortDetailArray.add("");
                            spinners_f.adapterforSortDetailArray.notifyDataSetChanged();
                            setUpSortDetailSpinner(false);
                            if(gettingPrices){
                                Snackbar.make(mDrawerLayout, "I don't have all the prices yet...", Snackbar.LENGTH_SHORT).show();
                            } else {
                                sortCardsByPrice(); }
                            break;
                        default:
                            break;

                    }
                    //endregion
                case R.id.detailspinner:
                    //this will dynamically display cards that fit the selected detail.
                    int spinnerPosition = spinners_f.sortbyDetailSpinner.getSelectedItemPosition();
                    String selected = (String) spinners_f.sortbyDetailSpinner.getSelectedItem();
                    if(spinnerPosition != 0) { // didn't select further sorting options.
                        if(sortedCards.isEmpty())
                            addSortedCardstoList();
                        int sec = spinners_f.sortbyDetailSpinner.getSelectedItemPosition();
                        int endsec = spinners_f.sortbyDetailSpinner.getAdapter().getCount();
                        int endx = 0;
                        if(sec == endsec-1){
                            endx = sortedCards.size();
                        } else {
                            endx = listView_f.adapter.getPositionForSection(sec);
                        }
                        int startx = listView_f.adapter.cardIndex.get(selected);
                        temp.clear();
                        temp.addAll(sortedCards.subList(startx, endx));
                        displayedCards.clear();
                        listView_f.adapter.clear();
                        displayedCards.addAll(temp);
                        listView_f.adapter.addAll(displayedCards);
                        listView_f.listView.setFastScrollEnabled(false);
                        listView_f.adapter.notifyDataSetChanged();
                    }
                    else if(displayedCards.size() != masterCardList.size()){
                        displayedCards.clear();
                        displayedCards.addAll(sortedCards);
                        listView_f.adapter.clear();
                        listView_f.adapter.addAll(displayedCards);
                        listView_f.listView.setFastScrollEnabled(true);
                        listView_f.adapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.deckTypeSpinner:
                    TextView t = (TextView) findViewById(R.id.landsindeck);
                    switch(position){
                        case 0: // Limited - 40 cards, default is 17 lands
                            t.setText("17");
                            break;
                        case 1: // Standard - 60 cards, default is 24 lands
                            t.setText("24");
                            break;
                        case 2: // EDH - 100 cards, default is around 40 lands
                            t.setText("40");
                            break;
                        default:
                            break;
                    }
                    break;
                default: break;
            }
        }
//        private void setRarityAdapter(String rarity){
//            temp.clear();
//            rare.clear();
//            //displayedCards.clear();
//            listView_f.adapter.clear();
//
//            if(!(SearchResults.isEmpty())){
//                temp.addAll(SearchResults);
//            } else {
//                temp.addAll(displayedTypes);
//            }
//            for (Card card : temp) {
//                if (card.getRarity().equals(rarity)) {
//                    rare.add(card);
//                }
//            }
//            displayedCards.clear();
//            displayedCards.addAll(rare);
//            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
//            listView_f.adapter.addAll(displayedCards);
//            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
//        }
        @Override
        public int getSpinnerPosition() {
            sharedPrefs = this.getSharedPreferences("micahgemmell.com.mtg_deck_l", Context.MODE_PRIVATE);
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
            return spinnerPosition; }
        //region Sorting Methods
        void sortCardsByAZ(){
            //master list has all of them in a-z order.
            this.displayedCards.clear();
            this.displayedCards.addAll(masterCardList);
            if(sortedCards.size() != masterCardList.size())
                sortedCards.addAll(displayedCards);
            listView_f.adapter.notifyDataSetChanged();
            refreshFragment();
        }
        void sortCardsBySetNumber(){
            //because Set Numbers can have letters in them, I need to check for that.
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card a, Card b) {
                    try{
                        if (Integer.parseInt(a.getNumber()) > Integer.parseInt(b.getNumber()))
                            return 1;
                        else if (Integer.parseInt(a.getNumber()) < Integer.parseInt(b.getNumber()))
                            return -1;
                        else // equal
                            return 0;
                    } catch (Exception e){
                        //if no number, then use multiverse ID
                        if (a.getMultiverseid() > b.getMultiverseid())
                            return 1;
                        else if (a.getMultiverseid() < b.getMultiverseid())
                            return -1;
                        else // equal
                            return 0;
                    }
                }
            });
            listView_f.adapter.notifyDataSetChanged();
            refreshFragment();
            Snackbar.make(mDrawerLayout, "I sorted the set by the set number.", Snackbar.LENGTH_SHORT).show();

        }
        void sortCardsByCardType(){
            Log.d("sorting", "sorting by type");
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card a, Card b) {
                    int c1 = convertTypeToInt(a.getTypes().get(0));
                    int c2 = convertTypeToInt(b.getTypes().get(0));

                    if (c1 > c2)
                        return 1;
                    else if (c1 < c2)
                        return -1;
                    else
                        return 0;
                }
            });
            Snackbar.make(mDrawerLayout, "I sorted the cards by type for you.", Snackbar.LENGTH_SHORT).show();


        }
        void sortCardsByColor(){
            Log.d("sorting", "sorting by Color");
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card a, Card b) {

                    int c1 = 0;
                    int c2 = 0;

                    if (a.getColors().size() > 1) {
                        c1 = 5; //"gold"
                    } else if (a.getColors().size() == 1) {
                        c1 = convertColorToInt(a.getColors().get(0).substring(0,3));
                    } else {
                        c1 = 6; //"Other"
                    }

                    if (b.getColors().size() > 1) {
                        c2 = 5; //"gold"
                    } else if (b.getColors().size() == 1) {
                        c2 = convertColorToInt(b.getColors().get(0).substring(0,3));
                    } else {
                        c2 = 6; //"Other"
                    }


                    if (c1 > c2)
                        return 1;
                    else if (c1 < c2)
                        return -1;
                    else
                        return 0;
                }
            });
            Snackbar.make(mDrawerLayout, "I sorted all the cards by color", Snackbar.LENGTH_SHORT).show();

        }
        void sortCardsByCMC(){
            Log.d("sorting", "sorting by CMC");
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card a, Card b) {
                    Log.d("sorting", "comparing CMC of " + a.getName() + " and " + b.getName());
                    //if the card is a land, it has no CMC.
                    if(a.getTypes().contains("Land")){
                        a.setCmc(999);
                    }
                    if(b.getTypes().contains("Land")){
                        b.setCmc(999);
                    }

                    if (a.getCmc() > b.getCmc())
                        return 1;
                    else if (a.getCmc() < b.getCmc())
                        return -1;
                    else // equal
                        return 0;
                }
            });
            Snackbar.make(mDrawerLayout, "I sorted the cards by their cmc.", Snackbar.LENGTH_SHORT).show();

        }
        void sortCardsByRarity(){
            Log.d("sorting", "sorting by Rarity");
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card a, Card b) {
                    int c1 = convertRarityToInt(a.getRarity().substring(0, 1));
                    int c2 = convertRarityToInt(b.getRarity().substring(0, 1));

                    if (c1 > c2)
                        return -1;
                    else if (c1 < c2)
                        return 1;
                    else
                        return 0;
                }
            });
            Snackbar.make(mDrawerLayout, "I sorted the cards by their rarity.", Snackbar.LENGTH_SHORT).show();

        }
        void sortCardsByPrice(){
            // low to high
            Collections.sort(this.displayedCards, new Comparator<Card>() {
                @Override
                public int compare(Card cardone, Card cardtwo) {
                    String one = cardone.getMedPrice().replace(",","");
                    String two = cardtwo.getMedPrice().replace(",","");
                    if (Double.parseDouble(one.substring(1)) > Double.parseDouble(two.substring(1))) {
                        Log.d(one + " > " + two, "sorting");
                        return -1;
                    } else if (Double.parseDouble(one.substring(1)) < Double.parseDouble(two.substring(1))) {
                        Log.d(one + " < " + two, "sorting");
                        return 1;
                    } else // equal
                        Log.d(one + " = " + two, "sorting");
                    return 0;
                }
            });
            listView_f.adapter.notifyDataSetChanged();
            refreshFragment();
            Snackbar.make(mDrawerLayout, "I sorted the cards by their price.", Snackbar.LENGTH_SHORT).show();
        }
        //helper methods
        int convertTypeToInt(String type){
            switch(type){
                case "Planeswalker":
                    return 0;
                case "Creature":
                    return 1;
                case "Artifact":
                    return 2;
                case "Tribal":
                    return 3;
                case "Enchantment":
                    return 4;
                case "Instant":
                    return 5;
                case "Sorcery":
                    return 6;
                case "Land":
                    return 7;
                default:
                    break;
            }
            return 0;
        }
        int convertColorToInt(String color){
            switch (color){
                case "Whi":
                    return 0;
                case "Gre":
                    return 1;
                case "Blu":
                    return 2;
                case "Red":
                    return 3;
                case "Bla":
                    return 4;
                case "gol":
                    return 5;
                default:
                    break;
            }
            return 0;
        }
        int convertRarityToInt(String firstLetter){
            switch (firstLetter){
                case "S":
                    return 0;
                case "B":
                    return 1;
                case "C":
                    return 2;
                case "U":
                    return 3;
                case "R":
                    return 4;
                case "M":
                    return 5;
                default:
                    break;
            }
            return 0;
        }
        void addSortedCardstoList(){
            if(displayedCards.size() == masterCardList.size())
                if(!sortedCards.isEmpty())
                    sortedCards.clear();
                sortedCards.addAll(displayedCards);
        }
        void setUpSortDetailSpinner(boolean b){
            spinners_f.sortbyDetailSpinner.setSelection(0, true);
            spinners_f.sortbyDetailSpinner.setEnabled(b);
            spinners_f.sortbyDetailSpinner.setClickable(b);
        }
        //endregion
        //endregion

        @Override
        public void addCardToDeck(int position){
            Card card = displayedCards.get(position);
            deck.add(card);
            Log.d("Card", "added ".concat(card.getName()));
            Toast.makeText(this, "added ".concat(displayedCards.get(position).getName()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCardClicked(int position) {
            //if card clicked isn't an error
            if(!displayedCards.get(position).getImageName().equals("Null")){

                // When card is clicked, display full page
                cardView_f = CardViewFragment.newInstance(displayedCards.get(position));

                //Show the action bar on the next page.
                    mActionBarToolbar.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());

                getFragmentManager().beginTransaction()
                        .detach(spinners_f).detach(listView_f)
                        .replace(R.id.listviewContainer, cardView_f)
                        .addToBackStack("mainlist")
                        .commit();
            }
        }

/*      @Override
        public void onSwipeRefresh() {
            //refresh the prices.
            if(gettingPrices){
                Toast.makeText(this, "Hold your horses, I'm getting prices", Toast.LENGTH_LONG).show();
                return;
            }
            getBus().post(new PleaseGetSetPriceEvent(mSet));
            gettingPrices = true;
        }*/

        @Override
        public void onCardImageClicked(){
            //when card image clicked, return.
            getFragmentManager().popBackStack();
        }

        //region CARD IMAGES
        //Triggers a user clicks the card image in the CardViewFragment.
        @Override
        public void onImageClicked(String image) {
            cardImageView_f = CardImageFragment.newInstance(image);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, cardImageView_f)
                    .addToBackStack("back to cardview")
                    .commit();
        }

        //When cardImage is long-pressed, set the image as the favorite image
        @Override
        public void onImageLongClicked(String image){
            favImage = image;
            Snackbar.make(mDrawerLayout, "I replaced your favorite image for you.", Snackbar.LENGTH_LONG).
                    setAction("SHOW", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //open navigation drawer
                                    mDrawerLayout.openDrawer(Gravity.LEFT);
                                }
                            }).show();
            Picasso.with(this).load(favImage).into(mDrawerImage);
            Picasso.with(this).load(favImage).transform(new CropTransform_gatherer()).into(mDrawerImage);
        }
        //endregion

        @Override
        public void onTransform(String card){
                for (Card a : displayedCards) {
                    if (a.getName().contains(card)) {
                        cardView_f = CardViewFragment.newInstance(a);
                        getFragmentManager().popBackStack("mainlist", 0);
                        getFragmentManager().beginTransaction()
                                .addToBackStack("transform")
                                .replace(R.id.listviewContainer, cardView_f)
                                .commit();
                    }
                }
        }

        //@Override
        public void calculateMana(View view){
            //need to get the mana of all 5 boxes.
            int b,u,g,r,w,total,lands = 0;

            TextView mb = (TextView)findViewById(R.id.mana_black);
            if(mb.getText().length() == 0){
                mb.setText("0");
            }
            b = Integer.parseInt(mb.getText().toString());

            TextView mu = (TextView)findViewById(R.id.mana_blue);
            if(mu.getText().length() == 0){
                mu.setText("0");
            }
            u = Integer.parseInt(mu.getText().toString());

            TextView mr = (TextView)findViewById(R.id.mana_red);
            if(mr.getText().length() == 0){
                mr.setText("0");
            }
            r = Integer.parseInt(mr.getText().toString());

            TextView mg = (TextView)findViewById(R.id.mana_green);
            if(mg.getText().length() == 0){
                mg.setText("0");
            }
            g = Integer.parseInt(mg.getText().toString());

            TextView mw = (TextView)findViewById(R.id.mana_white);
            if(mw.getText().length() == 0){
                mw.setText("0");
            }
            w = Integer.parseInt(mw.getText().toString());

            total = (b+u+r+g+w);
            Log.d("total symbols = ", Integer.toString(total));

            TextView l = (TextView)findViewById(R.id.landsindeck);
            lands = Integer.parseInt(l.getText().toString());
            Log.d("lands in deck", Integer.toString(lands));

            int greenLands = Math.round(g*lands/total);
            int redLands = Math.round(r*lands/total);
            int blueLands = Math.round(u*lands/total);
            int blackLands = Math.round(b*lands/total);
            int whiteLands = Math.round(w*lands/total);

            TextView GL = (TextView)findViewById(R.id.greenLand);
            GL.setText(Integer.toString(greenLands));

            TextView RL = (TextView)findViewById(R.id.redLands);
            RL.setText(Integer.toString(redLands));

            TextView UL = (TextView)findViewById(R.id.blueLands);
            UL.setText(Integer.toString(blueLands));

            TextView BL = (TextView)findViewById(R.id.blackLands);
            BL.setText(Integer.toString(blackLands));

            TextView WL = (TextView)findViewById(R.id.whiteLands);
            WL.setText(Integer.toString(whiteLands));

            TextView TS = (TextView)findViewById(R.id.totalSymbols);
            TS.setText(Integer.toString(total));
        }


        //region Dice Roller
        @Override
        public void diceRoller(int button) {
            switch(button)
            {
                case 10:
                    int rand = generator.nextInt(9);
                    rollResult= (TextView) findViewById(R.id.rollResult);
                    rollResult.setText(String.valueOf(rand+1));
                    break;
                case 12:
                    rand = generator.nextInt(11);
                    rollResult= (TextView) findViewById(R.id.rollResult);
                    rollResult.setText(String.valueOf(rand+1));
                    break;
                case 20:
                    rand = generator.nextInt(19);
                    rollResult= (TextView) findViewById(R.id.rollResult);
                    rollResult.setText(String.valueOf(rand+1));
                    break;
                case 50: //Lost Life Button
                    livesLeft = (TextView) findViewById(R.id.livesLeft);
                    lives--;
                    livesLeft.setText("Life Total: " + String.valueOf(lives));
                    break;
                case 60: //Add Life Button
                    livesLeft = (TextView) findViewById(R.id.livesLeft);
                    lives++;
                    livesLeft.setText("Life Total: " + String.valueOf(lives));
                    break;
            }

        }

        @Override
        public void diceRollerInit() {
            livesLeft = (TextView) findViewById(R.id.livesLeft);
            livesLeft.setText("Life Total: " + String.valueOf(lives));
        }

        @Override
        public void player1life(String changelife) {
            TextView p1life = (TextView) findViewById(R.id.player1_life);
            int P1life = Integer.valueOf(p1life.getText().toString());

            switch(changelife) {
                case "+1":
                    P1life++;
                    break;
                case "-1":
                    P1life--;
                    break;
                case "+5":
                    P1life = P1life + 4;
                    break;
                case "-5":
                    P1life = P1life - 4;
                    break;
                default:
                    break;
            }
            p1life.setText(Integer.toString(P1life));
        }

        @Override
        public void player2life(String changelife) {
            TextView p2life = (TextView) findViewById(R.id.player2_life);
            int P2life = Integer.valueOf(p2life.getText().toString());
            switch(changelife) {
                case "+1":
                    P2life++;
                    break;
                case "-1":
                    P2life--;
                    break;
                case "+5":
                    P2life = P2life + 4;
                    break;
                case "-5":
                    P2life = P2life - 4;
                    break;
                default:
                    break;
            }

            p2life.setText(Integer.toString(P2life));
        }
        //endregion

        //region Navigation Drawer
        protected class DrawerItemClickListener implements ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(parent.getId()){
                    case (R.id.left_drawer):
                        selectItem(position);
                    default:
                        break;
                }


            }
        }

        private void selectItem(int position){
            // update the main content by replacing fragments
            switch (position){
                case 0: // first item - "main"
                    getFragmentManager().beginTransaction()
                            .remove(manaCalc_f)
                            .add(R.id.spinnerContainer, spinners_f)
                            .add(R.id.listviewContainer, listView_f, listview_tag)
                            .commit();
                    break;
                case 1:
                    manaCalc_f = ManaCalculatorFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .remove(spinners_f).remove(listView_f)
                            .replace(R.id.content, manaCalc_f).commit();
                    break;
                case 2: //lifecounter
                    lifeCount_f = LifeCounterFragment.newInstance();
                    getFragmentManager().beginTransaction().remove(spinners_f).remove(listView_f)
                            .replace(R.id.content, lifeCount_f).commit();
                    break;
//                case 1: //second item - decks
//                    deckView_f.newInstance(deck);
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.container, deckView_f)
//                            .addToBackStack("Your Deck")
//                            .commit();
//                    break;
//                case 2: // third item - life/dice counter
//                    dice = new DiceRollerFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.container, dice)
//                            .addToBackStack("Dice")
//                            .commit();
//                    break;
                default:
                    break;
            }

            mDrawerLayout.closeDrawer(mDrawerRelativeLeft);
    }
        //endregion

        //region optionsMenu
        @Override
        public boolean onCreateOptionsMenu(final Menu menu) {

            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);

            final MenuItem searchItem = menu.findItem(R.id.action_search);
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            SearchView.OnQueryTextListener q = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    String searchquery = String.valueOf(searchView.getQuery());
                    Log.d("Search", searchquery);
                    performSearch(searchquery);
                    sharedPrefs.edit().putString("query", searchquery).apply();
                    searchItem.collapseActionView();
                    searchView.setIconified(true);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            };
            searchView.setOnQueryTextListener(q);
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
            return true;
        }

//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            // Handle action bar item clicks here. The action bar will
//            // automatically handle clicks on the Home/Up button, so long
//            // as you specify a parent activity in AndroidManifest.xml.
//
//            switch (item.getItemId()) {
//                case R.id.sortPrice: //"Sort Prices: high to low"
//                    if(gettingPrices){
//                        Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show();
//                    } else {
//                    sortCardsByPrice(); }
//                    break;
//                case R.id.resetCardview: //"Reload"
//                    listView_f.adapter.clear();
//                    displayedCards.clear();
//                    displayedCards.addAll(masterCardList);
//                    listView_f.adapter.addAll(masterCardList);
//                    listView_f.adapter.indexcardsAlphabetically();
//                    getBus().post(new PleaseGetSetPriceEvent(mSet));
//                    gettingPrices = true;
//                    Toast.makeText(this, "reloading and getting prices", Toast.LENGTH_SHORT).show();
//                    //refreshFragment();
//                    break;
//            }
//
//            if (mDrawerToggle.onOptionsItemSelected(item)) {
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
        //endregion

        private void refreshFragment() {
            Fragment currentFragment = getFragmentManager().findFragmentByTag(listview_tag);
            FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
            fragTransaction.detach(currentFragment);
            fragTransaction.attach(currentFragment);
            fragTransaction.commit();
        }

        // Use some kind of injection, so that we can swap in a mock for tests.
        // Here we just use simple getter/setter injection for simplicity.
        private Bus getBus() {
            if (mBus == null) {
                mBus = BusProvider.getInstance();
            }
            return mBus;
        }
        public void setBus(Bus bus) {
            mBus = bus;
        }
        @Subscribe
        public void getError(ApiErrorEvent event) {
            Dialog.dismiss();
            DialogFragment dialog = new ErrorDialogFragment();
            dialog.show(getSupportFragmentManager(), "ErrorDialogFragment");
        }

    }

