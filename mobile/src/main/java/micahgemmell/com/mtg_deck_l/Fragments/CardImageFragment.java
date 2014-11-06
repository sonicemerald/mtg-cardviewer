package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//import com.squareup.picasso.Picasso;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;

public class CardImageFragment extends Fragment
{
    Context context;
    private Card cards;
    private Bitmap itemBitmap;
    private ImageView itemImageView;

    //"Constructor" for Fragments;
    public static CardImageFragment newInstance(Card card){
        CardImageFragment f = new CardImageFragment();
        f.cards = card;
        return f;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.context = activity;
    }


    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View localView = paramLayoutInflater.inflate(R.layout.card_image_view, paramViewGroup, false);
        this.itemImageView = ((ImageView)localView.findViewById(R.id.imageView));

        if (this.itemBitmap != null) {
            this.itemImageView.setImageBitmap(this.itemBitmap);
        }
        return localView;
    }

    public void setImageView(Bitmap paramBitmap)
    {
        this.itemBitmap = paramBitmap;
        this.itemImageView.setImageBitmap(paramBitmap);
    }
}
