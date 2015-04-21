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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plan extends BaseModel {

  private String code;
  private String name;
  private Map<String, PlanPrice> price;
  private PlanPeriod period;
  private boolean tax_exempt;
  private PlanPeriod trial;
  private List<Addon> addons;

  private HashMap<String, Addon> addonsByCode = new HashMap<String, Addon>();

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

  public Map<String, PlanPrice> getPrice() {
    return price;
  }

  public void setPrice(Map<String, PlanPrice> price) {
    this.price = price;
  }

  public PlanPeriod getPeriod() {
    return period;
  }

  public void setPeriod(PlanPeriod period) {
    this.period = period;
  }

  public boolean isTaxExempt() {
    return tax_exempt;
  }

  public void setTaxExempt(boolean taxExempt) {
    this.tax_exempt = taxExempt;
  }

  public PlanPeriod getTrial() {
    return trial;
  }

  public void setTrial(PlanPeriod trial) {
    this.trial = trial;
  }

  public List<Addon> getAddons() {
    return addons;
  }

  public void setAddons(List<Addon> addons) {
    this.addons = addons;
  }

  public Addon getAddon(String code) {
    if (addons == null) {
      return null;
    }

    if (addonsByCode.size() != addons.size()) {
      addonsByCode = new HashMap<String, Addon>();
      for (Addon addon : addons) {
        addonsByCode.put(addon.getCode(), addon);
      }
    }

    return addonsByCode.get(code);

  }

  public PlanPrice getPricing() {
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
