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

package com.recurly.android.network.request;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Addon;
import com.recurly.android.model.BaseModel;
import com.recurly.android.model.CartSummary;
import com.recurly.android.model.Coupon;
import com.recurly.android.model.Plan;
import com.recurly.android.model.PlanPrice;
import com.recurly.android.model.PlanPricing;
import com.recurly.android.model.PriceSummary;
import com.recurly.android.model.Pricing;
import com.recurly.android.model.Tax;
import com.recurly.android.network.RecurlyError;

import java.util.HashMap;
import java.util.Map;

public class PricingRequest extends WrapperRequest {
  private String planCode;
  private int planQuantity;

  private String couponCode;
  private Map<String, Integer> addons;

  private String currency;
  private String country;
  private String postalCode;
  private String vatNumber;

  private PricingFetchHandler mFetchHandler;
  private RecurlyApi.PricingResponseHandler mHandler;

  protected Pricing mLastPricing;

  protected Plan mPlan;
  protected Coupon mCoupon;
  protected Tax mTax;
  protected HashMap<String, Float> mAddonTotals;


  public PricingRequest(String planCode, int planQuantity, String couponCode, Map<String, Integer> addons, String country, String postalCode, String vatNumber) {
    this.planCode = planCode;
    this.planQuantity = planQuantity;
    this.couponCode = couponCode;
    this.addons = addons;
    this.currency = BaseModel.getCurrency();
    this.country = country;
    this.postalCode = postalCode;
    this.vatNumber = vatNumber;
  }

  public String getPlanCode() {
    return planCode;
  }

  public void setPlanCode(String planCode) {
    this.planCode = planCode;
  }

  public int getPlanQuantity() {
    return planQuantity;
  }

  public void setPlanQuantity(int planQuantity) {
    this.planQuantity = planQuantity;
  }

  public String getCouponCode() {
    return couponCode;
  }

  public void setCouponCode(String couponCode) {
    this.couponCode = couponCode;
  }

  public Map<String, Integer> getAddons() {
    return addons;
  }

