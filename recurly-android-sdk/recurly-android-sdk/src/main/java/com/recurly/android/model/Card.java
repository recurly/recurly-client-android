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

public class Card extends BaseModel {
  private String cardNumber;
  private String nameOnCard;
  private String cvv;
  private int expirationMonth;
  private int expirationYear;

  public Card(String cardNumber, String nameOnCard, String cvv, int expirationMonth, int expirationYear) {
    this.cardNumber = cardNumber;
    this.nameOnCard = nameOnCard;
    this.cvv = cvv;
    this.expirationMonth = expirationMonth;
    this.expirationYear = expirationYear;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getNameOnCard() {
    return nameOnCard;
  }

  public void setNameOnCard(String nameOnCard) {
    this.nameOnCard = nameOnCard;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public int getExpirationMonth() {
    return expirationMonth;
  }

  public void setExpirationMonth(int expirationMonth) {
    this.expirationMonth = expirationMonth;
  }

  public int getExpirationYear() {
    return expirationYear;
  }

  public void setExpirationYear(int expirationYear) {
    this.expirationYear = expirationYear;
  }

  public static class Builder {
    private String mCardNumber;
    private String mNameOnCard;
    private String mCvv;
    private int mExpirationMonth;
    private int mExpirationYear;

    public Builder setCardNumber(String cardNumber) {
      mCardNumber = cardNumber;
      return this;
    }

    public Builder setNameOnCard(String nameOnCard) {
      mNameOnCard = nameOnCard;
      return this;
    }

    public Builder setCvv(String cvv) {
      mCvv = cvv;
      return this;
    }

    public Builder setExpirationMonth(int expirationMonth) {
      mExpirationMonth = expirationMonth;
      return this;
    }

    public Builder setExpirationYear(int expirationYear) {
      mExpirationYear = expirationYear;
      return this;
    }

    public Card build() {
      return new Card(mCardNumber, mNameOnCard, mCvv, mExpirationMonth, mExpirationYear);
    }
  }
}
