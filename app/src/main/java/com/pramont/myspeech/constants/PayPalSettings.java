package com.pramont.myspeech.constants;

import android.net.Uri;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PayPalSettings {

    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    public static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox environments.
    public static final String CONFIG_CLIENT_ID = "AZLX8-vxk6VCqm27DN08x5eg4OKr6UlVoSrGq1b430wbpjsJV5ySYydPHdcuTSnxsoigMLJZiOlMVe_Z";

    public static final int REQUEST_CODE_PAYMENT = 1;

    public static PayPalConfiguration CONFIG = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("pramont")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
}
