package micahgemmell.com.mtg_deck_l;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WearActivity extends Activity {

    private TextView mTextView;
    private Button mAddBtn;
    private Button mSubBtn;
    private int mLifeTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);

        mLifeTotal = 20;


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                mTextView = (TextView) stub.findViewById(R.id.text);
                mAddBtn = (Button) stub.findViewById(R.id.addLifeButton);
                mSubBtn = (Button) stub.findViewById(R.id.subtractLifeButton);
                mTextView.setText(String.valueOf(mLifeTotal));
                mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //reset life;
                        mLifeTotal = 20;
                        mTextView.setText(String.valueOf(mLifeTotal));
                    }
                });
                mAddBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLifeTotal++;
                        mTextView.setText(String.valueOf(mLifeTotal));
                    }
                });

                mAddBtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mLifeTotal += 5;
                        mTextView.setText(String.valueOf(mLifeTotal));
                        return true;
                    }
                });

                mSubBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mLifeTotal--;
                        mTextView.setText(String.valueOf(mLifeTotal));
                    }
                });

                mSubBtn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mLifeTotal -= 5;
                        mTextView.setText(String.valueOf(mLifeTotal));
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onPause(){

    }
}
