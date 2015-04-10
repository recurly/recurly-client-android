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

package com.recurly.android;

import android.content.Context;
import android.content.Intent;

import com.recurly.android.model.Coupon;
import com.recurly.android.model.Plan;
import com.recurly.android.model.Pricing;
import com.recurly.android.model.Tax;
import com.recurly.android.network.RecurlyConfig;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.RecurlyApiClient;
import com.recurly.android.network.ResponseHandler;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.request.PricingRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.android.network.response.TokenResponse;
import com.recurly.android.util.RecurlyLog;

import java.util.List;

public class RecurlyApi {
  public static final java.lang.String VERSION = "0.1.0";

  private RecurlyApiClient mClient;

  private RecurlyApi(Context context, String apiKey) {
    RecurlyConfig recurlyConfig = RecurlyConfig.getDefaultConfiguration(apiKey);
    mClient = new RecurlyApiClient();
    mClient.init(context, recurlyConfig);

    RecurlyLog.i("RecurlyApi initialized");

  }

  public static RecurlyApi getInstance(Context context, String apiKey) {
    RecurlyApi api = new RecurlyApi(context, apiKey);
    return api;
  }

  public void onStart() {
  }


  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    return false;
  }


  public interface TaxResponseHandler {
    void onTaxSuccess(Tax tax);
    void onTaxFailure(RecurlyError exception);
  }

  public void getPostalTax(final TaxRequest request,
                           final TaxResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onTaxFailure(error);
      return;
    }

    mClient.getTaxForPostalCode(request, new ResponseHandler<List<Tax>>() {
      @Override
      public void onSuccess(List<Tax> taxes) {
        if (taxes != null && taxes.size() > 0) {
          handler.onTaxSuccess(taxes.get(0));
        } else {
          handler.onTaxSuccess(Tax.NO_TAX);
        }
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onTaxFailure(ex);
      }
    });
  }

  public interface TokenResponseHandler {
    void onTokenSuccess(String response);
    void onTokenFailure(RecurlyError exception);
  }

  public void getPaymentToken(final CardPaymentRequest request,
                              final TokenResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onTokenFailure(error);
      return;
    }

    mClient.getTokenForCardPayment(request, new ResponseHandler<TokenResponse>() {
      @Override
      public void onSuccess(TokenResponse tokenResponse) {
        handler.onTokenSuccess(tokenResponse.getId());
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onTokenFailure(ex);
      }
    });
  }

  public interface PlanResponseHandler {
    void onPlanSuccess(Plan plan);
    void onPlanFailure(RecurlyError exception);
  }

  public void getPlan(final PlanRequest request,
                      final PlanResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onPlanFailure(error);
      return;
    }

    mClient.getPlan(request, new ResponseHandler<Plan>() {
      @Override
      public void onSuccess(Plan plan) {
        handler.onPlanSuccess(plan);
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onPlanFailure(ex);
      }
    });
  }

  public interface CouponResponseHandler {
    void onCouponSuccess(Coupon coupon);
    void onCouponFailure(RecurlyError exception);
  }

  public void getCoupon(final CouponRequest request,
                        final CouponResponseHandler handler) {
    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onCouponFailure(error);
      return;
    }

    mClient.getCoupon(request, new ResponseHandler<Coupon>() {
      @Override
      public void onSuccess(Coupon coupon) {
        handler.onCouponSuccess(coupon);
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onCouponFailure(ex);
      }
    });
  }


  public interface PricingResponseHandler {
    void onPricingSuccess(Pricing pricing);
    void onPricingFailure(RecurlyError exception);
  }

  public void getPricing(final PricingRequest request,
                         final PricingResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onPricingFailure(error);
      return;
    }

    request.setHandler(handler);
    request.fetchRequiredData(this);
  }


}