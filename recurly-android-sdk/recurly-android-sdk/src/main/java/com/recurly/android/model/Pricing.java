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

public class Pricing {

    private PriceSummary now;  // TODO: rename to initialPrice
    private PriceSummary next; // TODO: rename to recurringPrice
    private CartSummary base;  // TODO: rename to cartItems

    private String currencyCode;
    private String currencySymbol;

    public PriceSummary getNow() {
        return now;
    }

    public void setNow(PriceSummary now) {
        this.now = now;
    }

    public PriceSummary getNext() {
        return next;
    }

    public void setNext(PriceSummary next) {
        this.next = next;
    }

    public CartSummary getBase() {
        return base;
    }

    public void setBase(CartSummary base) {
        this.base = base;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "now=" + now +
                ", next=" + next +
                ", base=" + base +
                ", currencyCode='" + currencyCode + '\'' +
                ", currencySymbol='" + currencySymbol + '\'' +
                '}';
    }
}
