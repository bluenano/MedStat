package com.seanschlaefli.medstat;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


// Spinner is a widget to create a drop-down list
public class HomeActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return new HomeFragment();
    }


}
