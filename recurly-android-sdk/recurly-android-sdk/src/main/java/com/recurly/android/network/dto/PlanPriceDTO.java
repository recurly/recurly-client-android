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

/**
 * Encapsulates the pricing for a Plan in a PlanRequest.  Relates to a specific currency
 *
 * @see PlanDTO
 * @see com.recurly.android.Constants.CurrencyType
 */
public class PlanPriceDTO extends BaseDTO {
  /**
   * The setup fee for the plan, if any
   */
  protected float setup_fee;
  /**
   * The cost of the plan on a recurring basis
   */
  protected float unit_amount;

  /**
   * The symbol for the currency of this plan
   */
  protected String symbol;

  /**
   * See {@link PlanPriceDTO#setup_fee}
   */
  public float getSetupFee() {
    return setup_fee;
  }

  /**
   * See {@link PlanPriceDTO#setup_fee}
   */
  public void setSetupFee(float setupFee) {
    this.setup_fee = setupFee;
  }

  /**
   * See {@link PlanPriceDTO#unit_amount}
   */
  public float getUnitAmount() {
    return unit_amount;
  }

  /**
   * See {@link PlanPriceDTO#unit_amount}
   */
  public void setUnitAmount(float unitAmount) {
    this.unit_amount = unitAmount;
  }

  /**
   * See {@link PlanPriceDTO#symbol}
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * See {@link PlanPriceDTO#symbol}
   */
  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String toString() {
    return "Price{" +
        "setup_fee=" + setup_fee +
        ", unit_amount=" + unit_amount +
        ", symbol='" + symbol + '\'' +
        '}';
  }
}
