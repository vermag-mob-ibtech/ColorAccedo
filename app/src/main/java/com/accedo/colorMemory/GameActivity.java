package com.accedo.colorMemory;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.accedo.viewFragments.CardGameFragment;
import com.accedo.viewFragments.HighScoreFragment;
import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;

public class GameActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private CardGameFragment mGameFragment;

    private boolean mShowingHighScore = false;

    public boolean isCanSwitchView() {
        return canSwitchView;
    }

    public void setCanSwitchView(boolean canSwitchView) {
        this.canSwitchView = canSwitchView;
    }

    private boolean canSwitchView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.

            mGameFragment = new CardGameFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.fragment_place, mGameFragment);
            fragmentTransaction.commit();

        } else {
            mShowingHighScore = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    public void rotateScreenPermission(boolean allow) {
        if(allow)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void flipScreen() {
        if (mShowingHighScore) {
            getFragmentManager().popBackStack();
            return;
        }

        // Flip to the back.

        mShowingHighScore = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        HighScoreFragment highscoreFragment = new HighScoreFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, fragmentTransaction, mGameFragment, highscoreFragment, R.id.fragment_place);
        fragmentTransactionExtended.addTransition(FragmentTransactionExtended.GLIDE);
        fragmentTransactionExtended.commit();
    }




    @Override
    public void onBackStackChanged() {
        mShowingHighScore = (getFragmentManager().getBackStackEntryCount() > 0);
    }

}
