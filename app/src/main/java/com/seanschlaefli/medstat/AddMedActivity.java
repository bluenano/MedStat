package com.seanschlaefli.medstat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class AddMedActivity extends SingleFragmentActivity {

    private static String EXTRA_MED_ID =
            "com.seanschlaefli.medstat.med_id";
    public static String EXTRA_NAME =
            "com.seanschlaefli.medstat.name";
    private static String EXTRA_BUNDLE =
            "com.seanschlaefli.medstat.bundle_name";

    public static Intent newIntent(Context packageContext, String name) {
        Intent intent = new Intent(packageContext, AddMedActivity.class);
        Bundle args = new Bundle();
        args.putString(EXTRA_NAME, name);
        intent.putExtra(EXTRA_BUNDLE, args);
        return intent;
    }

    public Fragment createFragment() {
       return new AddMedFragment();
    }

    @Override
    public String getBundleName() {
        return AddMedActivity.EXTRA_BUNDLE;
    }

}
