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

package com.recurly.android.model;

public class Coupon extends BaseModel {

  public enum DiscountType {
    DISCOUNT_TYPE_NONE,
    DISCOUNT_TYPE_PERCENT,
    DISCOUNT_TYPE_FIXED_AMOUNT,
  }
  private String code;
  private String name;
  private Discount discount;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDiscount(Discount discount) {
    this.discount = discount;
  }


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



  public float getDiscountRate() {
    if (discount != null) {
      return discount.getRate();
    }
    return 0;
  }

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
