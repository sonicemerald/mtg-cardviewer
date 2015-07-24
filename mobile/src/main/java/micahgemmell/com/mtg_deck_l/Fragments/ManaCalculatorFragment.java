package micahgemmell.com.mtg_deck_l.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import micahgemmell.com.mtg_deck_l.R;

/**
 * Created by sonicemerald on 7/3/15.
 */
public class ManaCalculatorFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    Context mContext;
    private Spinner deckTypeSpinner;
    private ArrayAdapter<String> adapterfordeckTypeSpinner;
    private AdapterView.OnItemSelectedListener listener;
    private ManaCalcInterface iListener;

    public ManaCalculatorFragment() {
        // Required empty public constructor
    }

    public static ManaCalculatorFragment newInstance() {
        return new ManaCalculatorFragment();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        if ((activity instanceof ManaCalcInterface)) {
            this.iListener = ((ManaCalcInterface) activity);
            return;
        }
        throw new ClassCastException(activity.toString() + " is lame!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

//        Html.ImageGetter imgGetter = symbolGetter.GlyphGetter(parent.getResources());
        // Inflate the layout for this fragment
        View localview = inflater.inflate(R.layout.fragment_mana_calculator, parent, false);
        this.deckTypeSpinner = (Spinner) localview.findViewById(R.id.deckTypeSpinner);
        String[] type_array = getResources().getStringArray(R.array.manacalculator);
        adapterfordeckTypeSpinner = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, type_array);
        this.deckTypeSpinner.setAdapter(adapterfordeckTypeSpinner);

        listener = this;
        this.deckTypeSpinner.post(new Runnable() {
            @Override
            public void run() {
                deckTypeSpinner.setOnItemSelectedListener(listener);
                deckTypeSpinner.setSelection(0);
            }
        });

//        Button b = (Button) localview.findViewById(R.id.button_calculate);
//        b.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
//                iListener.calculateMana();
//            }
//        });

//        iListener = this;
//        this.sortSetSpinner.post(new Runnable() {
//            public void run() {
//                sortSetSpinner.setOnItemSelectedListener(listener);
//                int initialposition = (adapterforSetArray.getCount() - 1);
//                int spinnerposition = mListener.getSpinnerPosition();
//                //if(mListener.getSpinnerPosition() = initialposition) { spinnerposition = initialposition; }
//                sortSetSpinner.setSelection(spinnerposition);
//            }
//        });

        return localview;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.iListener.spinnerItemSelected(position, parent.getId());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public static abstract interface ManaCalcInterface {
        public abstract void spinnerItemSelected(int position, int id);
        //public abstract void calculateMana();
    }
}
