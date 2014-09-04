package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import micahgemmell.com.mtg_deck_l.Card.oldCard;
import micahgemmell.com.mtg_deck_l.R;

public class CardImageFragment
        extends Fragment
{
    private oldCard cards;
    private Bitmap itemBitmap;
    private ImageView itemImageView;

    public CardImageFragment(oldCard paramOldCard)
    {
        this.cards = paramOldCard;
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
