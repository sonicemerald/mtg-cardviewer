    package micahgemmell.com.mtg_deck_l;

    import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
    import micahgemmell.com.mtg_deck_l.Card.*;
    import micahgemmell.com.mtg_deck_l.Fragments.CardImageFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.CardViewFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DeckFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.DiceRollerFragment;
    import micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment;
//    import micahgemmell.com.mtg_deck_l.event.CardPricedEvent;
//    import micahgemmell.com.mtg_deck_l.event.PleaseGetCardPriceEvent;
    import micahgemmell.com.mtg_deck_l.Fragments.SpinnerFragment;
    import micahgemmell.com.mtg_deck_l.event.PleaseGetSetPriceEvent;
    import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
    import micahgemmell.com.mtg_deck_l.event.SetPricedEvent;
    import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
    import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;

    import android.app.Activity;
    import android.app.Fragment;
    import android.app.FragmentTransaction;
    import android.app.ProgressDialog;
    import android.app.SearchManager;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.support.v7.app.ActionBarDrawerToggle;
    import android.support.v4.widget.DrawerLayout;
    import android.util.Log;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.RelativeLayout;
    import android.widget.SearchView;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.List;
    import java.util.Random;

    import com.squareup.otto.Bus;
    import com.squareup.otto.Subscribe;

    import static micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment.newInstance;

    public class MainActivity extends Activity implements ListViewFragment.CardListViewInterface, DiceRollerFragment.OnDiceRoll, CardViewFragment.OnCardViewFragmentInteraction, SpinnerFragment.SpinnerInterface, CardImageFragment.OnCardImageClicked {
        //region VARIABLES
        //general
        private Bus mBus;
        private SharedPreferences sharedPrefs;
        private ProgressDialog Dialog;
        public String mSet;

        //Fragments
        DiceRollerFragment dice;
        ListViewFragment listView_f;
        SpinnerFragment spinners_f;
        DeckFragment deckView_f;
        CardImageFragment cardImageView_f;
        CardViewFragment cardView_f;

       // ListView container_listView;
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
        //
    //    private ArrayList<Creature> creatures;
    //    private ArrayList<Enchantment> enchantments;
    //    private ArrayList<Instant> instants;
    //    private ArrayList<Sorcery> sorceries;
    //    private ArrayList<Land> lands;
    //    private ArrayList<Artifact> artifacts;
    //    private ArrayList<Planeswalker> planeswalkers;

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
        DrawerItemClickListener dListener;
        RelativeLayout mDrawerRelativeLeft;
        RelativeLayout mDrawerRelativeRight;
        CharSequence mDrawerTitle = "Menu";
        String[] navMenuItems;

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

           mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close){
              public void onDrawerClosed(View view) {
                   getActionBar().setTitle(R.string.app_name);
                   //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
               }
              public void onDrawerOpened(View drawerView) {
                   getActionBar().setTitle(mDrawerTitle);
                   //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
               }
           };
           mDrawerLayout.setDrawerListener(mDrawerToggle);
           mDrawerToggle.setDrawerIndicatorEnabled(true);
           getActionBar().setHomeButtonEnabled(true);
           //endregion
        }

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
            Dialog.dismiss();
            listView_f.refresh();
        }

        @Subscribe
        public void onCardsPriced(SetPricedEvent event){
            List<CardPrice> PricesArray = event.getPricesArray();

            if(PricesArray.size() > 1) {
                int i = 0;

                for (Card a : masterCardList) {
                    a.setHighPrice(PricesArray.get(i).getHigh());
                    a.setMedPrice(PricesArray.get(i).getMed());
                    a.setLowPrice(PricesArray.get(i).getLow());
                    i++;
                }
                Log.d(PricesArray.get(164).getName(), "");
                Log.d(PricesArray.get(165).getName(), "");
                Log.d(masterCardList.get(164).getName(),"");
                Log.d(masterCardList.get(165).getName(),"");
                listView_f.adapter.notifyDataSetChanged();
                Toast.makeText(this, "finished getting prices", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("priceError","can't parse " + mSet);
            }
        }

        //region LIFECYCLE
        protected void onResume(){
            super.onResume();
            //this gets called?
            Log.d("", "MainActivity onResume called, " + displayedCards.toString());
            spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
            getBus().register(this);
        }

        protected void onPause(){
            super.onPause();
            Log.d("", "MainActivity onPause");
            sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
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

                    if(mSet.equals("SET"))
                        break;
                    Log.d("SPINNER", "selected ".concat(String.valueOf(spinnerPosition)));
                    displayedCards.clear(); //clear the activities currently displayed displayedCards
                    listView_f.adapter.clear(); //clear the adapters list.
                    masterCardList.clear(); //clear master list of displayedCards
                    getBus().post(new PleaseParseCardsEvent(cardSetCode_array[position]));
                    Dialog.setMessage("loading " + spinners_f.adapterforSetArray.getItem(spinnerPosition) + " cards.");
                    Dialog.setCanceledOnTouchOutside(false);
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
            getFragmentManager().beginTransaction()
                    .detach(spinners_f).detach(listView_f)
                    .replace(R.id.container, cardView_f)
                    .addToBackStack("Main list back")
                    .commit();
        }

        @Override
        public void onCardImageClicked(){
            //when card image clicked, return.
            getFragmentManager().popBackStack();
        }

        //region CARD IMAGES
//        @Override
//        public void onCardImageViewUpdate(int position, String calledBy) {
//            String image;
//            String imageURL;
//
//            Log.d("", "Displayed cards is: "+String.valueOf(displayedCards.size()));
//            cardImageView_f = CardImageFragment.newInstance(displayedCards.get(position));
//            image = displayedCards.get(position).getImageName();
//            mSet = cardSetCode_array[spinnerPosition];
//            imageURL = "http://mtgimage.com/set/".concat(mSet).concat("/").concat(image).concat(".jpg");
//            Log.d("tag", imageURL);
//            getCardImageFrom(imageURL);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.container, cardImageView_f)
//                    .addToBackStack("CardView Back")
//                    .commit();
//
//        }

        //When a user clicks the card image in the CardViewFragment.
        @Override
        public void onImageClicked(String image) {
            cardImageView_f = CardImageFragment.newInstance(image);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, cardImageView_f)
                    .addToBackStack("card view back")
                    .commit();
        }

