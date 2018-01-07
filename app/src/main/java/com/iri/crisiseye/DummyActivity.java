package com.iri.crisiseye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by tsarkar on 07/10/17.
 */
public class DummyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent login_activity = new Intent(this, LoginActivity.class);
        startActivity(login_activity);
    }
}
