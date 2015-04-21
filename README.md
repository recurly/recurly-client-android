# Recurly Android SDK

The Recurly SDK allows you to integrate recurrent payments in your exisiting Android app in a matter of minutes.

The Recurly SDK will provide to you an authorization key (or token), you can do anything with our API that requires payment information. Because you never handle any sensitive payment information, your PCI scope is drastically reduced.


##1. Set up recurly.com

Visit https://app.recurly.com/ to sign up for an account.  If you already have an account, proceed to the next step.

##2. Get the SDK

Install via Maven

Add the Recurly Android SDK dependency to the build.gradle file.

```gradle

dependencies {
    compile 'com.recurly:android-sdk:1.0.0@aar'
    compile 'com.mcxiaoke.volley:library:1.0.15@aar'
    compile 'com.google.code.gson:gson:2.2.4'
}

```


##3. Configure

### Obtain Public Key

Your API credentials are available at https://app.recurly.com/ under Developer/API Credentials.  Make sure you use your *public key* for the next step.

### Implement SDK

Set up appropriate permissions for your Android Project.  Add the following permissions to AndroidManifest.xml

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

Import the RecurlyApi and use the public key obtained from https://app.recurly.com/ to configure RecurlyApi.

```java
import com.recurly.android.RecurlyApi;
```

```java
    RecurlyApi recurlyApi = RecurlyApi.getInstance(this, "YOUR_ACCESS_TOKEN");
```

##4. Make calls to Recurly

Below are examples of using the RecurlyApi

### Get a payment token

```java
        CardPaymentRequest cardPaymentRequest = new CardPaymentRequest.Builder()
            .setFirstName("John")
            .setLastName("Doe")
            .setNumber("4111 1111 1111 1111")
            .setCvv("123")
            .setCountry("US")
            .setExpirationMonth(06)
            .setExpirationYear(2020)
            .build();

        recurlyApi.getPaymentToken(cardPaymentRequest,
            new RecurlyApi.TokenResponseHandler() {
              @Override
              public void onTokenSuccess(String token) {
                // This token can be used to charge the user
              }

              @Override
              public void onTokenFailure(RecurlyError exception) {
                // Handle any exceptions
              }
            });
```

### Get plan details from a SKU

```java
        PlanRequest planRequest = new PlanRequest.Builder()
                .setPlanCode("PLAN_SKU")
                .build();
                
        getRecurlyApi().getPlan(planRequest,
            new RecurlyApi.PlanResponseHandler() {
              @Override
              public void onPlanSuccess(PlanDTO plan) {
                // getPricing() returns all pricing information, including setup fee, if there is one
                float setupFee = plan.getPricing().getSetupFee();
              }

              @Override
              public void onPlanFailure(RecurlyError exception) {
                // handle failure
              }
            });
```

### Get tax information for a locale

```java
        TaxRequest taxRequest = new TaxRequest.Builder()
                .setCountryCode("US")
                .setPostalCode("94101")
                .build();
                
        recurlyApi.getPostalTax(taxRequest,
            new RecurlyApi.TaxResponseHandler() {
              @Override
              public void onTaxSuccess(TaxDTO tax) {
                // tax.getRate() now contains the tax rate for 94101/US
              }

              @Override
              public void onTaxFailure(RecurlyError exception) {
                // Error handling here
              }
       });
```



