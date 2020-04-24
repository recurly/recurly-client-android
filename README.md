# Recurly Android SDK

The Recurly SDK allows you to integrate recurrent payments in your existing Android app in a matter of minutes.

We encourage our partners to review Google's guidelines on mobile application development. In particular, please review the "Paid and Free Apps" section to familiarize yourself with the guidelines around in-app purchases. https://play.google.com/about/developer-content-policy.html

When a customer submits your payment form, the Recurly Android SDK sends customer payment information to be encrypted and stored at Recurly and gives you an billing token to complete the subscription process using our powerful API.

With this billing token, you can do anything with our API that requires payment information. Because you never handle any sensitive payment information, your PCI scope is drastically reduced.


## 1. Sign Up for Recurly

Sign up for a free Recurly account if you don't have one already, at <https://app.recurly.com/signup>.

## 2. Get the SDK

Add the Recurly Android SDK dependency to the build.gradle file.

```groovy
dependencies {
    compile 'com.recurly:android-sdk:1.0.1@aar'
    compile 'com.mcxiaoke.volley:library:1.0.15@aar'
    compile 'com.google.code.gson:gson:2.2.4'
}
```


## 3. Configure

### 3.1 Obtain Public Key

You'll need to have your API public key (yes *public*, not private) on hand for the next step, so make sure you grab it before. Your API credentials are available on your Recurly Site, at: https://app.recurly.com/go/developer/api_access

### 3.2 Integrate SDK

Declare the necessary permissions for your Android Project by adding the following lines to `app/src/AndroidManifest.xml`, inside the `<application>` tags.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

You'll need to import `RecurlyApi` in each file you use the SDK, and use the public key from earlier to configure RecurlyApi.

```java
import com.recurly.android.RecurlyApi;
```

```java
RecurlyApi recurlyApi = RecurlyApi.getInstance(this, "YOUR_PUBLIC_KEY");
```

## 4. Examples

Once the SDK is imported and configured, we can start building stuff with it! Feel free to use these as starting points for your own integrations.

**Note**: Different parts of the SDK require different classes to be imported. The required imports for each example are listed, but you may choose to structure them in a different way. More info is available in the Java documentation: <https://docs.oracle.com/javase/tutorial/java/package/usepkgs.html>.

### Get a payment token

**Imports**:

```java
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;
```

**Request**:

```java
 Address billingAddress = new Address.Builder()
	.setAddress1("310 Townsend St.")
	.setCity("San Francisco")
	.setState("CA")
	.setPostalCode("94107")
	.setCountry("US")
	.createAddress();

CardPaymentRequest cardPaymentRequest = new CardPaymentRequest.Builder()
  .setFirstName("John")
  .setLastName("Doe")
  .setNumber("4111 1111 1111 1111")
  .setCvv("123")
  .setExpirationMonth(06)
  .setExpirationYear(2020)
  .setBillingAddress(billingAddress)
  .build();

recurlyApi.getPaymentToken(cardPaymentRequest,
  new RecurlyApi.TokenResponseHandler() {
    @Override
    public void onTokenSuccess(String token) {
      // This token can be used to charge the user
      Log.i("RecurlyExample", "TOKEN SUCCESS – token: "+token);
    }

    @Override
    public void onTokenFailure(RecurlyError exception) {
      // Handle any exceptions
      Log.e("RecurlyExample", "TOKEN FAIL");
    }
  });
```

### Get the details of a plan
**Imports**:

```java
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.dto.PlanDTO;
```
**Request**:

```java
PlanRequest planRequest = new PlanRequest.Builder()
  .setPlanCode("PLAN_SKU") // Substitute your own plan code here
  .build();

recurlyApi.getPlan(planRequest,
  new RecurlyApi.PlanResponseHandler() {
    @Override
    public void onPlanSuccess(PlanDTO plan) {
      // getPricing() returns all pricing information, including setup fee, if there is one
      float setupFee = plan.getPricing().getSetupFee();
      Log.i("RecurlyExample", "PLAN SUCCESS – setup fee: "+setupFee);
    }

    @Override
    public void onPlanFailure(RecurlyError exception) {
      // handle failure
      Log.e("RecurlyExample", "PLAN FAIL");
    }
  });
```

### Get tax details for a specific locale
**Imports**:

```java
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.android.network.dto.TaxDTO;
```

**Request**:

```java
TaxRequest taxRequest = new TaxRequest.Builder()
  .setCountryCode("US")
  .setPostalCode("94110")
  .build();

recurlyApi.getPostalTax(taxRequest,
  new RecurlyApi.TaxResponseHandler() {
    @Override
    public void onTaxSuccess(TaxDTO tax) {
      // tax.getRate() now contains the tax rate for 94110/US
      Log.i("RecurlyExample", "TAX SUCCESS – tax rate: "+tax.getRate());
    }

    @Override
    public void onTaxFailure(RecurlyError exception) {
      // Error handling here
      Log.e("RecurlyExample", "TAX FAIL");
    }
});
```

## 5. Using a Token

Once the SDK has stored your customer’s sensitive data and given you a token reference, you will have 20 minutes to use it in our [API](https://dev.recurly.com/). Expired tokens are permanently removed from the Recurly servers.

Tokens can be used to populate any account Billing Info data through our API. Simply assign it to the Billing Info’s `token_id` property and we’ll do the rest.

**These endpoints accept tokens within billing info.**

* Subscription [`create`](https://dev.recurly.com/docs/create-subscription)
* Account [`create`](https://dev.recurly.com/docs/create-an-account), [`update`](https://dev.recurly.com/docs/update-account)
* Billing Info [`update`](https://dev.recurly.com/docs/update-an-accounts-billing-info-token)
* Transaction [`create`](https://dev.recurly.com/docs/create-transaction)

> If you use a token, no other attributes will be allowed on that Billing Info for that request.
