package micahgemmell.com.mtg_deck_l;

import micahgemmell.com.mtg_deck_l.Adapter.CardListAdapter;
import micahgemmell.com.mtg_deck_l.Card.*;
import micahgemmell.com.mtg_deck_l.Fragments.CardImageFragment;
import micahgemmell.com.mtg_deck_l.Fragments.DeckFragment;
import micahgemmell.com.mtg_deck_l.Fragments.DiceRollerFragment;
import micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment;
import micahgemmell.com.mtg_deck_l.event.PleaseParseCardsEvent;
import micahgemmell.com.mtg_deck_l.helpers.BusProvider;
import micahgemmell.com.mtg_deck_l.event.CardsParsedEvent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.loopj.android.http.AsyncHttpClient;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import static micahgemmell.com.mtg_deck_l.Fragments.ListViewFragment.newInstance;

public class MainActivity extends Activity implements ListViewFragment.OnCardView, DiceRollerFragment.OnDiceRoll {
    //region VARIABLES
    //general
    private Bus mBus;
    private SharedPreferences sharedPrefs;
    private ProgressDialog Dialog;

    //Fragments
    DiceRollerFragment dice;
    ListViewFragment listView_f;
    DeckFragment deckView_f;
    CardImageFragment cardView_f;

    ListView container_listView;
    String listview_tag = "listviewFragment";

    //Lists
    private List<Card> masterCardList; //masterCardList keeps a copy of the original parsed list.
    List<Card> displayedCards;
    List<Card> deck;
    List<Card> SearchResults;
    List<Card> temp; // used for rarity adapter
    List<Card> rare;
    //
    private ArrayList<Creature> creatures;
    private ArrayList<Enchantment> enchantments;
    private ArrayList<Instant> instants;
    private ArrayList<Sorcery> sorceries;
    private ArrayList<Land> lands;
    private ArrayList<Artifact> artifacts;
    private ArrayList<Planeswalker> planeswalkers;

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
        deck = new ArrayList<Card>();
        SearchResults = new ArrayList<Card>();
        temp = new ArrayList<Card>();
        rare = new ArrayList<Card>();

        creatures = new ArrayList<Creature>();
        enchantments = new ArrayList<Enchantment>();
        instants = new ArrayList<Instant>();
        sorceries = new ArrayList<Sorcery>();
        lands = new ArrayList<Land>();
        artifacts = new ArrayList<Artifact>();
        planeswalkers = new ArrayList<Planeswalker>();
        //endregion
        //region Set up main container for the displayedCards.
        cardSetCode_array = getResources().getStringArray(R.array.sets);

