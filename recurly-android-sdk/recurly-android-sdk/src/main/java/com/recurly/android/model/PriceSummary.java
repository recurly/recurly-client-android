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

public class PriceSummary extends BaseModel {
  private float subtotal;
  private float addons;
  private float discount;
  private float setup_fee;
  private float tax;
  private float total;

  public float getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(float subtotal) {
    this.subtotal = subtotal;
  }

  public float getAddons() {
    return addons;
  }

  public void setAddons(float addons) {
    this.addons = addons;
  }

  public float getDiscount() {
    return discount;
  }

  public void setDiscount(float discount) {
    this.discount = discount;
  }

  public float getSetupFee() {
    return setup_fee;
  }

  public void setSetupFee(float setupFee) {
    this.setup_fee = setupFee;
  }

  public float getTax() {
    return tax;
  }

  public void setTax(float tax) {
    this.tax = tax;
  }

  public float getTotal() {
    return total;
  }

  public void setTotal(float total) {
    this.total = total;
  }

  /**
   * Update total based on other info
   */
  public void updateTotal() {
    total = subtotal - discount + tax;
  }

  @Override
  public String toString() {
    return "PriceSummary{" +
        "subtotal=" + subtotal +
        ", addons=" + addons +
        ", discount=" + discount +
        ", setup_fee=" + setup_fee +
        ", tax=" + tax +
        ", total=" + total +
        '}';
  }

}
