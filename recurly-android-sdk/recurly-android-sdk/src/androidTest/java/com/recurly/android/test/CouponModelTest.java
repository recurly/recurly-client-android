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

package com.recurly.android.test;

import com.google.gson.Gson;
import com.recurly.android.model.Coupon;

public class CouponModelTest extends UnitTest {


  public static String fixedCouponJson = "{\"code\":\"cyberfixed\",\"name\":\"CYBERFIXED\",\"discount\":{\"type\":\"dollars\",\"amount\":{\"USD\":7.0}}}";
  public static String percentCouponJson = "{\"code\":\"cyberpercent\",\"name\":\"CYBERPERCENT\",\"discount\":{\"type\":\"percent\",\"rate\":0.15}}";


  public void testParseFixedCoupon() {

    Coupon coupon = new Gson().fromJson(fixedCouponJson, Coupon.class);

    assertEquals(coupon.getName(), "CYBERFIXED");
    assertEquals(coupon.getCode(), "cyberfixed");
    assertEquals(coupon.getDiscountType(), Coupon.DiscountType.DISCOUNT_TYPE_FIXED_AMOUNT);
    assertEquals(coupon.getDiscountRate(), 0.0f);
    assertEquals(coupon.getDiscountAmount(), 7.0f);

    assertEquals(coupon.getDiscount(1000), 7.0f);
    assertEquals(coupon.getDiscount(10000), 7.0f);
    assertEquals(coupon.getDiscount(100000), 7.0f);

  }
  public void testParsePercentCoupon() {

    Coupon coupon = new Gson().fromJson(percentCouponJson, Coupon.class);

    assertEquals(coupon.getName(), "CYBERPERCENT");
    assertEquals(coupon.getCode(), "cyberpercent");
    assertEquals(coupon.getDiscountType(), Coupon.DiscountType.DISCOUNT_TYPE_PERCENT);
    assertEquals(coupon.getDiscountRate(), 0.15f);
    assertEquals(coupon.getDiscountAmount(), 0.0f);

    assertEquals((int)coupon.getDiscount(1000), 150);
    assertEquals((int)coupon.getDiscount(10000), 1500);
    assertEquals((int)coupon.getDiscount(100000), 15000);

  }
}
