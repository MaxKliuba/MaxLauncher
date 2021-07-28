package com.maxclub.android.maxlauncher;

import androidx.fragment.app.Fragment;

public class MaxLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MaxLauncherFragment.newInstance();
    }
}