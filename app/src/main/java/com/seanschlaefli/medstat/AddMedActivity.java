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

    public static Intent newIntent(Context packageContext, UUID medId) {
        Intent intent = new Intent(packageContext, AddMedActivity.class);
        intent.putExtra(EXTRA_MED_ID, medId);
        return intent;
    }

    public Fragment createFragment() {
        return new AddMedFragment();
    }

}
