package com.accedo.viewFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accedo.colorMemory.ColorAlert;
import com.accedo.colorMemory.GameActivity;
import com.accedo.colorMemory.R;
import com.accedo.gameController.HighScoreAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.inqbarna.tablefixheaders.TableFixHeaders;

/**
 * Created by Vishal Nigam on 10-10-2016.
 */
public class HighScoreFragment extends Fragment {

    private View rootView;
    private HighScoreAdapter highscoreTableAdapter;
    private static final int INTRO_DURATION = 600;

    public HighScoreFragment() {
        rootView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.high_score_table, container, false);
            final TextView header = (TextView) rootView.findViewById(R.id.textViewHeaderHighScore);
            final TextView message =  (TextView) rootView.findViewById(R.id.textViewEmtyTable);
            final TableFixHeaders highscore_table = (TableFixHeaders) rootView.findViewById(R.id.tableHighScore);
            highscoreTableAdapter = new HighScoreAdapter((AppCompatActivity)getActivity());
            if(highscoreTableAdapter.populateScore())
                message.setVisibility(View.GONE);
            else
                highscore_table.setVisibility(View.INVISIBLE);
            highscore_table.setAdapter(highscoreTableAdapter);
            initGameButtons(rootView);
        }
        ((GameActivity)getActivity()).rotateScreenPermission(true);
        return rootView;
    }

    @Override
    public void onDestroy() {
        if(rootView != null && rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        super.onDestroy();
    }

    protected void initGameButtons(View root_view) {
        final ImageButton home = (ImageButton)root_view.findViewById(R.id.imageButtonHome);
        final ImageButton close = (ImageButton)root_view.findViewById(R.id.imageButtonClose);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse)
                        .duration(INTRO_DURATION)
                        .playOn(v);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final GameActivity parent = (GameActivity)getActivity();
                        if(parent != null && parent.isCanSwitchView()) {
                            parent.setCanSwitchView(false);
                            parent.flipScreen();
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    parent.setCanSwitchView(true);
                                }
                            }, INTRO_DURATION);
                        }
                    }
                }, INTRO_DURATION);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse)
                        .duration(INTRO_DURATION)
                        .playOn(v);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ColorAlert.getInstance().Ask(getActivity(), "Quit the game", "Do you want to quit the game?", R.drawable.close,
                               "Yes", "No", new Runnable() {
                                   @Override
                                   public void run() {
                                       getActivity().finish();
                                       System.exit(0);
                                   }
                               }, null);
                    }
                }
                        , INTRO_DURATION);
            }
        });
    }
}
