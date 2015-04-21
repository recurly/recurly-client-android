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
 *
 * Encapsulates the parameters used to make a getPaymentToken request.  Use of this class should be
 * limited to using the Builder to create the request.
 *
 * @see com.recurly.android.RecurlyApi#getPaymentToken(CardPaymentRequest, RecurlyApi.TokenResponseHandler)
 */
public class CardPaymentRequest extends GetRequest {

  /**
   * The credit card number
   */
  protected String number;
  /**
   * The first name on the credit card
   */
  protected String firstName;
  /**
   * The last name on the credit card
   */
  protected String lastName;
  /**
   * The cvv of the credit card
   */
  protected String cvv;
  /**
   * The country of the credit card
   */
  protected String country;
  /**
   * The month the credit card expires
   */
  protected int expirationMonth;
  /**
   * The year the credit card expires
   */
  protected int expirationYear;

  /**
   * Constructor to generated a CardPaymentRequest.  Builder preferred
   *
   * @see com.recurly.android.network.request.CardPaymentRequest.Builder
   *
   * @param number
   * @param firstName
   * @param lastName
   * @param cvv
   * @param country
   * @param expirationMonth
   * @param expirationYear
   */
  public CardPaymentRequest(String number, String firstName, String lastName, String cvv,
                            String country, int expirationMonth, int expirationYear) {
    this.number = number;
    this.firstName = firstName;
    this.lastName = lastName;
    this.cvv = cvv;
    this.expirationMonth = expirationMonth;
    this.expirationYear = expirationYear;
    this.country = country;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public int getExpirationMonth() {
    return expirationMonth;
  }

  public void setExpirationMonth(int expirationMonth) {
    this.expirationMonth = expirationMonth;
  }

  public int getExpirationYear() {
    return expirationYear;
  }

  public void setExpirationYear(int expirationYear) {
    this.expirationYear = expirationYear;
  }

  @Override
  public Map<String, String> getParams() {
    HashMap<String, String> params = new HashMap<String, String>();

    params.put("first_name", firstName);
    params.put("last_name", lastName);
    params.put("number", number);
    params.put("month", ""+expirationMonth);
    params.put("year", ""+expirationYear);
    params.put("cvv", cvv);
    params.put("country", country);

    return params;
  }

  @Override
  public boolean isListRequest() {
    return false;
  }

  @Override
  public String getEndpoint() {
    return "token";
  }

  @Override
  /**
   * Validate the request.  Will be called automatically, but can be called by user directly as well
   */
  public RecurlyError validate() {

    if (firstName == null || firstName.isEmpty()) {
      return RecurlyError.validationError("first name");
    }
    if (lastName == null || lastName.isEmpty()) {
      return RecurlyError.validationError("last name");
    }
    if (country == null || country.isEmpty()) {
      return RecurlyError.validationError("country");
    }
    if (!RecurlyValidator.validateCreditCard(number)) {
      return RecurlyError.validationError("credit card");
    }
    if (!RecurlyValidator.validateCvv(cvv)) {
      return RecurlyError.validationError("cvv");
    }
    if (!RecurlyValidator.validateExpirationDate(expirationMonth, expirationYear)) {
      return RecurlyError.validationError("expiration date");
    }
    return null;
  }

  /**
   * Helper builder class to instantiate a CardPaymentRequest
   */
  public static class Builder {

    private String mNumber;
    private String mFirstName;
    private String mLastName;
    private String mCvv;
    private String mCountry;
    private int mExpirationMonth;
    private int mExpirationYear;

    public Builder setNumber(String number) {
      mNumber = number;
      return this;
    }

    public Builder setFirstName(String firstName) {
      mFirstName = firstName;
      return this;
    }

    public Builder setLastName(String lastName) {
      mLastName = lastName;
      return this;
    }

    public Builder setCvv(String cvv) {
      mCvv = cvv;
      return this;
    }

    public Builder setCountry(String country) {
      mCountry = country;
      return this;
    }

    public Builder setExpirationMonth(int expirationMonth) {
      mExpirationMonth = expirationMonth;
      return this;
    }

    public Builder setExpirationYear(int expirationYear) {
      mExpirationYear = expirationYear;
      return this;
    }

    public CardPaymentRequest build() {
      return new CardPaymentRequest(mNumber, mFirstName, mLastName, mCvv, mCountry, mExpirationMonth, mExpirationYear);
    }
  }
}
