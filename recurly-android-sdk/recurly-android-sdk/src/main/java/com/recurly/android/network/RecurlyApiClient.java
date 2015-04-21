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
import android.net.Uri;

import com.android.volley.Request;
import com.recurly.android.network.dto.BaseDTO;
import com.recurly.android.network.dto.CouponDTO;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.android.network.dto.TaxDTO;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.android.network.request.TaxRequest;
import com.recurly.android.network.dto.TokenDTO;
import com.recurly.android.util.RecurlyLog;

import java.util.List;
import java.util.Map;

/**
 * Api Client used to connect to Recurly web services.  Used by RecurlyApi.  Do not access directly
 *
 * @see com.recurly.android.RecurlyApi
 */
public class RecurlyApiClient {

  private RecurlyNetwork mNetwork;
  private RecurlyConfig mRecurlyConfig;

  public void init(Context context, RecurlyConfig configuration) {
    mRecurlyConfig = configuration;

    BaseDTO.setCurrency(configuration.getCurrency());

    mNetwork = new RecurlyNetwork();
    mNetwork.init(context);
    mNetwork.setDefaultTimeout(mRecurlyConfig.getDefaultTimeout());
  }

  public void getTaxForPostalCode(TaxRequest taxRequest,
                           final ResponseHandler<List<TaxDTO>> responseHandler) {

    String url = buildRequestUrl(taxRequest.getEndpoint(), taxRequest.getParams(), true);

    RecurlyLog.d("URL is " + url);
    RecurlyListRequest<TaxDTO> request = new RecurlyListRequest<TaxDTO> (taxRequest,
        TaxDTO.class, Request.Method.GET, url,
        responseHandler);

    mNetwork.transmitRequest(request);
  }

  public void getTokenForCardPayment(CardPaymentRequest paymentRequest,
                                  final ResponseHandler<TokenDTO> responseHandler) {

    String url = buildRequestUrl(paymentRequest.getEndpoint(), paymentRequest.getParams(), true);

    RecurlyLog.d("URL is " + url);
    RecurlyRequest<TokenDTO> request = new RecurlyRequest<TokenDTO> (
        paymentRequest,
        TokenDTO.class, Request.Method.GET, url,
        responseHandler);

    mNetwork.transmitRequest(request);
  }

  public void getPlan(PlanRequest planRequest,
                      final ResponseHandler<PlanDTO> responseHandler) {

    String url = buildRequestUrl(planRequest.getEndpoint(), planRequest.getParams(), true);

    RecurlyLog.d("URL is " + url);
    RecurlyRequest<PlanDTO> request = new RecurlyRequest<PlanDTO> (
        planRequest,
        PlanDTO.class, Request.Method.GET, url,
        responseHandler);

    mNetwork.transmitRequest(request);
  }

  public void getCoupon(CouponRequest couponRequest,
                      final ResponseHandler<CouponDTO> responseHandler) {

    String url = buildRequestUrl(couponRequest.getEndpoint(), couponRequest.getParams(), true);

    RecurlyLog.d("URL is " + url);
    RecurlyRequest<CouponDTO> request = new RecurlyRequest<CouponDTO> (
        couponRequest,
        CouponDTO.class, Request.Method.GET, url,
        responseHandler);

    mNetwork.transmitRequest(request);
  }

  public String buildRequestUrl(String relativePath, Map<String, String> params, boolean appendKey) {

    Uri.Builder builder = Uri.parse(mRecurlyConfig.getBaseUrl()).buildUpon();

    builder.path(mRecurlyConfig.getApiPath() + "/" + relativePath);

    for (Map.Entry<String, String> entry : params.entrySet()) {
      builder.appendQueryParameter(entry.getKey(), entry.getValue());
    }

    builder.appendQueryParameter("currency", mRecurlyConfig.getCurrency());

    if (appendKey) {
      builder.appendQueryParameter("key", mRecurlyConfig.getPublicKey());
      builder.appendQueryParameter("version", mRecurlyConfig.getApiVersion());
    }

    return builder.build().toString();
  }


}
