package micahgemmell.com.mtg_deck_l.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haarman.listviewanimations.itemmanipulation.ExpandableListItemAdapter;

import org.w3c.dom.Text;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CardListAdapter extends ExpandableListItemAdapter<Card> {
    private List<Card> mCards;
    private Context mContext;
    private View mView;

    public CardListAdapter (final Context context, List<Card> cards) {
        super(context);//, R.layout.card_list_row, R.id.card_title, R.id.card_content);
        mContext = context;
        mCards = cards;
    }




    @Override
    public View getTitleView(int position, View view, ViewGroup parent){

        if(view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.card_list_row, parent, false);
            mView = view;
        }

        Card card = this.getItem(position);


        //Dealing with card color
        TextView text = (TextView) view.findViewById(R.id.cardName_tv);
        //CardView cardv = (CardView) view.findViewById(R.id.card_view);
        String cardColor;
        text.setText(card.getName());
        if (card.getColors().size()>0) {
            cardColor = this.getItem(position).getColors().get(0);
        }
        else {
            cardColor = "Other";
        }

        if (cardColor.equals("Blue")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.blue));
        } else if (cardColor.equals("Green")){
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.green));
        } else if (cardColor.equals("White")) {
            text.setBackgroundColor(Color.WHITE);
            text.setTextColor(Color.BLACK);
        } else if (cardColor.equals("Black")) {
            text.setBackgroundColor(Color.BLACK);
            text.setTextColor(Color.WHITE);
        } else if (cardColor.equals("Red")) {
//            cardv.setBackgroundColor(parent.getResources().getColor(R.color.red));
            text.setBackgroundColor(parent.getResources().getColor(R.color.red));
            text.setTextColor(Color.WHITE);
        } else {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.GRAY);
        }

        String rarity = card.getRarity();
        TextView textRarity = (TextView) view.findViewById(R.id.rarity_tv);

        switch(rarity.charAt(0)){
            case 'C': //common - black
                textRarity.setText("C");
                textRarity.setTextColor(parent.getResources().getColor(R.color.gold));
                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.black));
                break;
            case 'U': //uncommon - silver
                textRarity.setText("U");
                textRarity.setTextColor(parent.getResources().getColor(R.color.black));
                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.silver));
                break;
            case 'R':
                textRarity.setText("R");
                textRarity.setTextColor(parent.getResources().getColor(R.color.black));
                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.gold));
                break;
            case 'M':
                textRarity.setText("MR");
                textRarity.setTextColor(parent.getResources().getColor(R.color.silver));
                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.orangered));
                break;
        }

        String type = card.getType();


        TextView subtypeText = (TextView) view.findViewById(R.id.subtype_tv);
        subtypeText.setText(type);

        return view;
    }

    @NonNull
    @Override
    public View getContentView(int i, View view, ViewGroup viewGroup) {
       // TextView textView = (TextView)view;

        if(view == null){
            LayoutInflater inflater;
            inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.card_list_row, null);
        }

        TextView t = (TextView)view.findViewById(R.id.cardInfo_tv);
        t.setBackgroundColor(viewGroup.getResources().getColor(R.color.white));

        Card card = this.getItem(i);
        Log.d("3", card.getText());

        t.setText(card.getText());
        t.setLines(8);


        return view;
    }
}