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

package com.recurly.androidsdk.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Addon;
import com.recurly.android.model.BaseModel;
import com.recurly.android.model.CartSummary;
import com.recurly.android.model.Plan;
import com.recurly.android.model.PriceSummary;
import com.recurly.android.model.Pricing;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.PricingRequest;
import com.recurly.androidsdk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PricingFragment extends BaseFragment {

  private EditText mInputPostalCode;
  private EditText mInputCountryCode;
  private EditText mInputCouponCode;
  private EditText mInputPlanCode;
  private EditText mInputVatNumber;

  private EditText mInputAddonCode;
  private EditText mInputAddonQuantity;
  private TextView mTextResult;

  private View mButtonSubmit;
  private View mButtonAddAddon;
  private View mButtonClearAddons;

  private ListView mListViewAddons;
  private ListView mListView;

  private AddonAdapter mAddonAdapter = new AddonAdapter();

  private HashMap<String, Integer> mAddons = new HashMap<String, Integer>();

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_pricing, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputCountryCode = (EditText) view.findViewById(R.id.input_country);
    mInputPostalCode = (EditText) view.findViewById(R.id.input_postal_code);
    mInputCouponCode = (EditText) view.findViewById(R.id.input_coupon);
    mInputPlanCode = (EditText) view.findViewById(R.id.input_plan);
    mInputVatNumber = (EditText) view.findViewById(R.id.input_vat_number);
    mInputAddonCode = (EditText) view.findViewById(R.id.input_addon_code);
    mInputAddonQuantity = (EditText) view.findViewById(R.id.input_addon_quantity);

    mTextResult = (TextView) view.findViewById(R.id.text_result);

    mButtonSubmit = view.findViewById(R.id.button_submit);
    mButtonAddAddon = view.findViewById(R.id.button_addon_add);
    mButtonClearAddons = view.findViewById(R.id.button_addon_clear);

    mListView = (ListView) view.findViewById(R.id.list_view);
    mListViewAddons = (ListView) view.findViewById(R.id.list_view_addons);

    mListView.setVisibility(View.INVISIBLE);

    mListViewAddons.setAdapter(mAddonAdapter);

    mButtonAddAddon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();

        int quantity = 0;
        String addon = mInputAddonCode.getText().toString();

        try {
          quantity = Integer.parseInt(mInputAddonQuantity.getText().toString());
        } catch (Exception ex) {

        }

        if (addon.isEmpty()) {
          mTextResult.setText("Addon code not specified");
          mTextResult.setTextColor(Color.RED);
          return;
        }

        if (quantity <= 0) {
          mTextResult.setText("Addon quantity not specified");
          mTextResult.setTextColor(Color.RED);
          return;
        }


        int oldQuantity = 0;
        if (mAddons.containsKey(addon)) {
          oldQuantity = mAddons.get(addon);
        }

        mAddons.put(addon, oldQuantity+quantity);
        mAddonAdapter.notifyDataSetChanged();

      }
    });

    mButtonClearAddons.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        mAddons.clear();
        mAddonAdapter.notifyDataSetChanged();
      }
    });

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        hideKeyboard();
        getRecurlyApi().getPricing(new PricingRequest.Builder()
                .setCountry(mInputCountryCode.getText().toString())
                .setCoupon(mInputCouponCode.getText().toString())
                .setPlan(mInputPlanCode.getText().toString())
                .setPlanQuantity(1)
                .setPostalCode(mInputPostalCode.getText().toString())
                .setVatNumber(mInputVatNumber.getText().toString())
                .setAddons(mAddons)
                .build(),
            new RecurlyApi.PricingResponseHandler() {
              @Override
              public void onPricingSuccess(Pricing pricing) {
                mTextResult.setText("Pricing info:");
                mTextResult.setTextColor(Color.BLACK);
                mListView.setVisibility(View.VISIBLE);

                mListView.setAdapter(new PricingAdapter(pricing));

              }

              @Override
              public void onPricingFailure(RecurlyError exception) {
                mTextResult.setText(exception.getErrorMessage());
                mTextResult.setTextColor(Color.RED);
                mListView.setVisibility(View.INVISIBLE);
              }
            });
      }
    });


  }



  private class AddonAdapter extends BaseAdapter {

    public AddonAdapter() {
      super();
    }

    @Override
    public int getCount() {
      return mAddons.size();
    }

    @Override
    public String getItem(int position) {

      ArrayList<String> keys = new ArrayList(mAddons.keySet());

      Collections.sort(keys);

      return keys.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_addon, parent, false);
      }

      String addon = getItem(position);
      int quantity = mAddons.get(addon);

      TextView addonCode = (TextView) convertView.findViewById(R.id.text_addon_code);
      TextView addonQuantity = (TextView) convertView.findViewById(R.id.text_addon_quantity);

      addonCode.setText(addon);
      addonQuantity.setText(String.format("%d", quantity));

      return convertView;
    }
  }

  private class PricingAdapter extends BaseAdapter {

    private Pricing mPricing;

    public PricingAdapter(Pricing pricing) {
      super();

      mPricing = pricing;
    }

    private int numberOfRowsForCart(CartSummary cartSummary) {

      int addonSize = cartSummary.getAddons() == null ? 0 : cartSummary.getAddons().size();

      if (cartSummary.getPlan().getSetupFee() > 0) {
        return 2 + addonSize;
      }
      return 1 + addonSize;
    }

    private int numberOfRowsForPricing(PriceSummary priceSummary) {

      // subtotal, tax, total
      int total = 3;

      // don't include setup fee, should be included in subtotal
//    if (summary.setupFee > 0) {
//        total++;
//    }

      if (priceSummary.getDiscount() > 0) {
        total++;
      }

      // don't include addons, should be included in subtotal
//    if (summary.addons > 0) {
//        total++;
//    }

      return total;
    }

    @Override
    public int getCount() {

      return 3 + // section headers
          numberOfRowsForCart(mPricing.getBase()) +
          numberOfRowsForPricing(mPricing.getNow()) +
          numberOfRowsForPricing(mPricing.getNext());
    }


    private FieldValue getCartItem(int position, CartSummary cartSummary) {

      FieldValue fieldValue = new FieldValue();

      boolean hasSetupFee = cartSummary.getPlan().getSetupFee() > 0;

      int addOnStartIndex = hasSetupFee ? 2 : 1;

      if (position < addOnStartIndex) {
        if (position == 0 && hasSetupFee) {
          // setup fee, if it has one..
          fieldValue.field = "Setup Fee";
          fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), cartSummary.getPlan().getSetupFee());

          return fieldValue;
        } else {
          // otherwise plan fee
          fieldValue.field = "Plan Fee";
          fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), cartSummary.getPlan().getUnit());

          return fieldValue;
        }
      } else {
        int addOnIndex = position - addOnStartIndex;

        ArrayList<String> addonsSorted = new ArrayList<String>(cartSummary.getAddons().keySet());
        Collections.sort(addonsSorted);

        String addonKey = addonsSorted.get(addOnIndex);
        float cost = cartSummary.getAddons().get(addonKey);

        fieldValue.field = "Addon " + addonKey;
        fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), cost);

        return fieldValue;
      }

    }
    private FieldValue getPricingItem(int position, PriceSummary pricingSummary) {

      FieldValue fieldValue = new FieldValue();

      // subtotal always first..

      if (position == 0) {
        fieldValue.field = "Subtotal";
        fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), pricingSummary.getSubtotal());
        return fieldValue;
      }
      if (position == 1) {
        // always tax
        fieldValue.field = "Tax";
        fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), pricingSummary.getTax());
        return fieldValue;
      }
      if (position == 2 && pricingSummary.getDiscount() > 0) {
        fieldValue.field = "Discount";
        fieldValue.value = String.format("- %s%.2f", mPricing.getCurrencySymbol(), pricingSummary.getDiscount());
        return fieldValue;
      }

      // total last
      fieldValue.field = "Total";
      fieldValue.value = String.format("%s%.2f", mPricing.getCurrencySymbol(), pricingSummary.getTotal());
      return fieldValue;

    }


    @Override
    public FieldValue getItem(int position) {

      FieldValue fieldValue = new FieldValue();

      int cartRows = numberOfRowsForCart(mPricing.getBase());

      int nowRows = numberOfRowsForPricing(mPricing.getNow());

      int recurringRows = numberOfRowsForPricing(mPricing.getNext());

      if (position == 0) {
        // cart header
        fieldValue.bold = true;
        fieldValue.highlighted = true;
        fieldValue.field = "Cart Summary";
        return fieldValue;
      }
      if (position < cartRows + 1) {
        return getCartItem(position - 1, mPricing.getBase());
      }
      if (position == cartRows + 1) {
        // now header
        fieldValue.bold = true;
        fieldValue.highlighted = true;
        fieldValue.field = "Due now";
        return fieldValue;
      }
      if (position < cartRows + 1 + nowRows + 1) {
        return getPricingItem(position - 1 - cartRows - 1, mPricing.getNow());
      }
      if (position == cartRows + 1 + nowRows + 1) {
        fieldValue.bold = true;
        fieldValue.highlighted = true;
        fieldValue.field = "Recurring charges";
        return fieldValue;
      }
      return getPricingItem(position - 1 - cartRows - 1 - nowRows - 1, mPricing.getNext());
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_pricing_info, parent, false);
      }

      FieldValue fieldValue = getItem(position);

      TextView fieldTextView = (TextView) convertView.findViewById(R.id.text_field);
      TextView valueTextView = (TextView) convertView.findViewById(R.id.text_value);

      SpannableStringBuilder sb = new SpannableStringBuilder();
      sb.append(fieldValue.field);

      if (fieldValue.bold) {
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      }

      if (fieldValue.highlighted) {
        convertView.setBackgroundColor(getResources().getColor(R.color.grey));
      } else {
        convertView.setBackgroundColor(Color.TRANSPARENT);
      }
      fieldTextView.setText(sb);
      valueTextView.setText(fieldValue.value);

      return convertView;
    }
  }

  private class FieldValue {
    private String field;
    private String value;
    private boolean bold = true;
    private boolean highlighted = false;
  }

}
