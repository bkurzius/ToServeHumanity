package com.bahaiexplorer.toservehumanity.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bahaiexplorer.toservehumanity.R;

public class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            showAbout();
            return true;
        }
        if (id == R.id.action_terms_of_use) {
            showTerms();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAbout(){
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    public void showTerms(){
       /* Intent intent = new Intent(this,TermsActivity.class);
        startActivity(intent);*/
    }








}
