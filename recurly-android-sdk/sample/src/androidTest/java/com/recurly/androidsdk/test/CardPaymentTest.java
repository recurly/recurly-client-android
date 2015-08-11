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

package com.recurly.androidsdk.test;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Address;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CardPaymentRequest;
import com.recurly.androidsdk.MainActivity;

import java.util.concurrent.TimeoutException;

public class CardPaymentTest extends ApiTest {

  public CardPaymentTest() {
    super(MainActivity.class);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testInvalidCard() {

    CardPaymentRequest request = new CardPaymentRequest.Builder()
        .setFirstName("first")
        .setLastName("last")
        .setNumber("4111111111111112")
        .setExpirationMonth(12)
        .setExpirationYear(20)
        .setCvv("123")
        .setCountry("US")
        .build();


    mApi.getPaymentToken(request,
        new RecurlyApi.TokenResponseHandler() {
          @Override
          public void onTokenSuccess(String response) {
            assertFalse("Invalid card validated", true);
          }

          @Override
          public void onTokenFailure(RecurlyError exception) {
            assertTrue("Unexpected status code.. expected 400, received " + exception.getStatusCode(),
                exception.getStatusCode() == 400);
          }
        });

    try {
      waitForResult(request);
    } catch (TimeoutException e) {
      assertFalse("Network call timed out", true);
    }
  }

  public void testValidCard() {

    Address address = new Address.Builder()
        .setAddress1("123 main street")
        .setCity("Los Gatos")
        .setState("CA")
        .setPostalCode("95033")
        .setCountry("US")
        .build();

    CardPaymentRequest request = new CardPaymentRequest.Builder()
        .setFirstName("first")
        .setLastName("last")
        .setNumber("4111111111111111")
        .setExpirationMonth(12)
        .setExpirationYear(20)
        .setCvv("123")
        .setBillingAddress(address)
        .build();


    mApi.getPaymentToken(request,
        new RecurlyApi.TokenResponseHandler() {
          @Override
          public void onTokenSuccess(String response) {
            assertTrue(response != null);
          }

          @Override
          public void onTokenFailure(RecurlyError exception) {
            assertTrue(false);
          }
        });

    try {
      waitForResult(request);
    } catch (TimeoutException e) {
      assertFalse("Network call timed out", true);
    }
  }
}
