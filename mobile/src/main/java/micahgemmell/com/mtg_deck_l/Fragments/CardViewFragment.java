package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;
import micahgemmell.com.mtg_deck_l.helpers.symbolGetter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CardViewFragment.OnCardViewFragmentInteraction} interface
 * to handle interaction events.
 * Use the {@link CardViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardViewFragment extends Fragment {

    private Card card;
    private OnCardViewFragmentInteraction mListener;
    private String mSet;
    private int SDK_INT = android.os.Build.VERSION.SDK_INT;
    private Context mContext;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     * @return A new instance of fragment CardViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardViewFragment newInstance(Card card) {
        CardViewFragment fragment = new CardViewFragment();
        Context context;
        fragment.card = card;
        return fragment;
    }

    public CardViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        Html.ImageGetter imgGetter = symbolGetter.GlyphGetter(parent.getResources());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_view, parent, false);
        mSet = card.getSet().toLowerCase();

        //region Colors
        TextView text = (TextView) view.findViewById(R.id.card_view_cardname);
        TextView cmc = (TextView) view.findViewById(R.id.card_view_cmc);
        final ImageView cardImage = (ImageView) view.findViewById(R.id.card_view_image);
        text.setText(card.getName());

        cmc.setText(symbolGetter.formatStringWithGlyphs(card.getManaCost(), imgGetter));

        String cardColor;

        if (card.getColors().size() > 1) {
            cardColor = "Gold";
        } else if (card.getColors().size() == 1) {
            cardColor = card.getColors().get(0);
        } else {
            cardColor = "Other";
        }

        CardView cardView = (CardView) view.findViewById(R.id.card_view_container);

        if (cardColor.equals("Gold")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            cardImage.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gold_ripple));
        } else if (cardColor.equals("Blue")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            cardImage.setBackgroundColor(parent.getResources().getColor(R.color.blue));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.blue_ripple));
        } else if (cardColor.equals("Green")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.green));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.green));
            cardImage.setBackgroundColor(parent.getResources().getColor(R.color.green));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.green_ripple));
        } else if (cardColor.equals("White")) {
            text.setTextColor(Color.BLACK);
            text.setBackgroundColor(parent.getResources().getColor(R.color.white));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.white));
            cardImage.setBackgroundColor(parent.getResources().getColor(R.color.white));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.white_ripple));
        } else if (cardColor.equals("Black")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.BLACK);
            cmc.setBackgroundColor(Color.BLACK);
            cardImage.setBackgroundColor(Color.BLACK);
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.black_ripple));
        } else if (cardColor.equals("Red")) {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(parent.getResources().getColor(R.color.red));
            cmc.setBackgroundColor(parent.getResources().getColor(R.color.red));
            cardImage.setBackgroundColor(parent.getResources().getColor(R.color.red));
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.red_ripple));
        } else {
            text.setTextColor(Color.WHITE);
            text.setBackgroundColor(Color.GRAY);
            cmc.setBackgroundColor(Color.GRAY);
            cardImage.setBackgroundColor(Color.GRAY);
            if (this.SDK_INT >= 21)
                cardView.setForeground(parent.getResources().getDrawable(R.drawable.gray_ripple));
        }
        //endregion
        //region images
        final String imageURL = "http://mtgimage.com/set/".concat(mSet).concat("/").concat(card.getImageName());
        Picasso.with(mContext).load(imageURL.concat("-crop.hq.jpg")).fit().centerCrop().into(cardImage);
        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onImageClicked(imageURL);
            }
        });
        cardImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.onImageLongClicked(imageURL.concat("-crop.hq.jpg"));
                return true;
            }
        });
        String rarity = card.getRarity();
        ImageView textRarity = (ImageView) view.findViewById(R.id.card_view_rarity);

        switch (rarity.charAt(0)) {
            case 'C': //common - black
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/" + mSet + "/" + Character.toLowerCase(rarity.charAt(0)) + "/48.png").into(textRarity);
                break;
            case 'U': //uncommon - silver
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/" + mSet + "/" + Character.toLowerCase(rarity.charAt(0)) + "/48.png").into(textRarity);
                break;
            case 'R':
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/" + mSet + "/" + Character.toLowerCase(rarity.charAt(0)) + "/48.png").into(textRarity);
                break;
            case 'M':
                Picasso.with(mContext).load("http://mtgimage.com/actual/symbol/set/" + mSet + "/" + Character.toLowerCase(rarity.charAt(0)) + "/48.png").into(textRarity);
                break;
        }
        //endregion
        //region cardInfo
        String type = card.getType();
        TextView subtypeText = (TextView) view.findViewById(R.id.card_list_subtype);
        subtypeText.setText(type);
        subtypeText.setTextColor(parent.getResources().getColor(R.color.black));
        subtypeText.setMaxLines(1);

        TextView contentText = (TextView) view.findViewById(R.id.card_view_text);
        contentText.setText(symbolGetter.formatStringWithGlyphs(card.getText(), imgGetter));
        contentText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView flavorText = (TextView) view.findViewById(R.id.card_view_flavortext);
        flavorText.setText(card.getFlavor());
        flavorText.setTextColor(parent.getResources().getColor(R.color.black));

        TextView powertough = (TextView) view.findViewById(R.id.card_view_powertoughness);
        if (!(card.getPower() == null)) {
            powertough.setText(card.getPower() + "/" + card.getToughness());
            powertough.setTextColor(parent.getResources().getColor(R.color.black));
        }
        //endregion
        if(card.getLayout().equals("double-faced")){
            Button button = (Button)view.findViewById(R.id.transform);
            button.setVisibility(View.VISIBLE);
            int size = card.getNames().size();
            int idx = card.getNames().indexOf(card.getName());
            int newidx = (idx+size+1)%size;
            final String transform = card.getNames().get(newidx);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTransform(transform);
                }
            });
        }
        //region prices
        TextView lowCost = (TextView) view.findViewById(R.id.card_view_lowcost);
        TextView avgCost = (TextView) view.findViewById(R.id.card_view_avgcost);
        TextView highCost = (TextView) view.findViewById(R.id.card_view_highcost);
        lowCost.setTextColor(Color.WHITE);
        avgCost.setTextColor(Color.WHITE);
        highCost.setTextColor(Color.WHITE);

        if (card.getMedPrice() == null) {
            lowCost.setVisibility(View.INVISIBLE);
            avgCost.setText("price not available");
            highCost.setVisibility(View.INVISIBLE);
        } else {
            lowCost.setText("Low: " + card.getLowPrice());
            avgCost.setText("Average: " + card.getMedPrice());
            highCost.setText("High: " + card.getHighPrice());
        }
        //endregion
        //region debug
        if (view.isInEditMode()) {
            text.setText("Name of Card");
            text.setBackgroundColor(parent.getResources().getColor(R.color.gold));
            textRarity.setVisibility(View.INVISIBLE);
        }
        //endregion
        return view;
    }

    public void onImageClicked(String image) {
        if (mListener != null) {
            mListener.onImageClicked(image);
        }
    }

    public void onImageLongClicked(String image){
        if(mListener != null){
            mListener.onImageLongClicked(image);
        }
    }

    public void onTransform(String card){
        if(mListener != null){
            mListener.onTransform(card);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
        try {
            mListener = (OnCardViewFragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCardViewFragmentInteraction");
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnCardViewFragmentInteraction {
        // TODO: Update argument type and name
        public void onImageClicked(String image);
        public void onImageLongClicked(String image);
        public void onTransform(String card);
    }

}
