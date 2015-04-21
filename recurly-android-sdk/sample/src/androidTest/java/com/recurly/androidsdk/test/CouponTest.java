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
import com.recurly.android.network.dto.CouponDTO;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.androidsdk.MainActivity;

import java.util.concurrent.TimeoutException;

public class CouponTest extends ApiTest {
  public CouponTest() {
    super(MainActivity.class);
  }

  public void testInvalidRequest() {

    CouponRequest request = new CouponRequest.Builder()
        .setPlanCode("kale_fan")
        .setCouponCode("INVALIDCOUPON")
        .build();

    mApi.getCoupon(request, new RecurlyApi.CouponResponseHandler() {
      @Override
      public void onCouponSuccess(CouponDTO coupon) {
        assertTrue(false);
      }

      @Override
      public void onCouponFailure(RecurlyError exception) {
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


  public void testPercentCouponRequest() {

    CouponRequest request = new CouponRequest.Builder()
        .setPlanCode("kale_fan")
        .setCouponCode("cyberpercent")
        .build();

    mApi.getCoupon(request, new RecurlyApi.CouponResponseHandler() {
      @Override
      public void onCouponSuccess(CouponDTO coupon) {
        assertTrue(coupon != null);
        assertTrue(coupon.getDiscountType() == CouponDTO.DiscountType.DISCOUNT_TYPE_PERCENT);
        assertTrue(coupon.getDiscountRate() > 0);
      }

      @Override
      public void onCouponFailure(RecurlyError exception) {
        assertTrue(false);
      }
    });


    try {
      waitForResult(request);
    } catch (TimeoutException e) {
      assertFalse("Network call timed out", true);
    }
  }

  public void testFixedCouponRequest() {

    CouponRequest request = new CouponRequest.Builder()
        .setPlanCode("kale_fan")
        .setCouponCode("cyberfixed")
        .build();

    mApi.getCoupon(request, new RecurlyApi.CouponResponseHandler() {
      @Override
      public void onCouponSuccess(CouponDTO coupon) {
        assertTrue(coupon != null);
        assertTrue(coupon.getDiscountType() == CouponDTO.DiscountType.DISCOUNT_TYPE_FIXED_AMOUNT);
        assertTrue(coupon.getDiscountRate() == 0);
        assertTrue(coupon.getDiscountAmount() > 0);
      }

      @Override
      public void onCouponFailure(RecurlyError exception) {
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
