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
 * Encapsulates an addon included in a plan.
 * <p>
 * Instances of this class are populated in a response with a Plan instance
 *
 * @see PlanDTO
 * @see com.recurly.android.network.request.PlanRequest
 */

public class AddonDTO extends BaseDTO {

    /**
     * Unique identifier for this add on
     */
    protected String code;
    /**
     * Display name for add on
     */
    protected String name;
    /**
     * Quantity of this add on available for this plan
     */
    protected int quantity;
    /**
     * Pricing for this add on, mapped by currency type
     */
    protected Map<String, AddonPricingDTO> price;

    /**
     * See {@link AddonDTO#code}
     */
    public String getCode() {
        return code;
    }

    /**
     * See {@link AddonDTO#code}
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * See {@link AddonDTO#name}
     */
    public String getName() {
        return name;
    }

    /**
     * See {@link AddonDTO#name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * See {@link AddonDTO#quantity}
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * See {@link AddonDTO#quantity}
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * See {@link AddonDTO#price}
     */
    public Map<String, AddonPricingDTO> getPrice() {
        return price;
    }

    /**
     * See {@link AddonDTO#price}
     */
    public void setPrice(Map<String, AddonPricingDTO> price) {
        this.price = price;
    }

    /**
     * Helper method that returns the pricing for the configured currency
     *
     * @return The pricing for the default currency
     * @see BaseDTO#sCurrency
     * @see com.recurly.android.RecurlyValidator#validateCurrency(String)
     */
    public AddonPricingDTO getPricing() {
        return price.get(sCurrency);
    }

    @Override
    public String toString() {
        return "Addon{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
