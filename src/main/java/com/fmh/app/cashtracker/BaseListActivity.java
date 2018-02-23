package com.fmh.app.cashtracker;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by ralf on 07.02.18.
 */

public class BaseListActivity extends Activity {

    public void AnimateProgressBar(ProgressBar progressBar, int progressState ){

        if(android.os.Build.VERSION.SDK_INT >= 11) {
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", (int) progressState);
            animation.setDuration(1000); // 0.5 second
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
        }else{
            progressBar.setProgress((int) progressState);
        }

    }

}