  public void setAddons(Map<String, Integer> addons) {
    this.addons = addons;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getVatNumber() {
    return vatNumber;
  }

  public void setVatNumber(String vatNumber) {
    this.vatNumber = vatNumber;
  }

  @Override
  public RecurlyError validate() {
    if (planCode == null) {
      return RecurlyError.validationError("plan code");
    }
    if (planQuantity <= 0) {
      return RecurlyError.validationError("plan quantity");
    }
    if (currency == null) {
      return RecurlyError.validationError("currency");
    }

    return null;
  }

  protected void setRequiresRefetch() {
    mLastPricing = null;

    if (mFetchHandler != null && mFetchHandler.isUpdating()) {
      mFetchHandler.cancel();
    }


  }

  public boolean requiresUpdate() {
    if (mLastPricing == null) {
      return true;
    }

    return false;
  }

  protected RecurlyError recalculate() {

    // set pricing and return no error..
    mLastPricing = null;


    if (mPlan == null) {
      // No plan fetched, logical error
      return RecurlyError.genericServerError("Plan not found for pricing request");
    }

    PlanPrice planPrice = mPlan.getPricing();
    if (planPrice == null) {
      // price for specified currency not found
      return RecurlyError.genericServerError("Plan pricing not found for pricing request");
    }


    float setupFee = planPrice.getSetupFee();

    HashMap<String, Float> addonsChosen = new HashMap<String, Float>();

    float addonTotal = 0;

    if (addons != null && addons.size() > 0) {
      for (Map.Entry<String, Integer> entry : addons.entrySet()) {
        int quantity = entry.getValue();
        String addonName = entry.getKey();

        Addon addon = mPlan.getAddon(addonName);

        if (addon != null) {
          if (addon.getPricing() != null) {
            float addonPrice = addon.getPricing().getUnitAmount();
            addonTotal += addonPrice * quantity;

            addonsChosen.put(addonName, addonPrice * quantity);
          } else {
            // TODO: handle error - price in specified currency not found
          }

        } else {
          // TODO: handle error - addon not found
        }
      }

    }

    mAddonTotals = addonsChosen;



    float discountRate = 0;
    float discountTotal = 0;



    if (mCoupon != null) {
      switch (mCoupon.getDiscountType()) {
        case DISCOUNT_TYPE_PERCENT: {
          discountRate = mCoupon.getDiscountRate();
          break;
        }
        case DISCOUNT_TYPE_FIXED_AMOUNT: {
          discountTotal = mCoupon.getDiscountAmount();
          break;
        }
        default:
          // TODO: error - coupon not applied error?
          break;
      }
    }

    // calculate subtotal
    float planCost = planPrice.getUnitAmount() * planQuantity;

    float taxRate = 0;
    if (mTax != null) {
      taxRate = mTax.getRate();
    }

    // currency
    String currencySymbol = planPrice.getSymbol();

    // cart

    CartSummary cartSummary = new CartSummary();

    cartSummary.setPlan(new PlanPricing());
    cartSummary.getPlan().setSetupFee(setupFee);
    cartSummary.getPlan().setUnit(planPrice.getUnitAmount());

    // TODO: verify
    cartSummary.setAddons(mAddonTotals);

    // price summaries

    PriceSummary now = new PriceSummary();
    PriceSummary recurring = new PriceSummary();

    now.setSetupFee(setupFee);

    now.setAddons(addonTotal);
    recurring.setAddons(addonTotal);

    now.setSubtotal(planCost + addonTotal + setupFee);
    recurring.setSubtotal(planCost + addonTotal);

    if (discountRate > 0) {
      now.setDiscount(discountRate * now.getSubtotal());
      recurring.setDiscount(discountRate * recurring.getSubtotal());
    } else {
      now.setDiscount(discountTotal);
      recurring.setDiscount(discountTotal);
    }

    if (taxRate > 0) {
      now.setTax(taxRate * now.getSubtotal());
      recurring.setTax(taxRate * recurring.getSubtotal());
    }

    now.updateTotal();
    recurring.updateTotal();

    Pricing pricing = new Pricing();
    pricing.setNext(recurring);
    pricing.setNow(now);
    pricing.setBase(cartSummary);
    pricing.setCurrencyCode(currency);
    pricing.setCurrencySymbol(currencySymbol);

    mLastPricing = pricing;

    return null;
  }

  protected void recalculateAndNotify() {
    mFinished = true;
    RecurlyError error = recalculate();

    if (mHandler != null) {
      if (error != null) {
        mHandler.onPricingFailure(error);
      } else {
        mHandler.onPricingSuccess(mLastPricing);
      }
    }
  }

  protected void completeWithError(RecurlyError error) {
    mFinished = true;
    if (mHandler != null) {
      mHandler.onPricingFailure(error);
    }
  }

  public Pricing getPricing() {
    return mLastPricing;
  }

  public void setHandler(RecurlyApi.PricingResponseHandler handler) {
    mHandler = handler;
  }

  public static class Builder {

    private String mPlan;
    private int mPlanQuantity;
    private String mCoupon;
    private Map<String, Integer> mAddons;
    private String mCountry;
    private String mPostalCode;
    private String mVatNumber;

    public Builder setPlan(String plan) {
      mPlan = plan;
      return this;
    }

    public Builder setPlanQuantity(int planQuantity) {
      mPlanQuantity = planQuantity;
      return this;
    }

    public Builder setCoupon(String coupon) {
      mCoupon = coupon;
      return this;
    }

    public Builder addAddon(String addon, int quantity) {
      if (mAddons == null) {
        mAddons = new HashMap<String, Integer>();
      }

      mAddons.put(addon, quantity);
      return this;
    }

    public Builder setAddons(Map<String, Integer> addons) {
      mAddons = addons;
      return this;
    }

    public Builder setCountry(String country) {
      mCountry = country;
      return this;
    }

    public Builder setPostalCode(String postalCode) {
      mPostalCode = postalCode;
      return this;
    }

    public Builder setVatNumber(String vatNumber) {
      mVatNumber = vatNumber;
      return this;
    }

    public PricingRequest build() {
      return new PricingRequest(mPlan, mPlanQuantity, mCoupon, mAddons, mCountry, mPostalCode, mVatNumber);
    }
  }


  public void fetchRequiredData(RecurlyApi recurlyApi) {

    if (mFetchHandler != null) {
      mFetchHandler.cancel();
    }

    mFetchHandler = new PricingFetchHandler();

    mFetchHandler.update(recurlyApi);
  }

  private class PricingFetchHandler {
    private boolean mCancelled;
    private boolean mUpdating;

    private void fetchPlan(final RecurlyApi recurlyApi) {
      if (mCancelled) {
        return;
      }

      recurlyApi.getPlan(new PlanRequest.Builder()
              .setPlanCode(getPlanCode())
              .build(),
          new RecurlyApi.PlanResponseHandler() {
            @Override
            public void onPlanSuccess(Plan plan) {
              if (mCancelled) {
                return;
              }
              mPlan = plan;
              fetchCoupon(recurlyApi);
            }

            @Override
            public void onPlanFailure(RecurlyError exception) {
              if (mCancelled) {
                return;
              }
              completeWithError(exception);
            }
          });
    }

    private void fetchCoupon(final RecurlyApi recurlyApi) {
      if (mCancelled) {
        return;
      }

      if (getCouponCode() == null || getCouponCode().isEmpty()) {
        fetchTax(recurlyApi);
      } else {
        recurlyApi.getCoupon(new CouponRequest(getPlanCode(), getCouponCode()),
            new RecurlyApi.CouponResponseHandler() {
              @Override
              public void onCouponSuccess(Coupon coupon) {
                if (mCancelled) {
                  return;
                }
                mCoupon = coupon;
                fetchTax(recurlyApi);
              }

              @Override
              public void onCouponFailure(RecurlyError exception) {
                if (mCancelled) {
                  return;
                }
                completeWithError(exception);
              }
            });
      }
    }

    private void fetchTax(final RecurlyApi recurlyApi) {

      if (mCancelled) {
        return;
      }

      if (mPlan.isTaxExempt()) {
        didFetchAll();
      } else {

        if (country == null || country.isEmpty()) {
          completeWithError(RecurlyError.validationError("country code"));
          return;
        }
        if (postalCode == null || postalCode.isEmpty()){
          completeWithError(RecurlyError.validationError("postal code"));
          return;
        }
        recurlyApi.getPostalTax(new TaxRequest.Builder()
                .setCountryCode(getCountry())
                .setPostalCode(getPostalCode()).build(),
            new RecurlyApi.TaxResponseHandler() {
              @Override
              public void onTaxSuccess(Tax tax) {
                if (mCancelled) {
                  return;
                }
                mTax = tax;
                didFetchAll();
              }

              @Override
              public void onTaxFailure(RecurlyError exception) {
                if (mCancelled) {
                  return;
                }
                completeWithError(exception);
              }
            }
        );
      }
    }

    private void didFetchAll() {
      if (mCancelled) {
        return;
      }

      recalculateAndNotify();
    }

    public void cancel() {
      mCancelled = true;
      mFinished = true;
    }

    public void update(RecurlyApi recurlyApi) {

      synchronized (this) {
        if (mUpdating) {
          return;
        }

        mUpdating = true;
        mCancelled = false;
      }

      fetchPlan(recurlyApi);
    }

    public boolean isUpdating() {
      return mUpdating;
    }

  }



}