//        public void getCardImageFrom(String imageURL){
//                AsyncHttpClient client = new AsyncHttpClient();
//                String[] allowedContentTypes = new String[] { "image/jpeg" };
//                 client.get(imageURL, new BinaryHttpResponseHandler(allowedContentTypes) {
//                    @Override
//                    public void onSuccess(byte[] fileData) {
//                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
//                        if (cardImageView_f != null)
//                         cardImageView_f.setImageView(imageBitmap);
//                  }
//                });
//            }
        //endregion

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
                case 0: // first item - "search displayedCards"
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, listView_f)
                            .addToBackStack("Search")
                            .commit();
                    break;
                case 1: //second item - decks
                    deckView_f.newInstance(deck);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, deckView_f)
                            .addToBackStack("Your Deck")
                            .commit();
                    break;
                case 2: // third item - life/dice counter
                    dice = new DiceRollerFragment();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, dice)
                            .addToBackStack("Dice")
                            .commit();
                    break;
                default:
                    break;
            }

            mDrawerLayout.closeDrawer(mDrawerRelativeLeft);
    }

        @Override
        public boolean onCreateOptionsMenu(final Menu menu) {

            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);

            final MenuItem mensu = (MenuItem) menu.findItem(R.id.search);
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            SearchView.OnQueryTextListener q = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    String searchquery = String.valueOf(searchView.getQuery());
                    Log.d("Search", searchquery);
                    performSearch(searchquery);
                    sharedPrefs.edit().putString("query", searchquery).commit();
                    mensu.collapseActionView();
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
                    sortCardsByPrices();
                case R.id.resetCardview:
                    listView_f.adapter.clear();
                    listView_f.adapter.addAll(masterCardList);
                    refreshFragment();
            }

            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        void sortCardsByPrices(){
                // low to high
                Collections.sort(this.displayedCards, new Comparator<Card>() {
                    @Override
                    public int compare(Card one, Card two) {
                        if (Double.parseDouble(one.getMedPrice().substring(1)) > Double.parseDouble(two.getMedPrice().substring(1))){
                            Log.d(one.getMedPrice() + " > " + two.getMedPrice(), "sorting");
                            return -1;
                        }
                        if (Double.parseDouble(one.getMedPrice().substring(1)) < Double.parseDouble(two.getMedPrice().substring(1))) {
                            Log.d(one.getMedPrice() + " < " + two.getMedPrice(), "sorting");
                            return 1;
                        }
                        else // equal
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

        //endregion

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
    }

