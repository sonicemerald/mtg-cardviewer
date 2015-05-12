    package micahgemmell.com.mtg_deck_l;

    import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
    import micahgemmell.com.mtg_deck_l.Card.*;
    import micahgemmell.com.mtg_deck_l.Fragments.CardImageFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.CardViewFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DeckFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DiceRollerFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ErrorDialogFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.SpinnerFragment;
    import micahgemmell.com.mtg_deck_l.event.ApiErrorEvent;
    import micahgemmell.com.mtg_deck_l.event.PleaseGetSetPriceEvent;
    import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
    import micahgemmell.com.mtg_deck_l.event.SetPricedEvent;
    import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
    import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;
    import micahgemmell.com.mtg_deck_l.helpers.CropTransform;

    import android.app.AlertDialog;
    import android.app.Fragment;
    import android.app.FragmentTransaction;
    import android.app.ProgressDialog;
    import android.app.SearchManager;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.support.v4.app.DialogFragment;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v7.app.ActionBarActivity;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v4.widget.DrawerLayout;
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
    import android.widget.TextView;
    import android.widget.Toast;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Random;

    import com.squareup.otto.Bus;
    import com.squareup.otto.Subscribe;
    import com.squareup.picasso.Picasso;

    import static micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment.newInstance;

    public class MainActivity extends ActionBarActivity implements ListViewFragment.CardListViewInterface, DiceRollerFragment.OnDiceRoll, CardViewFragment.OnCardViewFragmentInteraction, SpinnerFragment.SpinnerInterface, CardImageFragment.OnCardImageClicked {
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

        //ListView container_listView;
        String listview_tag = "listviewFragment";

        //Lists
        private List<Card> masterCardList; //masterCardList keepshttp://www.gamer-heaven.net/sonic-the-hedgehog-official-socks-3-pack/ a copy of the original parsed list.
        List<Card> displayedCards;
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

        //Navigation Drawer
        ListView NavigationDrawer_listView_Left; // used for the "navigation"
        ListView NavigationDrawer_listView_Right; // used for sort
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
        //endregion

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
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
            displayedTypes = new ArrayList<Card>();
            deck = new ArrayList<Card>();
            SearchResults = new ArrayList<Card>();
            temp = new ArrayList<Card>();
            rare = new ArrayList<Card>();
            type = new ArrayList<Card>();

    //        creatures = new ArrayList<Creature>();
    //        enchantments = new ArrayList<Enchantment>();
    //        instants = new ArrayList<Instant>();
    //        sorceries = new ArrayList<Sorcery>();
    //        lands = new ArrayList<Land>();
    //        artifacts = new ArrayList<Artifact>();
    //        planeswalkers = new ArrayList<Planeswalker>();
            //endregion
            //region Set up main container for the displayedCards.
            cardSetCode_array = getResources().getStringArray(R.array.sets);

            adapter = new CardListAdapter(this, R.id.card_list_layout, displayedCards);
            listView_f = newInstance(displayedCards);
            spinners_f = SpinnerFragment.newInstance();

                if (savedInstanceState == null){
                getFragmentManager().beginTransaction()
                        .add(R.id.spinnerContainer, spinners_f)
                        .add(R.id.listviewContainer, listView_f, listview_tag)
                        .commit();
            }
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

           //Right Side Setup
           NavigationDrawer_listView_Right = (ListView) findViewById(R.id.right_drawer); //Find where we want to put the list
           SortingItems = getResources().getStringArray(R.array.nav_drawer_sorting_items); // Get the Array of items.
           adapterforStringArray = new ArrayAdapter<String>(this, R.layout.drawer_list_item, SortingItems); // need to adapt the array of items
           //Now set the adapter.
           NavigationDrawer_listView_Right.setAdapter(adapterforStringArray);
           NavigationDrawer_listView_Right.setOnItemClickListener(dListener);

           //setting up for open close drawer
           mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           mDrawerRelativeLeft = (RelativeLayout) findViewById(R.id.drawer_layout_container_left);
           mDrawerRelativeRight = (RelativeLayout) findViewById(R.id.drawer_layout_container_right);

           mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar, R.string.drawer_open, R.string.drawer_close){
              public void onDrawerClosed(View view) {
                  getSupportActionBar().setTitle(R.string.app_name);
                   //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
               }
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
                favImage = "http://mtgimage.com/actual/cardback.hq.jpg";
            }
            Picasso.with(this).load(favImage).fit().centerCrop().into(mDrawerImage);
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
            displayedTypes.clear();
            displayedCards.clear();
            masterCardList.addAll(event.getParsedCards());
            displayedCards.addAll(masterCardList);
            displayedTypes.addAll(masterCardList);
            SearchResults.clear();
            listView_f.adapter.addAll(displayedCards);
            getBus().post(new PleaseGetSetPriceEvent(mSet));
            gettingPrices = true;
            Dialog.dismiss();
            listView_f.refresh();
        }

        @Subscribe
        public void onCardsPriced(SetPricedEvent event){
            List<CardPrice> PricesArray = event.getPricesArray();
            try {
                if (PricesArray.size() > 1) {
                    int i = 0;
                    for (Card a : masterCardList) {
                        if (a.getName().substring(0,4).equals(PricesArray.get(i).getName().substring(0, 4))) {
                            a.setHighPrice(PricesArray.get(i).getHigh());
                            a.setMedPrice(PricesArray.get(i).getMed());
                            a.setLowPrice(PricesArray.get(i).getLow());
                            a.setPriceHidden(false);
                            i++;
                        } else if (!a.getName().substring(0,4).equals(PricesArray.get(i).getName().substring(0, 4))){
                            Log.d("", a.getName() + "does not match" + PricesArray.get(i).getName());
                            a.setMedPrice("$0.0");
                            a.setPriceHidden(true);
                        }
                    }
                    listView_f.adapter.notifyDataSetChanged();
                    gettingPrices = false;
                    Toast.makeText(this, "finished getting prices", Toast.LENGTH_SHORT).show();
                }
            } catch (RuntimeException e) {
                Log.d("priceError","can't parse " + mSet);
                if(mSet.equals("Innistrad") || mSet.equals("Dragon's Maze")){
                    Toast.makeText(this, "Sorry, this sets price has trouble, i'm working on it", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //region LIFECYCLE
        protected void onResume(){
            super.onResume();
            //this gets called?
            Log.d("", "MainActivity onResume called, " + displayedCards.toString());
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
            favImage = sharedPrefs.getString("favImage", favImage);
            getBus().register(this);
        }

        protected void onPause(){
            super.onPause();
            Log.d("", "MainActivity onPause");
            sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
            sharedPrefs.edit().putString("favImage", favImage).apply();
            getBus().unregister(this);
        }

        protected void onStart(){
            super.onStart();
            Log.d("", "activity started");
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

          //spinnerPosition = savedInstanceState.getInt("spinnerPos");
            spinnerPosition = sharedPrefs.getInt("spinnerPos", spinnerPosition);
            Log.d("restore", "restored");
        }

        @Override
        public void onBackPressed() {
            if(mDrawerLayout.isDrawerOpen(Gravity.START))
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

            cQuery = query;

            if (cQuery.length()>1)
                cQuery = cQuery.substring(0,1).toUpperCase() + cQuery.substring(1);
            else
                cQuery = cQuery.toUpperCase();

            for (Card card : displayedCards) {
                if (card.getName().toLowerCase().contains(query) || card.getColors().contains(cQuery)) {
                    SearchResults.add(card);
                }
            }
            if(SearchResults.size() == 0){
                Card error404 = new Card();
                error404.setName("Sorry, no cards matched your search.");
                error404.setType("Error 404 - Not Found");
                error404.setMedPrice("");
                error404.setImageName("Null");
    //            try {
    //                error404.setCmc();//.setColors(new List<String>= {"Red"});
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            }
                SearchResults.add(error404);
            }
            displayedCards.clear();
            listView_f.adapter.clear();
            displayedCards.addAll(SearchResults);
            listView_f.adapter.addAll(displayedCards);
         }

        //region SPINNERS
        @Override
        public void spinnerItemSelected(int position, int id) {
            switch(id){
                case R.id.filterSetSpinner: //sets
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
                    break;
                case R.id.sortRaritySpinner:
                    String rarity;
                    switch(position){
                        case 0:
                            break;
                        case 1:
                            displayedCards.clear();
                            if(!(SearchResults.isEmpty())) {
                                displayedCards.addAll(SearchResults);
                            } else {
                                displayedCards.addAll(displayedTypes);
                            }
                            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
                            break;
                        case 2: // common
                            rarity = "Common";
                            setRarityAdapter(rarity);
                            break;
                        case 3: // uncommon
                            rarity = "Uncommon";
                            setRarityAdapter(rarity);
                            break;
                        case 4: // rare
                            rarity = "Rare";
                            setRarityAdapter(rarity);
                            break;
                        case 5: //mythic rare
                            rarity = "Mythic Rare";
                            setRarityAdapter(rarity);
                            break;
                        default: break;
                      }
                    break;
                default: break;
                    }

                    }
        private void setRarityAdapter(String rarity){
            temp.clear();
            rare.clear();
            //displayedCards.clear();
            listView_f.adapter.clear();

            if(!(SearchResults.isEmpty())){
                temp.addAll(SearchResults);
            } else {
                temp.addAll(displayedTypes);
            }
            for (Card card : temp) {
                if (card.getRarity().equals(rarity)) {
                    rare.add(card);
                }
            }
            displayedCards.clear();
            displayedCards.addAll(rare);
            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
            listView_f.adapter.addAll(displayedCards);
            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
        }
        @Override
        public int getSpinnerPosition() {
            sharedPrefs = this.getSharedPreferences("micahgemmell.com.mtg_deck_l", Context.MODE_PRIVATE);
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
            return spinnerPosition; }
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
                    .addToBackStack("back to the mainlist")
                    .commit();
        }

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
            Toast.makeText(this, "Favorite image replaced", Toast.LENGTH_SHORT).show();
            Picasso.with(this).load(favImage).transform(new CropTransform()).into(mDrawerImage);
        }
        //endregion

        @Override
        public void onTransform(String card){
                for (Card a : displayedCards) {
                    if (a.getName().contains(card)) {
                        cardView_f = CardViewFragment.newInstance(a);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.listviewContainer, cardView_f)
                                .commit();
                    }
                }
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
        //endregion

        //region Navigation Drawer

        protected class DrawerItemClickListener implements ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(parent.getId()){
                    case (R.id.left_drawer):
                        selectItem(position);
                    case (R.id.right_drawer):
                        sortItems(position);
                    default:
                        break;
                }


            }
        }

        private void sortItems(int position) {
            switch (position){
                case 0: //all
                    listView_f.adapter.clear();
                    listView_f.adapter.addAll(masterCardList);
                    break;
                case 1: //
                    setTypeAdapter("Planeswalker");
                    break;
                case 2: //
                    setTypeAdapter("Creature");
                    break;
                case 3:
                    setTypeAdapter("Artifact");
                    break;
                case 4:
                    setTypeAdapter("Enchantment");
                    break;
                case 5:
                    setTypeAdapter("Sorcery");
                    break;
                case 6:
                    setTypeAdapter("Instant");
                    break;
                case 7:
                    setTypeAdapter("Land");
                    break;
                default:
                    break;
            }
                mDrawerLayout.closeDrawer(mDrawerRelativeRight);
        }

        private void setTypeAdapter(String types){
            temp.clear();
            type.clear();
            displayedCards.clear();
            displayedTypes.clear();
            listView_f.adapter.clear();

            if(!(SearchResults.isEmpty())){
                temp.addAll(SearchResults);
            } else {
                temp.addAll(masterCardList);
            }
            for (Card card : temp) {
                if (card.getTypes().contains(types)) {
                    type.add(card);
                }
            }
            displayedTypes.addAll(type);
            displayedCards.addAll(type);
            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
            listView_f.adapter.addAll(displayedCards);
            Log.d("adapter size", String.valueOf(listView_f.adapter.getCount()));
        }

        private void selectItem(int position){
            // update the main content by replacing fragments
            switch (position){
                case 0: // first item - "main"
                    getFragmentManager().beginTransaction()
                            .attach(spinners_f).attach(listView_f)
                            .replace(R.id.listviewContainer, listView_f)
                            .commit();
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

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.

            switch (item.getItemId()) {
                case R.id.sortPrice:
                    if(gettingPrices){
                        Toast.makeText(this, "Please wait", Toast.LENGTH_LONG).show();
                    } else {
                    sortCardsByPrice(); }
                    break;
                case R.id.resetCardview:
                    listView_f.adapter.clear();
                    listView_f.adapter.addAll(masterCardList);
                    refreshFragment();
                    break;
            }

            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        //endregion

        void sortCardsByPrice(){
                // low to high
                Collections.sort(this.displayedCards, new Comparator<Card>() {
                    @Override
                    public int compare(Card one, Card two) {
                            if (Double.parseDouble(one.getMedPrice().substring(1)) > Double.parseDouble(two.getMedPrice().substring(1))) {
                                Log.d(one.getMedPrice() + " > " + two.getMedPrice(), "sorting");
                                return -1;
                            }
                            if (Double.parseDouble(one.getMedPrice().substring(1)) < Double.parseDouble(two.getMedPrice().substring(1))) {
                                Log.d(one.getMedPrice() + " < " + two.getMedPrice(), "sorting");
                                return 1;
                            } else // equal
                                Log.d(one.getMedPrice() + " = " + two.getMedPrice(), "sorting");
                            return 0;
                    }
                });
            //Toast.makeText(this, "sort complete", Toast.LENGTH_SHORT).show();
            listView_f.adapter.notifyDataSetChanged();
            refreshFragment();
        }

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