        adapter = new CardListAdapter(this, displayedCards);
        listView_f = newInstance(displayedCards);

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.container, listView_f, listview_tag)
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

       mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){

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
       getActionBar().setDisplayHomeAsUpEnabled(true);
       getActionBar().setHomeButtonEnabled(true);
       //endregion
    }

    @Subscribe
    public void onCardsLoaded(CardsParsedEvent event) {
        masterCardList.addAll(event.getParsedCards());
        displayedCards.addAll(masterCardList);
        SearchResults.clear();
        listView_f.adapter.setCards(displayedCards);
        Dialog.dismiss();
        listView_f.adapter.notifyDataSetChanged();
    }

    protected void onResume(){
        super.onResume();
        spinnerPosition = sharedPrefs.getInt("spinnerPos", 0);
        getBus().register(this);
    }

    protected void onPause(){
        super.onPause();
        sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
        getBus().unregister(this);
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
            error404.setName("Sorry, no displayedCards matched your search.");
            error404.setType("Error 404 - Not Found");
            error404.setImageName("Null");
//            try {
//                error404.setCmc();//.setColors(new List<String>= {"Red"});
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            SearchResults.add(error404);
        }
        displayedCards.clear();
        displayedCards.addAll(SearchResults);
        listView_f.adapter.notifyDataSetChanged();
     }

    //region SPINNERS
    @Override
    public void spinnerItemSelected(int position, int id) {
        switch(id){
            case R.id.filterSetSpinner: //sets
                spinnerPosition = position;
                sharedPrefs.edit().putInt("spinnerPos", spinnerPosition).apply();
                String set = cardSetCode_array[position];

                if(set.equals("SET"))
                    break;
                Log.d("SPINNER", "selected ".concat(String.valueOf(spinnerPosition)));
                displayedCards.clear(); //clear the activities currently displayed displayedCards
                masterCardList.clear(); //clear master list of displayedCards
                listView_f.adapter.notifyDataSetChanged();
                getBus().post(new PleaseParseCardsEvent(set));
                Dialog.setMessage("loading " + listView_f.adapterforSetArray.getItem(spinnerPosition) + " cards.");
                Dialog.show();
                break;
            case R.id.sortRaritySpinner:
                String rarity;
                switch(position){
                    case 0:
                        displayedCards.clear();
                        if(!(SearchResults.isEmpty())){
                        displayedCards.addAll(SearchResults);
                        } else {
                            displayedCards.addAll(masterCardList);
                        }
                        listView_f.adapter.notifyDataSetChanged();
                        Log.d("adapter size", String.valueOf(listView_f.adapter.getItemCount()));
                        break;
                    case 1: // common
                        rarity = "Common";
                        setRarityAdapter(rarity);
                        break;
                    case 2: // uncommon
                        rarity = "Uncommon";
                        setRarityAdapter(rarity);
                        break;
                    case 3: // rare
                        rarity = "Rare";
                        setRarityAdapter(rarity);
                        break;
                    case 4: //mythic rare
                        rarity ="Mythic Rare";
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
        displayedCards.clear();
        if(!(SearchResults.isEmpty())){
            temp.addAll(SearchResults);
        } else {
            temp.addAll(masterCardList);
        }
        for (Card card : temp) {
            if (card.getRarity().equals(rarity)) {
                rare.add(card);
            }
        }
        displayedCards.addAll(rare);
        listView_f.adapter.notifyDataSetChanged();
        Log.d("adapter size", String.valueOf(listView_f.adapter.getItemCount()));
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

    //region CARD IMAGES
    @Override
    public void onCardImageViewUpdate(int position, String calledBy) {
        String image;
        String set;
        String imageURL;

//        if(calledBy == "deck")// == "deck")
//            {
//                cardView_f = new CardImageFragment(deck.get(position));
//                image = deck.get(position).getImageName();
//                set = deck.get(position).getSet();
//                imageURL = "http://mtgimage.com/set/".concat(set).concat("/").concat(image).concat(".jpg");
//                Log.d("tag", imageURL);
//                getCardImageFrom(imageURL);
//            } else if (calledBy == "set"){
//            cardView_f = new CardImageFragment(displayedCards.get(position));
//            image = displayedCards.get(position).getImageName();
//            set = displayedCards.get(position).getSet();
//            imageURL = "http://mtgimage.com/set/".concat(set).concat("/").concat(image).concat(".jpg");
//            Log.d("tag", imageURL);
//            getCardImageFrom(imageURL);
//        }
//
//        getFragmentManager().beginTransaction()
//                    .replace(R.id.container, cardView_f)
//                    .addToBackStack("CardView Back")
//                    .commit();

    }

    @Override
    public void showCardInfo(int position) {
        // Stub for a later implementation. (Slide down displayedCards.)
    }

    public void getCardImageFrom(String imageURL){
            AsyncHttpClient client = new AsyncHttpClient();
            String[] allowedContentTypes = new String[] { "image/jpeg" };
             client.get(imageURL, new BinaryHttpResponseHandler(allowedContentTypes) {
                @Override
                public void onSuccess(byte[] fileData) {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(fileData, 0, fileData.length);
                    if (cardView_f != null)
                     cardView_f.setImageView(imageBitmap);
              }
            });
        }
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
                listView_f.adapter.clear();
                listView_f.adapter.addAll(planeswalkers);
                break;
            case 2: //
                listView_f.adapter.clear();
                listView_f.adapter.addAll(creatures);
                break;
            case 3:
                listView_f.adapter.clear();
                listView_f.adapter.addAll(artifacts);
                break;
            case 4:
                listView_f.adapter.clear();
                listView_f.adapter.addAll(enchantments);
                break;
            case 5:
                listView_f.adapter.clear();
                listView_f.adapter.addAll(sorceries);
                break;
            case 6:
                listView_f.adapter.clear();
                listView_f.adapter.addAll(instants);
                break;
            case 7:
                listView_f.adapter.clear();
                listView_f.adapter.addAll(lands);
                break;
            default:
                break;
        }
            mDrawerLayout.closeDrawer(mDrawerRelativeRight);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

