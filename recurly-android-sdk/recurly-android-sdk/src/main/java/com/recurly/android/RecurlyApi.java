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
import android.support.v4.app.FragmentManager;

import com.recurly.android.network.dto.CouponDTO;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.dto.PricingDTO;
import com.recurly.android.network.dto.TaxDTO;
import com.recurly.android.network.RecurlyConfig;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.RecurlyApiClient;
import com.recurly.android.network.ResponseHandler;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.request.PricingRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.android.network.dto.TokenDTO;
import com.recurly.android.util.RecurlyLog;

import java.util.List;

/**
 * Main interface to access Recurly services.
 *
 * Create an instance of RecurlyApi using the API key provided in your account setup instructions.
 *
 * @see RecurlyApi#getInstance(Context, String)
 *
 * Methods currently available:
 *
 * @see RecurlyApi#getPaymentToken(CardPaymentRequest, TokenResponseHandler)
 * @see RecurlyApi#getPlan(PlanRequest, PlanResponseHandler)
 * @see RecurlyApi#getCoupon(CouponRequest, CouponResponseHandler)
 * @see RecurlyApi#getPostalTax(TaxRequest, TaxResponseHandler)
 * @see RecurlyApi#getPricing(PricingRequest, PricingResponseHandler)
 */
public class RecurlyApi {

  // NOTE: Update value in recurly-android-sdk/recurly-android-sdk/build.gradle as well
  /**
   * Current version number
   */
  public static final java.lang.String VERSION = "1.0.0";

  private RecurlyApiClient mClient;

  private RecurlyApi(Context context, String apiKey) {
    RecurlyConfig recurlyConfig = RecurlyConfig.getDefaultConfiguration(apiKey);
    mClient = new RecurlyApiClient();
    mClient.init(context, recurlyConfig);

    RecurlyLog.i("RecurlyApi initialized");

  }

  /**
   * Helper method to create instance of Recurly Api
   * @param context The Activity context
   * @param apiKey The API key for your Recurly account
   * @return Instance of RecurlyApi configured for your Recurly account
   */
  public static RecurlyApi getInstance(Context context, String apiKey) {
    RecurlyApi api = new RecurlyApi(context, apiKey);
    return api;
  }

  /**
   * Handler interface for getPostalTax request.  Implement interface to handle callbacks for this
   * API method.
   *
   * @see RecurlyApi#getPostalTax(TaxRequest, TaxResponseHandler)
   */
  public interface TaxResponseHandler {
    void onTaxSuccess(TaxDTO tax);
    void onTaxFailure(RecurlyError exception);
  }

  /**
   * The tax request is used to determine a user's tax rate based on their location.
   *
   * On success, returns a {@link TaxDTO}
   * On failure, returns a {@link com.recurly.android.network.RecurlyError}
   *
   * @see TaxRequest
   * @see com.recurly.android.RecurlyApi.TaxResponseHandler
   *
   *
   * @param request The request encapsulating the query parameters
   * @param handler The handler provides callbacks on success and on failure
   */
  public void getPostalTax(final TaxRequest request,
                           final TaxResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onTaxFailure(error);
      return;
    }

