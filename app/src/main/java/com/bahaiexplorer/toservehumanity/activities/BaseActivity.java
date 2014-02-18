package com.bahaiexplorer.toservehumanity.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.model.ConfigObjects;

public class BaseActivity extends ActionBarActivity {
    private static final String TAG = "BaseActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final ToServeHumanityApplication mApp = (ToServeHumanityApplication)getApplication();
        final ConfigObjects.ConfigObject.Strings strings = mApp.getStrings();
        if(strings != null){
            // replace the text with the languages options
            for(int i=0;i< menu.size();i++){
                MenuItem mi = menu.getItem(i);
                int id = mi.getItemId();
                switch(id){
                    case R.id.action_about:
                        mi.setTitle(strings.titleAbout);
                        break;
                    case R.id.action_language:
                        mi.setTitle(strings.titleLanguage);
                        break;
                    case R.id.action_gotowebsite:
                        mi.setTitle(strings.titleGoToWebsite);
                        break;
                    case R.id.action_terms_of_use:
                        mi.setTitle(strings.titleTerms);
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            showAbout();
            return true;
        }
        else if (id == R.id.action_terms_of_use) {
            showTerms();
            return true;
        }
        else if (id == R.id.action_gotowebsite) {
            gotoWebsite();
            return true;
        }
        else if (id == R.id.action_feedback) {
            sendFeedback();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAbout(){
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    public void showTerms(){
       Intent intent = new Intent(this,TermsActivity.class);
       startActivity(intent);
    }

    public void gotoWebsite(){
        final ToServeHumanityApplication mApp = (ToServeHumanityApplication)getApplication();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mApp.currLanguageConfig.website) );
        startActivity(intent);
    }

    public void sendFeedback(){
        final ToServeHumanityApplication mApp = (ToServeHumanityApplication)getApplication();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                getResources().getString(R.string.feedback_subject));
        sharingIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[] {getResources().getString(R.string.feedback_email)});
        this.startActivity(Intent.createChooser(sharingIntent,
                mApp.getStrings().titleShare));
    }


}
