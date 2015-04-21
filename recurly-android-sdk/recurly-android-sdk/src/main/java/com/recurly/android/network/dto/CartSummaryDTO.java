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

import java.util.Map;

/**
 * Encapsulates the shopping cart details for pricing details from a PricingRequest call
 *
 * @see PricingDTO
 * @see com.recurly.android.network.request.PricingRequest
 */
public class CartSummaryDTO extends BaseDTO {

  /**
   * The pricing details for the plan
   */
  protected CartPlanPricingDTO plan;

  /**
   * The pricing details for the add ons selected, by add on code
   *
   * @see AddonDTO
   * @see AddonDTO#code
   */
  protected Map<String, Float> addons;

  /**
   * See {@link CartSummaryDTO#plan}
   */
  public CartPlanPricingDTO getPlan() {
    return plan;
  }

  /**
   * See {@link CartSummaryDTO#plan}
   */
  public void setPlan(CartPlanPricingDTO plan) {
    this.plan = plan;
  }

  /**
   * See {@link CartSummaryDTO#addons}
   */
  public Map<String, Float> getAddons() {
    return addons;
  }

  /**
   * See {@link CartSummaryDTO#addons}
   */
  public void setAddons(Map<String, Float> addons) {
    this.addons = addons;
  }

  @Override
  public String toString() {
    return "CartSummary{" +
        "plan=" + plan +
        ", addons=" + addons +
        '}';
  }
}