    mClient.getTaxForPostalCode(request, new ResponseHandler<List<TaxDTO>>() {
      @Override
      public void onSuccess(List<TaxDTO> taxes) {
        if (taxes != null && taxes.size() > 0) {
          handler.onTaxSuccess(taxes.get(0));
        } else {
          handler.onTaxSuccess(TaxDTO.NO_TAX);
        }
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onTaxFailure(ex);
      }
    });
  }

  /**
   * Handler interface for getPaymentToken request.  Implement interface to handle callbacks for this
   * API method.
   *
   * @see RecurlyApi#getPaymentToken(CardPaymentRequest, TokenResponseHandler)
   */
  public interface TokenResponseHandler {
    void onTokenSuccess(String response);
    void onTokenFailure(RecurlyError exception);
  }

  /**
   * The card payment request sends credit card payment information to Recurly's servers in exchange
   * for a token that can be used to process payment requests.
   *
   * On success, returns a {@link TokenDTO}
   * On failure, returns a {@link com.recurly.android.network.RecurlyError}
   *
   * @see CardPaymentRequest
   * @see com.recurly.android.RecurlyApi.TokenResponseHandler
   *
   * @param request The request encapsulating the query parameters
   * @param handler The handler provides callbacks on success and on failure
   */
  public void getPaymentToken(final CardPaymentRequest request,
                              final TokenResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onTokenFailure(error);
      return;
    }

    mClient.getTokenForCardPayment(request, new ResponseHandler<TokenDTO>() {
      @Override
      public void onSuccess(TokenDTO tokenResponse) {
        handler.onTokenSuccess(tokenResponse.getId());
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onTokenFailure(ex);
      }
    });
  }

  /**
   * Handler interface for getPlan request.  Implement interface to handle callbacks for this
   * API method.
   *
   * @see RecurlyApi#getPlan(PlanRequest, PlanResponseHandler)
   */
  public interface PlanResponseHandler {
    void onPlanSuccess(PlanDTO plan);
    void onPlanFailure(RecurlyError exception);
  }

  /**
   * The plan request fetches the details of the specified plan.
   *
   * On success, returns a {@link PlanDTO}
   * On failure, returns a {@link com.recurly.android.network.RecurlyError}
   *
   * @see PlanRequest
   * @see com.recurly.android.RecurlyApi.PlanResponseHandler
   *
   * @param request The request encapsulating the query parameters
   * @param handler The handler provides callbacks on success and on failure
   */
  public void getPlan(final PlanRequest request,
                      final PlanResponseHandler handler) {

    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onPlanFailure(error);
      return;
    }

    mClient.getPlan(request, new ResponseHandler<PlanDTO>() {
      @Override
      public void onSuccess(PlanDTO plan) {
        handler.onPlanSuccess(plan);
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onPlanFailure(ex);
      }
    });
  }

  /**
   * Handler interface for getCoupon request.  Implement interface to handle callbacks for this
   * API method.
   *
   * @see RecurlyApi#getCoupon(CouponRequest, CouponResponseHandler)
   */
  public interface CouponResponseHandler {
    void onCouponSuccess(CouponDTO coupon);
    void onCouponFailure(RecurlyError exception);
  }

  /**
   * The coupon request checks to see if a coupon is valid for a specified plan.
   *
   * On success, returns a {@link CouponDTO}
   * On failure, returns a {@link com.recurly.android.network.RecurlyError}
   *
   * @see CouponRequest
   * @see com.recurly.android.RecurlyApi.CouponResponseHandler
   *
   * @param request The request encapsulating the query parameters
   * @param handler The handler provides callbacks on success and on failure
   */
  public void getCoupon(final CouponRequest request,
                        final CouponResponseHandler handler) {
    RecurlyError error = request.validate();
    if (error != null) {
      request.setFinished(true);
      handler.onCouponFailure(error);
      return;
    }

    mClient.getCoupon(request, new ResponseHandler<CouponDTO>() {
      @Override
      public void onSuccess(CouponDTO coupon) {
        handler.onCouponSuccess(coupon);
      }

      @Override
      public void onFailure(RecurlyError ex) {
        handler.onCouponFailure(ex);
      }
    });
  }


  /**
   * Handler interface for getPricing request.  Implement interface to handle callbacks for this
   * API method.
   *
   * @see RecurlyApi#getPricing(PricingRequest, PricingResponseHandler)
   */
  public interface PricingResponseHandler {
    void onPricingSuccess(PricingDTO pricing);
    void onPricingFailure(RecurlyError exception);
  }

  /**
   * The pricing request is used to determine pricing information for a plan.  The request may also
   * include a coupon code and user's location to determine the tax rate.  Additional add-ons for the
   * plan may be included in the pricing request as well.
   *
   * On success, returns a {@link PricingDTO}
   * On failure, returns a {@link com.recurly.android.network.RecurlyError}
   *
   * @see PricingRequest
   * @see com.recurly.android.RecurlyApi.PricingResponseHandler
   *
   * @param request The request encapsulating the query parameters
   * @param handler The handler provides callbacks on success and on failure
   */
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