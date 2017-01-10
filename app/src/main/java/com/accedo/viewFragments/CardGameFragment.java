package com.accedo.viewFragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.accedo.colorMemory.ColorAlert;
import com.accedo.colorMemory.GameActivity;
import com.accedo.colorMemory.R;
import com.accedo.gameController.ImageCardAdapter;
import com.accedo.gameModel.HighScore;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Vishal Nigam on 08-10-2016.
 */
public class CardGameFragment extends Fragment implements ImageCardAdapter.GameEvents{

    private static final int DELAY_DURATION = 1000;
    private static final int DELAY_LONG = 1500;

    private static final int INTRO_DURATION = 600;
    private static final String GAME_PREFERENCE = "PREFERENCE_COLOUR_MEMORY";
    private TextView gameScore;
    private TextView currentScore;
    private long gameScoreCount;
    private ImageCardAdapter cardAdapter;
    private View rootView;
    private boolean gameOver;
    private boolean openDialog;

    public interface GameOverResult {
        public boolean onResult(final String username);
    }

    public CardGameFragment() {
        rootView = null;
        openDialog = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            gameOver = false;
            SharedPreferences preferences = getActivity().getSharedPreferences(GAME_PREFERENCE, Context.MODE_PRIVATE);
            rootView = inflater.inflate(R.layout.activity_game, container, false);
            gameScore = (TextView) rootView.findViewById(R.id.textViewGameScore);
            currentScore = (TextView) rootView.findViewById(R.id.textViewCurrentScore);
            cardAdapter = new ImageCardAdapter((AppCompatActivity) getActivity());
            gameScoreCount = preferences.getLong("CurrentGameScore", 0);
            cardAdapter.populateCards(false);
            cardAdapter.setGameEvents(this);
            currentScore.setVisibility(View.INVISIBLE);
            gameScore.setVisibility(View.INVISIBLE);

            gameScore.setText(getResources().getString(R.string.score) + " " + gameScoreCount);
            initGameField(rootView);
            initGameButtons(rootView);
        }

