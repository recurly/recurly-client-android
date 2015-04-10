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

package com.recurly.androidsdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Coupon;
import com.recurly.android.model.Plan;
import com.recurly.android.model.Pricing;
import com.recurly.android.model.Tax;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.request.PricingRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.androidsdk.fragment.BaseFragment;
import com.recurly.androidsdk.fragment.FragmentSelectorFragment;


/**
 * Notes for developer setup:
 *
 * Add appropriate permissions
 *  - Add latest google play library
 *  - Add support fragment
 *
 * Add to AndroidManifest.xml:
 *
 * under <application> tag </application> <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
 *
 * Code changes:
 *  - call mRecurlyApi.onStart() in onStart()
 *  - call mRecurlyApi.on
 *  - call onActivityResult in onActivityResult()
 */
public class MainActivity extends BaseActivity {

  private RecurlyApi mRecurlyApi;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // initialize Recurly API with your dev key
    mRecurlyApi = RecurlyApi.getInstance(this, "sc-30WYXJUzQ852w0kHEYQ7Rw"); // Peter

    if (savedInstanceState == null) {
      pushFragment(FragmentSelectorFragment.class);
    }
  }

  @Override
  public void onStart() {
    super.onStart();

    // Notify recurly API app has started to set up appropriate connections
    // (required for Google Wallet)
    mRecurlyApi.onStart();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    boolean handled = mRecurlyApi.onActivityResult(requestCode, resultCode, data);

    if (!handled) {
      // your normal onActivityResult handling
    }
  }


  /**
   * Globally accessible recurly API
   * @return
   */
  public RecurlyApi getRecurlyApi() {
    return mRecurlyApi;
  }

}
