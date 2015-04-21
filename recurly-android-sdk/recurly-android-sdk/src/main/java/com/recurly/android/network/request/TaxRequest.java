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

package com.recurly.android.network.request;

import com.recurly.android.RecurlyApi;
import com.recurly.android.RecurlyValidator;
import com.recurly.android.network.RecurlyError;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the parameters used to make a getPostalTax request.  Use of this class should be
 * limited to using the Builder to create the request.
 *
 * @see com.recurly.android.RecurlyApi#getPostalTax(TaxRequest, RecurlyApi.TaxResponseHandler)
 *
 */

public class TaxRequest extends GetRequest {

  /**
   * The postal code for this tax request
   */
  protected String postalCode;

  /**
   * The country code for this tax request
   */
  protected String countryCode;

  /**
   * Constructor for TaxRequest.  Use Builder instead
   *
   * @see com.recurly.android.network.request.TaxRequest.Builder
   *
   * @param postalCode
   * @param countryCode
   */
  public TaxRequest(String postalCode, String countryCode) {
    this.postalCode = postalCode;
    this.countryCode = countryCode;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  @Override
  public Map<String, String> getParams() {
    HashMap<String, String> params = new HashMap<String, String>();
    params.put("postal_code", postalCode);
    params.put("country", countryCode);
    return params;
  }

  @Override
  public boolean isListRequest() {
    return true;
  }

  @Override
  public String getEndpoint() {
    return "tax";
  }

  @Override
  /**
   * Validate the request.  Will be called automatically, but can be called by user directly as well
   */
  public RecurlyError validate() {
    if (postalCode == null || postalCode.isEmpty()) {
      return RecurlyError.validationError("postal code");
    }
    if (countryCode == null || countryCode.isEmpty() || !RecurlyValidator.validateCountryCode(countryCode)) {
      return RecurlyError.validationError("country code");
    }

    return null;
  }

  /**
   * Helper builder class to create instance of TaxRequest
   */
  public static class Builder {

    private String mPostalCode;
    private String mCountryCode;

    public Builder setPostalCode(String postalCode) {
      mPostalCode = postalCode;
      return this;
    }

    public Builder setCountryCode(String countryCode) {
      mCountryCode = countryCode;
      return this;
    }

    public TaxRequest build() {
      return new TaxRequest(mPostalCode, mCountryCode);
    }
  }

}
