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

import com.squareup.picasso.Picasso;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;

public class CardImageFragment extends Fragment
{
    private Context mContext;
    private String image;
    private ImageView itemImageView;
    private OnCardImageClicked mListener;

    //"Constructor" for Fragments;
    public static CardImageFragment newInstance(String card){
        CardImageFragment f = new CardImageFragment();
        f.image = card;
        return f;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.mContext = activity;
        try {
            mListener = (OnCardImageClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCardImageClicked");
        }
    }

    public void onImageClicked() {
        if (mListener != null) {
            mListener.onCardImageClicked();
        }
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
        View localView = paramLayoutInflater.inflate(R.layout.card_image_view, paramViewGroup, false);
        this.itemImageView = ((ImageView)localView.findViewById(R.id.imageView));
        this.itemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCardImageClicked();
            }
        });
        Picasso.with(mContext).load(image.concat(".hq.jpg")).into(this.itemImageView);
        return localView;
    }

    public interface OnCardImageClicked {
        // TODO: Update argument type and name
        public void onCardImageClicked();
    }

}
