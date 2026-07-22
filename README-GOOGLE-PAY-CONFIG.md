# Google Pay Configuration

This guide covers the merchant-side setup required to accept Google Pay through the Recurly
Android SDK. It mirrors the setup required for Apple Pay on `recurly-client-ios`
(see `README-APPLE-PAY-CONFIG.md` in that repository), adapted for Google Pay.

Google Pay is fully native on Android — no WebView or `recurly.js` is used. The SDK asks Recurly
which card networks, authentication methods, and tokenization spec are enabled for your gateway
(`GET /js/v1/google_pay/info`), and uses those values as-is to build the Google Pay request. It
never hardcodes networks or gateway parameters.

## 1. Enable Google Pay on your Recurly gateway

Google Pay must be enabled on a Google Pay-capable payment gateway configured on your Recurly
site.

1. Sign in to your Recurly account.
2. Go to **Configuration > Payment Gateways**.
3. Add or edit a gateway that supports Google Pay (for example, a card gateway with Google Pay
   tokenization enabled, such as Braintree, Stripe, or Adyen — check your gateway's Recurly
   integration docs for Google Pay support).
4. Note the gateway's `gateway_code` if you plan to support multiple gateways and need to select
   one explicitly via `RecurlyGooglePayParams.gatewayCode`. If you only have a single Google
   Pay-capable gateway, you can omit `gatewayCode` and Recurly will resolve it automatically.

Without a Google Pay-enabled gateway, `GET /js/v1/google_pay/info` returns no payment methods and
`RecurlyGooglePayHandler.getPaymentMethod`/`isReadyToPay` will report Google Pay as unavailable.

## 2. TEST vs PRODUCTION environment

The SDK's `RecurlyGooglePayParams.environment` maps directly to Google Pay's own
`WalletConstants.ENVIRONMENT_TEST` / `ENVIRONMENT_PRODUCTION`:

- **TEST** — lets you exercise the full Google Pay flow (readiness check, payment sheet,
  tokenization) using Google's test cards, with no Google Business Console registration required.
  `googleMerchantId` is ignored by Google in this mode. Use this while developing and in your QA
  environment.
- **PRODUCTION** — requires your app to be registered with Google Pay (see below) and returns
  real, chargeable payment credentials.

Always start integration in `TEST` and only switch to `PRODUCTION` once your Google Pay Business
Console registration (below) has been approved.

## 3. Register for production (Google Pay Business Console)

Before you can use `GooglePayEnvironment.PRODUCTION`, you must register your app and business with
Google:

1. Go to the [Google Pay & Wallet Console](https://pay.google.com/business/console).
2. Create (or select) a business profile and complete Google's brand/business review.
3. Register your app's package name and signing certificate.
4. Once approved, use the **production `merchantId`** issued by Google Business Console as
   `RecurlyGooglePayParams.googleMerchantId`.

This review process is entirely between you and Google; Recurly does not need to be involved, and
the SDK does not gate `PRODUCTION` usage on this being complete — Google's own servers will reject
unregistered production requests.

## 4. Android manifest requirement

Add the following `meta-data` entry inside your app's `<application>` tag (required by the Google
Pay API regardless of environment):

```xml
<meta-data
    android:name="com.google.android.gms.wallet.api.enabled"
    android:value="true" />
```

## 5. Device and session identifiers

The SDK sends a `device_id` and `session_id` with every tokenization request (card and Google
Pay). `device_id` is a randomly generated UUID stored in the app's private storage and persists
for the lifetime of the app install; `session_id` is a randomly generated UUID scoped to the
current `RecurlyClient` instance. Neither is derived from `ANDROID_ID`, the advertising ID, or any
other hardware/device identifier — they exist purely as fraud-prevention signals for Recurly and
carry no information beyond "this request came from the same app install" /
"this request came from the same SDK session."

## 6. What the SDK does NOT do

- **3D Secure / risk scoring**: Recurly's optional post-tokenization risk preflight
  (`PUT /tokens/{id}`) is not currently performed by the Android SDK's Google Pay integration.
- **Buy Now, Pay Later (BNPL) methods** (e.g. Affirm, Klarna, Afterpay): the SDK rejects these
  before ever invoking the Google Pay sheet, since Google Pay's native card flow does not support
  tokenizing them. Configure a card-capable gateway for Google Pay.

See the main [README.md](README.md#google-pay) for the Kotlin integration steps.
