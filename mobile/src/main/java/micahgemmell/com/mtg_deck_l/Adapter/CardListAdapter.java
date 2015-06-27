package micahgemmell.com.mtg_deck_l.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.widget.SectionIndexer;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;
import micahgemmell.com.mtg_deck_l.helpers.symbolGetter;


public class CardListAdapter extends ArrayAdapter<Card> implements SectionIndexer {
    HashMap<String, Integer> cardIndex;
    private List<Card> mCards;
    private Context mContext;
    private static String mSet;
    String[] sections;
    private int SDK_INT = android.os.Build.VERSION.SDK_INT;
    public ArrayList<String> sectionList;

    public CardListAdapter (final Context context, int resource, List<Card> cards) {
        super(context, resource);
        mContext = context;
        mCards = cards;
    }

    public void indexcardsAlphabetically(){
        cardIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mCards.size(); x++) {
            String name = mCards.get(x).getName();
            String ch = name.substring(0, 1);
            if(ch.equals("Æ")){
                ch = "a";
            }
            ch = ch.toUpperCase(Locale.US);
            Log.d("letter", ch);
            // HashMap will prevent duplicates
            if(!(cardIndex.containsKey(ch)))
                cardIndex.put(ch, x);

        }

        Set<String> sectionLetters = cardIndex.keySet();

