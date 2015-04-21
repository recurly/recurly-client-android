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
 * Encapsulates the details for tax returned from a TaxRequest call
 *
 * @see com.recurly.android.network.request.TaxRequest
 */
public class TaxDTO extends BaseDTO {

  /**
   * Tax instance representing no tax
   */
  public static final TaxDTO NO_TAX = new TaxDTO("all", 0);

  /**
   * The currency type for this tax
   */
  protected String type;

  /**
   * The tax rate
   */
  protected float rate;

  public TaxDTO(String type, float rate) {
    this.type = type;
    this.rate = rate;
  }

  /**
   * See {@link TaxDTO#type}
   */
  public String getType() {
    return type;
  }

  /**
   * See {@link TaxDTO#type}
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * See {@link TaxDTO#rate}
   */
  public float getRate() {
    return rate;
  }

  /**
   * See {@link TaxDTO#rate}
   */
  public void setRate(float rate) {
    this.rate = rate;
  }

  @Override
  public String toString() {
    return "Tax{" +
        "type='" + type + '\'' +
        ", rate=" + rate +
        '}';
  }

}
