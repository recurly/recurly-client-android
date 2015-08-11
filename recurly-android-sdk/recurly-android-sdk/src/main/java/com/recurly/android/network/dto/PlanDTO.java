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
import com.recurly.android.network.request.PlanRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates plan details returned from getPlan call
 *
 * @see com.recurly.android.network.request.PlanRequest
 * @see com.recurly.android.RecurlyApi#getPlan(PlanRequest, RecurlyApi.PlanResponseHandler)
 */
public class PlanDTO extends BaseDTO {

    /**
     * The unique code for this plan.
     */
    protected String code;

    /**
     * The display name of this plan.
     */
    protected String name;

    /**
     * The pricing details of this plan, mapped by currency type.
     *
     * @see com.recurly.android.Constants.CurrencyType
     */
    protected Map<String, PlanPriceDTO> price;

    /**
     * The details about the frequency of this plan.
     */
    protected PlanPeriodDTO period;

    /**
     * Whether or not the plan is tax exempt.
     */
    protected boolean tax_exempt;

    /**
     * The duration of the trial period of this plan, if any.
     */
    protected PlanPeriodDTO trial;

    /**
     * A list of add ons available for this plan.
     */
    protected List<AddonDTO> addons;

    /**
     * Helper map listing addons by add on code.
     */
    protected HashMap<String, AddonDTO> addonsByCode = new HashMap<String, AddonDTO>();

    /**
     * See {@link PlanDTO#code}
     */
    public String getCode() {
        return code;
    }

    /**
     * See {@link PlanDTO#code}
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * See {@link PlanDTO#name}
     */
    public String getName() {
        return name;
    }

    /**
     * See {@link PlanDTO#name}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * See {@link PlanDTO#price}
     */
    public Map<String, PlanPriceDTO> getPrice() {
        return price;
    }

    /**
     * See {@link PlanDTO#price}
     */
    public void setPrice(Map<String, PlanPriceDTO> price) {
        this.price = price;
    }

    /**
     * See {@link PlanDTO#period}
     */
    public PlanPeriodDTO getPeriod() {
        return period;
    }

    /**
     * See {@link PlanDTO#period}
     */
    public void setPeriod(PlanPeriodDTO period) {
        this.period = period;
    }

    /**
     * See {@link PlanDTO#tax_exempt}
     */
    public boolean isTaxExempt() {
        return tax_exempt;
    }

    /**
     * See {@link PlanDTO#tax_exempt}
     */
    public void setTaxExempt(boolean taxExempt) {
        this.tax_exempt = taxExempt;
    }

    /**
     * See {@link PlanDTO#trial}
     */
    public PlanPeriodDTO getTrial() {
        return trial;
    }

    /**
     * See {@link PlanDTO#trial}
     */
    public void setTrial(PlanPeriodDTO trial) {
        this.trial = trial;
    }

    /**
     * See {@link PlanDTO#addons}
     */
    public List<AddonDTO> getAddons() {
        return addons;
    }

    /**
     * See {@link PlanDTO#addons}
     */
    public void setAddons(List<AddonDTO> addons) {
        this.addons = addons;
    }

    /**
     * Helper method to look up an add on by code for this plan.
     *
     * @param code The add on code.
     * @return The AddOn, if exists, null otherwise.
     */
    public AddonDTO getAddon(String code) {
        if (addons == null) {
            return null;
        }

        if (addonsByCode.size() != addons.size()) {
            addonsByCode = new HashMap<String, AddonDTO>();
            for (AddonDTO addon : addons) {
                addonsByCode.put(addon.getCode(), addon);
            }
        }

        return addonsByCode.get(code);

    }

    /**
     * Helper method that returns pricing details for the default currency
     *
     * @return The plan's pricing details for the default currency
     * @see BaseDTO#sCurrency
     */
    public PlanPriceDTO getPricing() {
        return price.get(sCurrency);
    }

    @Override
    public String toString() {
        return "Plan{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", period=" + period +
                ", tax_exempt=" + tax_exempt +
                ", trial=" + trial +
                ", addons=" + addons +
                '}';
    }
}
