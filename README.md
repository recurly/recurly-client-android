# Recurly Android SDK
[![Maven Central](https://img.shields.io/static/v1?label=Maven%20Central&message=recurly&color=purple)](https://search.maven.org/artifact/com.recurly/android-sdk) ![Recurly Client Android](https://github.com/recurly/recurly-client-android/actions/workflows/ci-test.yaml/badge.svg?branch=master) ![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/recurly/recurly-client-android?sort=semver)

The Recurly SDK allows you to integrate recurrent payments in your existing Android app in a matter of minutes.

We encourage our partners to review Google's guidelines on mobile application development. In particular, please review the "Paid and Free Apps" section to familiarize yourself with the guidelines around in-app purchases. https://play.google.com/about/developer-content-policy.html

When a customer submits your payment form, the Recurly Android SDK sends customer payment information to be encrypted and stored at Recurly and gives you an authorization key to complete the subscription process using our powerful API.

With this authorization key (or token), you can do anything with our API that requires payment information. Because you never handle any sensitive payment information, your PCI scope is drastically reduced.

## 1. Sign Up for Recurly

Sign up for a free Recurly account if you don't have one already, at https://app.recurly.com/signup

## 2. Implement the SDK

Add the Recurly Android SDK dependency to the build.gradle file.

```groovy
dependencies {
    implementation 'com.recurly.androidsdk:androidsdk:1.0.0'
}
```

## 3. Configure

## 3.1 Obtain Public Key

You'll need to have your API public key (yes public, not private) on hand for the next step, so make sure you grab it before. Your API credentials are available on your Recurly Site, at: https://app.recurly.com/go/developer/api_access

## 3.2 Integrate SDK

Declare the necessary permissions for your Android Project by adding the following lines to `app/src/AndroidManifest.xml`, inside the `<application>` tags.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
```

You need to configure your public key before using the API

```kotlin
import com.recurly.androidsdk.data.model.RecurlySessionData
```
```kotlin
RecurlySessionData.setPublicKey("YOUR_PUBLIC_KEY");
```

## 4 Examples

## XML implementation

You can implement the credit card fields directly on your XML file depending on your needs

Unified Credit Card View that contains the Credit Card Number, the Expiration date and the CVV code input fields
```xml
    <com.recurly.androidsdk.presentation.view.RecurlyUnifiedCreditCard
        android:id="@+id/recurly_unified_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

Individual Credit Card Views
Credit Card number
```xml
<com.recurly.androidsdk.presentation.view.RecurlyCreditCardNumber
        android:id="@+id/recurly_credit_card_number"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

Expiration date MM/YY
```xml
<com.recurly.androidsdk.presentation.view.RecurlyExpirationMMYY
        android:id="@+id/recurly_expiration_date"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

CVV code view
```xml
<com.recurly.androidsdk.presentation.view.RecurlyCVV
        android:id="@+id/recurly_cvv_code"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
```

## Kotlin usage

You can use `binding view` to access the methods of the views.

These methods can be used on every Recurly view and are the same for all of them

```kotlin

//With this method you can change the placeholders of the different views
recurlyView.setPlaceholders("YOUR_NEW_PLACEHOLDERS")

//Changes the placeholders color
recurlyView.setPlaceholderColor(ContextCompat.getColor(context, R.color.your_color))

//Changes the text color
recurlyView.setTextColor(ContextCompat.getColor(context, R.color.your_color))

//Changes the error text color
recurlyView.setTextErrorColor(ContextCompat.getColor(context, R.color.your_color))

//Changes the font of the input fields with a typeface
recurlyView.setFont(Typeface , Style)

// This fun validates if all the inputs are complete, returns a boolean 
// true if the inputs are correctly filled, false if they are not
recurlyView.validateData()

//With this function you can highlight the number field with an error, this is useful if you find an error with the tokenization
recurlyView.setCreditCardNumberError()

//With this function you can highlight the expiration date field with an error, this is useful if you find an error with the tokenization
recurlyView.setExpirationError()

//With this function you can highlight the cvv code field with an error, this is useful if you find an error with the tokenization
recurlyView.setCvvError()
```

When you need to call the Tokenization you first need to have your public key already instantiated, then as a recommendation you should call the `.validateData()` functions of the views you are using.
After the validation of the credit card inputs you should fill the billing Information as this example
```kotlin
// Checkout the documentation about this fields at https://developers.recurly.com/reference/recurly-js/index.html
val billingInfo = RecurlyApi.buildCreditCardBillingInfo(
        firstName = "John",
        lastName = "Doe",
        company = "",
        addressOne = "",
        addressTwo = "",
        city = "",
        state = "",
        postalCode = "",
        country = "",
        phone = "",
        vatNumber = "",
        taxIdentifier = "",
        taxIdentifierType = ""
    ) 
```

Once you have completed the billing info you can directly call the tokenization, the Credit Card data is saved automatically  

```kotlin
RecurlyApi.creditCardTokenization(
    lifecycleOwner, // The LifecycleOwner where you are calling the tokenization
    billingInfo, // The Billing information you previously filled
    object : ResponseHandler, // The Handler that will allow you to directly get a success or error response
        RecurlyTokenizationHandler {
        
        override fun onSuccess(token: String, type: String) {
            // Here you receive directly the token and the type
        }

        override fun onError(error: ErrorRecurly) {
            // Here you can obtain and handle the error from recurly, to have a deep look at the error codes checkout
            // https://developers.recurly.com/reference/recurly-js/index.html#validation
        }
    })
```
