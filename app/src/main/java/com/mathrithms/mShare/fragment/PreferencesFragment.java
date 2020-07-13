package com.mathrithms.mShare.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.mathrithms.mShare.R;

public class PreferencesFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        addPreferencesFromResource(R.xml.preferences_main_app);

        if (Build.VERSION.SDK_INT < 26)
            addPreferencesFromResource(R.xml.preferences_main_notification);
        else
            addPreferencesFromResource(R.xml.preferences_main_notification_oreo);
    }
}