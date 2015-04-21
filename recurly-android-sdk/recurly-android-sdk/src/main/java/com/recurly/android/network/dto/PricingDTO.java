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

package com.recurly.android.network.dto;

import com.recurly.android.RecurlyApi;
import com.recurly.android.network.request.PricingRequest;

/**
 * Encapsulates details returned from a getPricing call
 *
 * @see com.recurly.android.network.request.PricingRequest
 * @see com.recurly.android.RecurlyApi#getPricing(PricingRequest, RecurlyApi.PricingResponseHandler)
 */

public class PricingDTO {

  /**
   * The price summary for amount that is due now
   */
  protected PriceSummaryDTO now;  // TODO: rename to initialPrice
  /**
   * The price summary for amount that will be due on recurring basis when plan expires
   */
  protected PriceSummaryDTO next; // TODO: rename to recurringPrice
  /**
   * The card summary details for amount due now
   */
  protected CartSummaryDTO base;  // TODO: rename to cartItems

  /**
   * The currency code for the pricing
   *
   * @see com.recurly.android.Constants.CurrencyType
   */
  protected String currencyCode;

  /**
   * The symbol for the currency
   */
  protected String currencySymbol;

  public PriceSummaryDTO getNow() {
    return now;
  }

  public void setNow(PriceSummaryDTO now) {
    this.now = now;
  }

  public PriceSummaryDTO getNext() {
    return next;
  }

  public void setNext(PriceSummaryDTO next) {
    this.next = next;
  }

  public CartSummaryDTO getBase() {
    return base;
  }

  public void setBase(CartSummaryDTO base) {
    this.base = base;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  @Override
  public String toString() {
    return "Pricing{" +
        "now=" + now +
        ", next=" + next +
        ", base=" + base +
        ", currencyCode='" + currencyCode + '\'' +
        ", currencySymbol='" + currencySymbol + '\'' +
        '}';
  }
}
