package com.pramont.myspeech;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private TextView    mSpeechInputTextView;
    private ImageButton mMicImageButton;
    private Switch      mLangSwitch;
    private String      mLanguageString;
    private final int   REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpeechInputTextView    = (TextView) findViewById(R.id.txtSpeechInput);
        mMicImageButton         = (ImageButton) findViewById(R.id.btnSpeak);
        mLangSwitch             = (Switch) findViewById(R.id.lang);

        //default lang English
        mLanguageString = getString(R.string.en);
        mLangSwitch.setText(getString(R.string.lang));

        mLangSwitch.setOnCheckedChangeListener(this);
        mMicImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSpeak:
                promptSpeechInput();
                break;
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mLanguageString);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,getString(R.string.speech_prompt));

        try
        {
            startActivityForResult(intent,REQ_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException activityError)
        {
            Toast.makeText(getApplicationContext(),getString(R.string.speech_not_supported),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
                if(resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mSpeechInputTextView.setText(result.get(0));
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked)
        {
            //Spanish
            mLangSwitch.setText(getString(R.string.lang2));
            mLanguageString = getString(R.string.es);
            mSpeechInputTextView.setText("");
        }
        else
        {
            //English
            mLangSwitch.setText(getString(R.string.lang));
            mLanguageString = getString(R.string.en);
            mSpeechInputTextView.setText("");
        }

    }
}
