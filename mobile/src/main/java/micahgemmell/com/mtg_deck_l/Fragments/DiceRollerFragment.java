package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import micahgemmell.com.mtg_deck_l.R;

// Created by Nick Mahnke on 12/7/13.

public class DiceRollerFragment extends Fragment {

    Context context;
    OnDiceRoll mListener;
    Button rb10;
    Button rb12;
    Button rb20;
    Button loseLifeButton;
    Button addLifeButton;

    View.OnClickListener rb10Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.diceRoller(10);

        }
    };

    View.OnClickListener rb12Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.diceRoller(12);
        }
    };

    View.OnClickListener rb20Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.diceRoller(20);
        }
    };

    View.OnClickListener loseLifeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.diceRoller(50);
        }
    };

    View.OnClickListener addLifeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.diceRoller(60);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View localView = inflater.inflate(R.layout.dice_roller_layout, container, false);
        this.rb10 = ((Button)localView.findViewById(R.id.Button10S));
        this.rb12 = ((Button)localView.findViewById(R.id.Button12S));
        this.rb20 = ((Button)localView.findViewById(R.id.Button20S));
        this.loseLifeButton = ((Button)localView.findViewById(R.id.loseLifeButton));
        this.addLifeButton = ((Button)localView.findViewById(R.id.addLifeButton));
        this.rb10.setOnClickListener(rb10Listener);
        this.rb12.setOnClickListener(rb12Listener);
        this.rb20.setOnClickListener(rb20Listener);
        this.loseLifeButton.setOnClickListener(loseLifeButtonListener);
        this.addLifeButton.setOnClickListener(addLifeButtonListener);
        return localView;
    }

    public DiceRollerFragment()
    {
    }


    @Override
    public void onStart() {
        super.onStart();
        mListener.diceRollerInit();
    }

    public void onAttach(Activity paramActivity)
    {
        super.onAttach(paramActivity);
        this.context = paramActivity;
        if ((paramActivity instanceof OnDiceRoll))
        {
            this.mListener = ((OnDiceRoll)paramActivity);
            return;
        }
        //throw new ClassCastException(paramActivity.toString() + " is lame!");
    }

/*
    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        String calledBy = "set";
        this.mListener.onCardImageViewUpdate(paramInt, calledBy);
    }*/

    public static abstract interface OnDiceRoll
    {

        public abstract void diceRoller (int button);
        public abstract void diceRollerInit ();



    }

}
