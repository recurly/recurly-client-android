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
import com.recurly.android.network.request.CouponRequest;

/**
 * Encapsulates the coupon details from a getCoupon call
 *
 * @see com.recurly.android.network.request.CouponRequest
 * @see com.recurly.android.RecurlyApi#getCoupon(CouponRequest, RecurlyApi.CouponResponseHandler)
 *
 */
public class CouponDTO extends BaseDTO {

  /**
   * The discount type. Either a fixed amount, or a percentage of the total.
   */
  public enum DiscountType {
    DISCOUNT_TYPE_NONE,
    DISCOUNT_TYPE_PERCENT,
    DISCOUNT_TYPE_FIXED_AMOUNT,
  }

  /**
   * The unique coupon code
   */
  protected String code;

  /**
   * The display name for this coupon
   */
  protected String name;

  /**
   * The discount given by this coupon
   */
  protected DiscountDTO discount;

  /**
   * See {@link CouponDTO#code}
   */
  public String getCode() {
    return code;
  }

  /**
   * See {@link CouponDTO#code}
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * See {@link CouponDTO#name}
   */
  public String getName() {
    return name;
  }

  /**
   * See {@link CouponDTO#name}
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * See {@link CouponDTO#discount}
   */
  public void setDiscount(DiscountDTO discount) {
    this.discount = discount;
  }

  /**
   * Parses and returns the DiscountType
   *
   * See {@link CouponDTO#discount}
   */
  public DiscountType getDiscountType() {

    if (discount != null) {
      if (discount.getType().equals("percent")) {
        return DiscountType.DISCOUNT_TYPE_PERCENT;
      } else if (discount.getType().equals("dollars")) {
        return DiscountType.DISCOUNT_TYPE_FIXED_AMOUNT;
      }
    }

    return DiscountType.DISCOUNT_TYPE_NONE;
  }

  /**
   * Helper method that returns the discount applied based on a subtotal.
   * @param total The total that this coupon applies to
   * @return The discount given by this coupon
   */
  public float getDiscount(float total) {
    switch (getDiscountType()) {

      case DISCOUNT_TYPE_NONE:
        return 0;
      case DISCOUNT_TYPE_FIXED_AMOUNT:
        return getDiscountAmount();
      case DISCOUNT_TYPE_PERCENT:
        return total * getDiscountRate();
    }
    return 0;
  }


  /**
   * Helper method to return the discount rate for this coupon.
   *
   * Relies on configured default currency.
   *
   * @return Percentage discount returned, 0 if no discount or not a percent type coupon
   *
   * @see BaseDTO#sCurrency
   */
  public float getDiscountRate() {
    if (discount != null) {
      return discount.getRate();
    }
    return 0;
  }

  /**
   * Helper method to return the fixed discount for this coupon
   *
   * Relies on configured default currency.
   *
   * @return Fixed discount returned, 0 if no discount or not a fixed amount type coupon
   *
   * @see BaseDTO#sCurrency
   */
  public float getDiscountAmount() {
    if (discount != null) {
      if (discount.getAmount() != null) {
        Float amount = discount.getAmount().get(sCurrency);

        if (amount != null) {
          return amount;
        }
      }
    }
    return 0;
  }


  @Override
  public String toString() {
    return "Coupon{" +
        "code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", discount=" + discount +
        '}';
  }
}
