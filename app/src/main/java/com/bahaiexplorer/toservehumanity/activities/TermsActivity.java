package com.bahaiexplorer.toservehumanity.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;

public class TermsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            ToServeHumanityApplication app = (ToServeHumanityApplication)getActivity()
                    .getApplication();
            View rootView = inflater.inflate(R.layout.fragment_terms, container, false);
            TextView tvTerms = (TextView)rootView.findViewById(R.id.tv_terms);
            String terms = app.currLanguageConfig.strings.textTerms;
            tvTerms.setText(terms);
            return rootView;
        }
    }

}
