/*
 * The MIT License
 * Copyright (c) 2014-2015 Recurly, Inc.

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.recurly.android.network;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.recurly.android.RecurlyApi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Handles transmission of recurly requests across network.
 */
public class RecurlyNetwork {

  public static final String UA_PLATFORM               = "recurly-android";

  public static final String UA_NETWORK_LIB            = "volley";
  public static final String UA_NETWORK_VERSION        = "1.0.15";
  public static final String UA_DEVICE                 = "device";
  public static final String UA_OS                     = "os";

  public static final String UA_CARRIER_NAME           = "carrierName";
  public static final String UA_COUNTRY_CODE           = "isoCountryCode";
  public static final String UA_MOBILE_COUNTRY_CODE    = "mobileCountryCode";
  public static final String UA_MOBILE_NETWORK_CODE    = "mobileNetworkCode";

  public static final String UA_APP_NAME               = "appName";

  private RequestQueue mRequestQueue = null;
  private static Map<String, String> sHeaders = new HashMap<String, String>();

  private int mRetryCount = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
  private float mBackoffMultiplier = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
  private RetryPolicy mRetryPolicy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
  private String mCarrierName;
  private String mCarrierCountry;
  private String mCarrierOperator;

  private String mPackageName;

  public void init(Context context) {
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(context);
    }

    try {
      TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      mCarrierName = manager.getNetworkOperatorName();
      String operator = manager.getNetworkOperator();

      if (operator != null && operator.length() == 6) {
        mCarrierCountry = operator.substring(0, 3);
        mCarrierOperator = operator.substring(3, 6);
      }

      mPackageName = context.getPackageName();
    } catch (Exception ex) {
    }

    if (mCarrierName == null) {
      mCarrierName = "";
    }
    if (mCarrierCountry == null) {
      mCarrierCountry = "";
    }
    if (mCarrierOperator == null) {
      mCarrierOperator = "";
    }

    setStaticHeader("Accept", "application/xml");
    String userAgent = getUserAgentString();
    setStaticHeader("User-Agent", userAgent);

  }

  public void setRetryPolicy(int initialTimeout, int maxNumRetries, float backoffMultiplier) {
    mRetryCount = maxNumRetries;
    mBackoffMultiplier = backoffMultiplier;
    mRetryPolicy = new DefaultRetryPolicy(initialTimeout, maxNumRetries, backoffMultiplier);
  }

  public void setDefaultTimeout(int initialTimeout) {
    mRetryPolicy = new DefaultRetryPolicy(initialTimeout, mRetryCount, mBackoffMultiplier);
  }

  public static void setStaticHeader(String key, String value) {
    sHeaders.put(key, value);
  }

  public Map<String, String> getStaticHeaders() {
    return sHeaders;
  }

  public void transmitRequest(Request request) {

    request.setRetryPolicy(mRetryPolicy);

    if (request instanceof RecurlyRequest) {
      updateHeaders();

      ((RecurlyRequest) request).setHeaders(sHeaders);
    }
    mRequestQueue.add(request);
  }

  private void updateHeaders() {
  }

  private String getUserAgentString() {

    StringBuilder sb = new StringBuilder();

    sb.append(UA_PLATFORM);
    sb.append("/");
    sb.append(RecurlyApi.VERSION);
    sb.append("; ");

    sb.append(UA_NETWORK_LIB);
    sb.append("/");
    sb.append(UA_NETWORK_VERSION);
    sb.append("; ");

    sb.append(UA_DEVICE);
    sb.append("/");
    sb.append(Build.PRODUCT.toLowerCase());
    sb.append("; ");

    sb.append(UA_OS);
    sb.append("/");
    sb.append(Build.VERSION.SDK_INT);
    sb.append("; ");

    sb.append(UA_CARRIER_NAME);
    sb.append("/");
    sb.append(mCarrierName);
    sb.append("; ");

    sb.append(UA_COUNTRY_CODE);
    sb.append("/");
    sb.append(Locale.getDefault().getCountry());
    sb.append("; ");

    sb.append(UA_MOBILE_COUNTRY_CODE);
    sb.append("/");
    sb.append(mCarrierCountry);
    sb.append("; ");

    sb.append(UA_MOBILE_NETWORK_CODE);
    sb.append("/");
    sb.append(mCarrierOperator);
    sb.append("; ");

    sb.append(UA_APP_NAME);
    sb.append("/");
    sb.append(mPackageName);


    sb.append(";");

    return sb.toString();
  }

  public void cancelOutstandingRequests() {
    mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });
  }

}