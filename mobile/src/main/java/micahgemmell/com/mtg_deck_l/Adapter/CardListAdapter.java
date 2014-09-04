package micahgemmell.com.mtg_deck_l.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import micahgemmell.com.mtg_deck_l.Card.Card;
import micahgemmell.com.mtg_deck_l.R;

import java.util.List;


public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> {

    private List<Card> mCards_data;
    private static Context sContext;

    // Adapter's Constructor
    public CardListAdapter(Context sContext, List<Card> cards_data) {
        this.mCards_data = cards_data;
        this.sContext = sContext;
    }
    public void setCards(List<Card> cards_data){
        this.mCards_data = cards_data;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //The viewholder is like a middleman between the RecyclerView and the View.
        //It translates the data from the dataset to the view.

        public TextView mCardName;
        public TextView mCardType;
        public TextView mCardManaCost;
        public TextView mCardRarity;
        public View mCardView;

        public vHOnClickInterface mListener;

        public ViewHolder(View itemView, vHOnClickInterface listener) {
            super(itemView);
            mListener = listener;
            mCardName = (TextView) itemView.findViewById(R.id.cardName_tv);
            mCardType = (TextView) itemView.findViewById(R.id.subtype_tv);
            mCardRarity = (TextView) itemView.findViewById(R.id.rarity_tv);
            mCardView = itemView.findViewById(R.id.card_view);
            //mCardName.setOnClickListener(this);
            itemView.setOnClickListener(this);

            //mCardDetails = (ImageView) itemView.findViewById(R.id.cardDetails);
            //mCardImage = (ImageView) itemView.findViewById(R.id.buttonforCardImage);

        }
        @Override
        public void onClick(View view){
            if(view instanceof TextView){
                mListener.onCardName(view);
            } else if (view != null){
                mListener.onViewClicked(view);
            }

        }
        public interface vHOnClickInterface {
            public void onCardName(View caller);
            public void onViewClicked(View caller);
        }

    }

    @Override public CardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Create a new view by inflating the row item xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_row, parent, false);
        //Set the view to the ViewHolder
        ViewHolder holder = new ViewHolder(v, new ViewHolder.vHOnClickInterface() {
            public void onCardName(View caller){
                Toast.makeText(sContext,"Clicked", Toast.LENGTH_SHORT).show();
            }
            public void onViewClicked(View caller){
                Toast.makeText(sContext, "View CLicked", Toast.LENGTH_SHORT).show();
            }
        });
//        holder.mCardName.setOnClickListener(this);
//        holder.mCardName.setOnClickListener(Card);
        return holder;
    }

    // Replace the contents of a view, This is invoked by the LayoutManager
    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        if(getItemCount() == 0){
            return;
        }
        Card card = mCards_data.get(position);
        holder.mCardName.setText(card.getName());

        String cardColor = new String();
        if(card.getColors().size()>0){
            cardColor = card.getColors().get(0);
        } else {
            cardColor = "Colorless";
        }

        if (cardColor.equals("Blue")) {
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardView.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.blue));
        } else if (cardColor.equals("Green")){
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardView.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.green));
        } else if (cardColor.equals("White")) {
            holder.mCardName.setTextColor(Color.BLACK);
            holder.mCardView.setBackgroundColor(Color.WHITE);
        } else if (cardColor.equals("Black")) {
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardView.setBackgroundColor(Color.BLACK);
        } else if (cardColor.equals("Red")) {
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardView.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.red));
        } else {
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardView.setBackgroundColor(Color.GRAY);
        }

        holder.mCardType.setText(card.getType());
//      holder.mCardManaCost.setText(card.getManacost());

        holder.itemView.setTag(card);
    }

    @Override public int getItemCount() {
        return mCards_data.size();
    }

    public void add(Card card){
        mCards_data.add(card);
        notifyDataSetChanged();
    }

    public void addAll(List cards){
        mCards_data.addAll(cards);
        notifyDataSetChanged();
    }

    public void remove(Card card, int position){
        mCards_data.remove(card);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void clear(){
        mCards_data.clear();
        notifyDataSetChanged();
    }
    public void removeAll(List cards){
        mCards_data.removeAll(cards);
        notifyDataSetChanged();
    }
}