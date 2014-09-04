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
            mCardView = itemView.findViewById(R.id.CardListRow);
            //mCardName.setOnClickListener(this);
            itemView.setOnClickListener(this);

            //mCardDetails = (ImageView) itemView.findViewById(R.id.cardDetails);
            //mCardImage = (ImageView) itemView.findViewById(R.id.buttonforCardImage);

        }
        @Override
        public void onClick(View view){
            if(view instanceof TextView){
                mListener.onCardName(view);
            } else if (view instanceof View){
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
//        holder.mCardName.setOnClickListener(oldCard);
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
            holder.mCardName.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.blue));
        } else if (cardColor.equals("Green")){
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardName.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.green));
        } else if (cardColor.equals("White")) {
            holder.mCardName.setBackgroundColor(Color.WHITE);
            holder.mCardName.setTextColor(Color.BLACK);
        } else if (cardColor.equals("Black")) {
            holder.mCardName.setBackgroundColor(Color.BLACK);
            holder.mCardName.setTextColor(Color.WHITE);
        } else if (cardColor.equals("Red")) {
            holder.mCardName.setBackgroundColor(holder.mCardView.getResources().getColor(R.color.red));
            holder.mCardName.setTextColor(Color.WHITE);
        } else {
            holder.mCardName.setTextColor(Color.WHITE);
            holder.mCardName.setBackgroundColor(Color.GRAY);
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

//adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener<ViewModel>() {
//@Override public void onItemClick(View view, ViewModel viewModel) {
//        // Do something
//        }
//        });
//
//public interface OnRecyclerViewItemClickListener<Model> {
//    public void onItemClick(View view, Model model);
//}
//
//    public void setOnItemClickListener(OnRecyclerViewItemClickListener<ViewModel> listener) {
//        this.itemClickListener = listener;
//    }
//}
//public class CardListAdapter extends ArrayAdapter<oldCard> {
//    //CardListAdapter takes a List of cards, and adapts them to the view that they will be displayed in.
//    public CardListAdapter(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//    }
//    private List<oldCard> cards;
//
//    public CardListAdapter(Context context, int resource, List<oldCard> cards) {
//        super(context, resource, cards);
//        this.cards = cards;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        View view = convertView;
//       // View view = super.getView(position, convertView, parent);
//
//        if (view == null) {
//
//            LayoutInflater inflater;
//            inflater = LayoutInflater.from(getContext());
//            view = inflater.inflate(R.layout.card_list_row, null);
//
//        }
//
//        oldCard card = this.getItem(position);
//
//
//        //Dealing with card color
//        TextView text = (TextView) view.findViewById(R.id.cardName_tv);
//        String cardColor = new String();
//        text.setText(card.getName());
//        if (card.getColor().size()>0) {
//            cardColor = this.getItem(position).getColor().get(0);
//        }
//        else {
//            cardColor = "Other";
//        }
//
//        if (cardColor.equals("Blue")) {
//            text.setTextColor(Color.WHITE);
//            text.setBackgroundColor(parent.getResources().getColor(R.color.blue));
//        } else if (cardColor.equals("Green")){
//            text.setTextColor(Color.WHITE);
//            text.setBackgroundColor(parent.getResources().getColor(R.color.green));
//        } else if (cardColor.equals("White")) {
//            text.setBackgroundColor(Color.WHITE);
//            text.setTextColor(Color.BLACK);
//        } else if (cardColor.equals("Black")) {
//            text.setBackgroundColor(Color.BLACK);
//            text.setTextColor(Color.WHITE);
//        } else if (cardColor.equals("Red")) {
//            text.setBackgroundColor(parent.getResources().getColor(R.color.red));
//            text.setTextColor(Color.WHITE);
//        } else {
//            text.setTextColor(Color.WHITE);
//            text.setBackgroundColor(Color.GRAY);
//        }
//
//        String rarity = card.getRarity();
//        TextView textRarity = (TextView) view.findViewById(R.id.rarity_tv);
//
//        switch(rarity.charAt(0)){
//            case 'C': //common - black
//                textRarity.setText("C");
//                textRarity.setTextColor(parent.getResources().getColor(R.color.gold));
//                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.black));
//                break;
//            case 'U': //uncommon - silver
//                textRarity.setText("U");
//                textRarity.setTextColor(parent.getResources().getColor(R.color.black));
//                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.silver));
//                break;
//            case 'R':
//                textRarity.setText("R");
//                textRarity.setTextColor(parent.getResources().getColor(R.color.black));
//                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.gold));
//                break;
//            case 'M':
//                textRarity.setText("MR");
//                textRarity.setTextColor(parent.getResources().getColor(R.color.silver));
//                textRarity.setBackgroundColor(parent.getResources().getColor(R.color.orangered));
//               break;
//        }
//
//        String type = card.getType();
//
//
//            TextView subtypeText = (TextView) view.findViewById(R.id.subtype_tv);
//            subtypeText.setText(type);
//
//        return view;
//    }
//
//};
