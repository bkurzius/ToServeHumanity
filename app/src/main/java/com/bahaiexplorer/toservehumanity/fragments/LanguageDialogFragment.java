package com.bahaiexplorer.toservehumanity.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.bahaiexplorer.toservehumanity.R;
import com.bahaiexplorer.toservehumanity.ToServeHumanityApplication;
import com.bahaiexplorer.toservehumanity.activities.MainActivity;
import com.bahaiexplorer.toservehumanity.model.Constants;
import com.bahaiexplorer.toservehumanity.model.Language;

import java.util.ArrayList;

/**
 * Created by briankurzius on 2/16/14.
 */
public class LanguageDialogFragment extends DialogFragment {
    private static final String TAG = "LanguageDialogFragment";
    private int selectedLanguage;
    ArrayList<Language> langArray;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ToServeHumanityApplication app = (ToServeHumanityApplication)getActivity()
                .getApplication();
        langArray = app.getLanguageArray();
        ArrayList<String> strLangArray = new ArrayList<String>();
        for(Language lang:langArray){
            strLangArray.add(lang.name);
        }
        final CharSequence[] charSequenceItems = strLangArray.toArray(new CharSequence[strLangArray.size()]);
        Log.d(TAG,"languageindex:" + app.getLanguagePreferenceIndex() );
        builder.setTitle(R.string.title_select_language)
                .setSingleChoiceItems(charSequenceItems, app.getLanguagePreferenceIndex() ,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                selectedLanguage = which;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "the language was chosen");
                        MainActivity act = (MainActivity) getActivity();
                        act.setLanguage(langArray.get(selectedLanguage).id);
                        // analytics
                        ((ToServeHumanityApplication)getActivity().getApplication()).trackEvent
                                (Constants
                                .TRACK_EVENT_TYPE_CHANGE_LANGUAGE,langArray.get(selectedLanguage).id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public void setLanguage(int language){
        selectedLanguage = language;

    }

}