        ((GameActivity)getActivity()).rotateScreenPermission(false);
        return rootView;
    }

    protected void initGameField(View root_view) {
        final GridView cardsView = (GridView) root_view.findViewById(R.id.gridViewCards);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                cardsView.setAdapter(cardAdapter);
            }
        }, INTRO_DURATION);


        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                gameScore.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.DropOut)
                        .duration(DELAY_DURATION)
                        .playOn(gameScore);
            }
        }, DELAY_LONG);
    }

    protected void initGameButtons(View root_view) {
        final ImageButton high_score = (ImageButton) root_view.findViewById(R.id.imageButtonHighScore);
        final ImageButton restart = (ImageButton) root_view.findViewById(R.id.imageButtonRestart);
        final ImageButton quit = (ImageButton) root_view.findViewById(R.id.imageButtonQuit);

        high_score.setOnClickListener(new View.OnClickListener() {
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

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                YoYo.with(Techniques.Pulse)
                        .duration(INTRO_DURATION)
                        .playOn(v);
                if(openDialog)
                    return;
                openDialog = true;
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ColorAlert.getInstance().Ask(getActivity(), getResources().getString(R.string.restart),
                                getResources().getString(R.string.restart_message), R.drawable.replay_btn_press,
                                getResources().getString(R.string.button_yes), getResources().getString(R.string.button_no), new Runnable() {
                                    @Override
                                    public void run() {
                                        openDialog = false;
                                        resetGame();
                                    }
                                }, new Runnable() {
                                    @Override
                                    public void run() {
                                        openDialog = false;
                                    }
                                });
                    }
                }
                        , INTRO_DURATION);
            }
        });

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse)
                        .duration(INTRO_DURATION)
                        .playOn(v);
                if(openDialog)
                    return;
                openDialog = true;
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ColorAlert.getInstance().Ask(getActivity(),getResources().getString(R.string.quit),
                                getResources().getString(R.string.quit_message) , R.drawable.close,
                                getResources().getString(R.string.button_yes), getResources().getString(R.string.button_no), new Runnable() {
                                    @Override
                                    public void run() {
                                        cardAdapter.populateCards(true);
                                        gameScoreCount = 0;
                                        saveScore();
                                        getActivity().finish();
                                        System.exit(0);
                                    }
                                }, new Runnable() {
                                    @Override
                                    public void run() {
                                        openDialog = false;
                                    }
                                });
                    }
                }
                        , INTRO_DURATION);
            }
        });
    }

    @Override
    public void gameOver() {
        gameOver = true;
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showGameOverDialog(new GameOverResult() {
                    @Override
                    public boolean onResult(final String username) {
                        final RealmConfiguration realmConfig = new RealmConfiguration.Builder(getActivity()).build();
                        final Realm db = Realm.getInstance(realmConfig);
                        HighScore score = db.where(HighScore.class).equalTo("name", username).findFirst();
                        if (score == null) {
                            db.beginTransaction();
                            score = db.createObject(HighScore.class);
                            score.setName(username);
                            score.setScore(gameScoreCount);
                            score.setRank(HighScore.getRank(gameScoreCount));
                            db.commitTransaction();
                        } else
                            return false;
                        resetGame();
                        ((GameActivity) getActivity()).flipScreen();
                        return true;
                    }
                });
            }
        }, INTRO_DURATION);
    }

    @Override
    public void onDestroy() {
        if(rootView != null && rootView.getParent() != null) {
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!gameOver && cardAdapter.gameEnded())
            gameOver();
    }

    @Override
    public void onPause() {
        cardAdapter.saveState();
        saveScore();
        super.onPause();
    }

    @Override
    public void result(final boolean matched) {
        currentScore.setVisibility(View.VISIBLE);
        if (matched) {
            currentScore.setText("+2");
            gameScoreCount += 2;
        } else {
            currentScore.setText("-1");
            gameScoreCount--;
        }

        YoYo.with(Techniques.FadeOutUp)
                .duration(DELAY_LONG)
                .playOn(currentScore);

        (new Handler()).postDelayed(new Runnable() {

            @Override
            public void run() {
                gameScore.setText(getResources().getString(R.string.score) + gameScoreCount);
                if (matched)
                    YoYo.with(Techniques.Tada)
                            .duration(DELAY_DURATION)
                            .playOn(gameScore);
                else
                    YoYo.with(Techniques.Flash)
                            .duration(DELAY_DURATION)
                            .playOn(gameScore);
            }
        }, DELAY_DURATION);

    }

    public void saveScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(GAME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("CurrentGameScore", gameScoreCount);
        editor.commit();
    }

    public void resetGame() {
        cardAdapter.populateCards(true);
        cardAdapter.notifyDataSetChanged();
        gameScoreCount = 0;
        gameScore.setText(getResources().getString(R.string.score) + gameScoreCount);
    }

    public void showGameOverDialog(final GameOverResult result) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float padding = ((float)size.x/100f)*6f;

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.game_over));
        alert.setMessage(getResources().getString(R.string.game_score) + gameScoreCount + getResources().getString(R.string.user_name));
        final EditText username = new EditText(getActivity());
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(25);
        username.setFilters(filters);
        username.setFocusable(true);
        username.setHint("Username...");
        username.setSingleLine();
        FrameLayout container = new FrameLayout(getActivity());
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(Math.round(padding), Math.round(padding), Math.round(padding), Math.round(padding));
        username.setLayoutParams(params);
        container.addView(username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username.setError(null);
            }
        });
        alert.setView(container);
        alert.setPositiveButton(getResources().getString(R.string.button_ok), null);
        alert.setNegativeButton(null, null);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dlg) {

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        String input = username.getText().toString().trim();
                        if (input.length() == 0) {
                            username.setError(getResources().getString(R.string.error_user));
                        }
                        else {
                            gameOver = false;
                            if (result != null){
                                if(result.onResult(input))
                                    dialog.dismiss();
                                else
                                    username.setError(getResources().getString(R.string.error_user_exist));
                            }
                            else
                                dialog.dismiss();

                        }
                    }
                });
            }
        });
        dialog.show();
    }
}
