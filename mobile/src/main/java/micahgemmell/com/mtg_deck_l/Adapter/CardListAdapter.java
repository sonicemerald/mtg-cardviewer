package micahgemmell.com.mtg_deck_l.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;
import micahgemmell.com.mtg_deck_l.helpers.symbolGetter;


public class CardListAdapter extends ArrayAdapter<Card> {
    private List<Card> mCards;
    private Context mContext;
    private static String mSet;
    private int SDK_INT = android.os.Build.VERSION.SDK_INT;

    public CardListAdapter (final Context context, int resource, List<Card> cards) {
        super(context, resource);
        mContext = context;
        mCards = cards;
    }


//    public void setSet(String set){
//        mSet = set;
//    }



    @Override
    public View getView(int position, View view, ViewGroup parent){

        if(view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.card_list_row, parent, false);
        }
        CardView cardView = (CardView) view.findViewById(R.id.card_view);
        Card card = this.getItem(position);
        TextView text = (TextView) view.findViewById(R.id.cardName_tv);
        TextView cmc = (TextView) view.findViewById(R.id.cmc_tv);
        mSet = card.getSet().toLowerCase();

        //region cardColor
        String cardColor;

        if (card.getColors().size()>1) {
            cardColor = "Gold";
        } else if (card.getColors().size() == 1){
            cardColor = this.getItem(position).getColors().get(0);
        } else {
            cardColor = "Other";
        }

        if(cardColor.equals("Gold")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gold_ripple));
        } else if (cardColor.equals("Blue")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.blue_ripple));
        } else if (cardColor.equals("Green")){
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.green));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.green));
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.green_ripple));
        } else if (cardColor.equals("White")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.white));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.white));
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.white_ripple));
        } else if (cardColor.equals("Black")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.BLACK);
            cmc.setBackgroundColor(Color.BLACK);
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.black_ripple));
        } else if (cardColor.equals("Red")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.red));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.red));
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.red_ripple));
        } else {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.GRAY);
            cmc.setBackgroundColor(Color.GRAY);
            if(this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gray_ripple));
        }
        //endregion
        //region CardInfo
        text.setText(card.getName());

        Html.ImageGetter imgGetter = symbolGetter.GlyphGetter(parent.getResources());
        cmc.setText(symbolGetter.formatStringWithGlyphs(card.getManaCost(), imgGetter));

        String rarity = card.getRarity();
        ImageView textRarity = (ImageView) view.findViewById(R.id.rarity_tv);
        textRarity.setBackgroundColor(Color.WHITE);

        switch(rarity.charAt(0)){
            case 'C': //common - black
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/"+mSet+"/"+Character.toLowerCase(rarity.charAt(0))+"/64.png").into(textRarity);
                break;
            case 'U': //uncommon - silver
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/"+mSet+"/"+Character.toLowerCase(rarity.charAt(0))+"/64.png").into(textRarity);
                break;
            case 'R':
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/"+mSet+"/"+Character.toLowerCase(rarity.charAt(0))+"/64.png").into(textRarity);
                break;
            case 'M':
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/"+mSet+"/"+Character.toLowerCase(rarity.charAt(0))+"/64.png").into(textRarity);
                break;
        }

        String type = card.getType();
        TextView subtypeText = (TextView) view.findViewById(R.id.subtype_tv);
        subtypeText.setText(type);
        subtypeText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView contentText = (TextView) view.findViewById(R.id.content_tv);
        contentText.setText(symbolGetter.formatStringWithGlyphs(card.getText(), imgGetter));
        contentText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView flavorText = (TextView) view.findViewById(R.id.flavor_tv);
        flavorText.setText(card.getFlavor());
        flavorText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView powertough = (TextView) view.findViewById(R.id.powertoughness_tv);
        if(card.getType().equals("Enchantment") || card.getType().equals("Instant") || card.getType().equals("Sorcery")){
            powertough.setVisibility(View.INVISIBLE);
        }
        if(!(card.getPower()==null)) {
            powertough.setText(card.getPower() + "/" + card.getToughness());
            powertough.setTextColor(parent.getResources().getColor(R.color.black));
        }
        //endregion
        //region Price
        TextView avgCost = (TextView) view.findViewById(R.id.avgCost_tv);
        if(card.getMedPrice() == null)
            avgCost.setText("getting card price...");
        else {
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

}