        // create a list from the set to sort
        sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());
        Collections.sort(sectionList);

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }
    public void indexCardsByRarity(){
        cardIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mCards.size(); x++) {
            String rarity = mCards.get(x).getRarity();
//            String r = rarity.substring(0,1);

            // HashMap will prevent duplicates
            if(!(cardIndex.containsKey(rarity)))
                cardIndex.put(rarity, x);
        }

        Set<String> sectionLetters = cardIndex.keySet();

        // create a list from the set to sort
        sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }
    public void indexCardsByColor() {
        cardIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mCards.size(); x++) {
            String color;
            if (mCards.get(x).getColors().size() > 1) {
                color = "Gold"; //"gold"
            } else if (mCards.get(x).getColors().size() == 1) {
                color = mCards.get(x).getColors().get(0);
            } else {
                color = "Colorless";
            }

            // HashMap will prevent duplicates
            if (!(cardIndex.containsKey(color)))
                cardIndex.put(color, x);
        }

        Set<String> sectionLetters = cardIndex.keySet();

        // create a list from the set to sort
        sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }
    public void indexCardsByType() {
        cardIndex = new LinkedHashMap<String, Integer>();

        for (int x = 0; x < mCards.size(); x++) {
            String type;
            type = mCards.get(x).getTypes().get(0);

            // HashMap will prevent duplicates
            if (!(cardIndex.containsKey(type)))
                cardIndex.put(type, x);
        }

        Set<String> sectionLetters = cardIndex.keySet();

        // create a list from the set to sort
        sectionList = new ArrayList<String>(sectionLetters);

        Log.d("sectionList", sectionList.toString());

        sections = new String[sectionList.size()];

        sectionList.toArray(sections);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.card_list_row, parent, false);
        }

        CardView cardView = (CardView) view.findViewById(R.id.card_list_main);
        Card card = this.getItem(position);
        TextView text = (TextView) view.findViewById(R.id.card_list_name);
        TextView cmc = (TextView) view.findViewById(R.id.card_list_cmc);
        mSet = card.getSet().toLowerCase();

        //region cardColor
        String cardColor;

        if (card.getColors().size() > 1) {
            cardColor = "Gold";
        } else if (card.getColors().size() == 1) {
            cardColor = this.getItem(position).getColors().get(0);
        } else {
            cardColor = "Other";
        }

        if (cardColor.equals("Gold")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gold_ripple));
        } else if (cardColor.equals("Blue")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.blue_ripple));
        } else if (cardColor.equals("Green")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.green));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.green));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.green_ripple));
        } else if (cardColor.equals("White")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.white));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.white));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.white_ripple));
        } else if (cardColor.equals("Black")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.BLACK);
            cmc.setBackgroundColor(Color.BLACK);
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.black_ripple));
        } else if (cardColor.equals("Red")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.red));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.red));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.red_ripple));
        } else {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.GRAY);
            cmc.setBackgroundColor(Color.GRAY);
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gray_ripple));
        }
        //endregion
        //region CardInfo
        text.setText(card.getName());

        Html.ImageGetter imgGetter = symbolGetter.GlyphGetter(parent.getResources());
        cmc.setText(symbolGetter.formatStringWithGlyphs(card.getManaCost(), imgGetter));

        String rarity = card.getRarity();
        ImageView textRarity = (ImageView) view.findViewById(R.id.card_list_rarity);
        //textRarity.setBackgroundColor();

        switch (rarity.charAt(0)) {
            case 'C': //common - black
                Picasso.with(mContext).load("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + mSet + "&size=medium&rarity=" + Character.toLowerCase(rarity.charAt(0))).into(textRarity);
                break;
            case 'U': //uncommon - silver
                Picasso.with(mContext).load("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + mSet + "&size=medium&rarity=" + Character.toLowerCase(rarity.charAt(0))).into(textRarity);
                break;
            case 'R':
                Picasso.with(mContext).load("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + mSet + "&size=medium&rarity=" + Character.toLowerCase(rarity.charAt(0))).into(textRarity);
                break;
            case 'M':
                Picasso.with(mContext).load("http://gatherer.wizards.com/Handlers/Image.ashx?type=symbol&set=" + mSet + "&size=medium&rarity=" + Character.toLowerCase(rarity.charAt(0))).into(textRarity);
                break;
            default:
                break;
        }

        String type = card.getType();
        TextView subtypeText = (TextView) view.findViewById(R.id.card_list_subtype);
        subtypeText.setText(type);
        subtypeText.setTextColor(parent.getResources().getColor(R.color.black));
        subtypeText.setMaxLines(1);


        TextView contentText = (TextView) view.findViewById(R.id.card_list_content);
        contentText.setText(symbolGetter.formatStringWithGlyphs(card.getText(), imgGetter));
        contentText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView flavorText = (TextView) view.findViewById(R.id.card_list_flavortext);
        flavorText.setText(card.getFlavor());
        flavorText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView powertough = (TextView) view.findViewById(R.id.card_list_powertoughness);
        powertough.setTextColor(parent.getResources().getColor(R.color.black));

        if (type.equals("Enchantment") || type.equals("Instant") || type.equals("Sorcery") || type.equals("Land")) {
            powertough.setVisibility(View.GONE);
        }
        if (!(card.getPower() == null)) {
            powertough.setVisibility(View.VISIBLE);
            powertough.setText(card.getPower() + "/" + card.getToughness());
        }
        if (!(card.getLoyalty() == null)) {
            powertough.setVisibility(View.VISIBLE);
            powertough.setText(card.getLoyalty());
        }
        //endregion
        //region Price
        TextView avgCost = (TextView) view.findViewById(R.id.card_list_avgcost);
        if (card.isPriceHidden()) {
            avgCost.setVisibility(View.INVISIBLE);
        }
        if (card.getMedPrice() == null){
            avgCost.setVisibility(View.VISIBLE);
            avgCost.setText("getting card price...");
        } else {
            avgCost.setText("Average Price: " + card.getMedPrice());
        }
        //endregion
        //region debug
        if(view.isInEditMode())
        {
            text.setText("Name of Card");
            text.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            textRarity.setVisibility(View.INVISIBLE);
        }
        //endregion
        return view;
    }

    //SectionIndexer
    @Override
    public int getPositionForSection(int section) {
        //Log.d("section", "" + section);
        return cardIndex.get(sections[section]);
    }
    @Override
    public int getSectionForPosition(int position) {
        //Log.d("position", "" + position);
        for (int i = sections.length - 1; i >= 0; i--) {
            if (position >= cardIndex.get(sections[i])) {
                return i;
            }
        }
        return 0;
    }
    @Override
    public Object[] getSections() {
        return sections;
    }
}