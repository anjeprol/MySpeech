package com.pramont.myspeech;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.pramont.myspeech.constants.PayPalSettings;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private final int   REQ_CODE_SPEECH_INPUT = 100;
    private TextView    mSpeechInputTextView;
    private ImageButton mMicImageButton;
    private ImageView   mDonateImageView;
    private Switch      mLangSwitch;
    private String      mLanguageString;
    private String      mDonationAmount;
    private AdView      mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDonationAmount = getString(R.string.amount);

        // Load an ad into the AdMob banner view.
        mAdView  = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                                            .setRequestAgent("android_studio:ad_template")
                                            .build();

        mSpeechInputTextView    = (TextView) findViewById(R.id.txtSpeechInput);
        mMicImageButton         = (ImageButton) findViewById(R.id.btnSpeak);
        mDonateImageView        = (ImageView) findViewById(R.id.donate);
        mLangSwitch             = (Switch) findViewById(R.id.lang);

        //default lang English
        mLanguageString = getString(R.string.en);
        mLangSwitch.setText(getString(R.string.lang));
        mAdView.loadAd(adRequest);

        mLangSwitch.setOnCheckedChangeListener(this);
        mMicImageButton.setOnClickListener(this);
        mDonateImageView.setOnClickListener(this);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalSettings.CONFIG);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSpeak:
                promptSpeechInput();
                break;
            case R.id.donate:
                alertDonate();
                break;
        }
    }

    private void donate() {

        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
            PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalSettings.CONFIG);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            startActivityForResult(intent, PayPalSettings.REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(mDonationAmount),
                getString(R.string.currency),
                getString(R.string.thing_to_buy),
                paymentIntent);
    }

    private void alertDonate(){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptsView = layoutInflater.inflate(R.layout.prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.InputAlertDialog);
        userInput.requestFocus();

        ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).
                toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.lb_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String newAmount;
                                // get user input and set it to result
                                // edit text
                                newAmount = userInput.getText().toString().trim();
                                if(!newAmount.isEmpty() && !newAmount.equalsIgnoreCase("0") )
                                {
                                    mDonationAmount = userInput.getText().toString().trim();
                                }
                                donate();
                                hideKeyboard();
                            }
                        })
                .setNegativeButton(getString(R.string.lb_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                hideKeyboard();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
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
