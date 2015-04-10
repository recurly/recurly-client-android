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
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.recurly.android.RecurlyApi;
import com.recurly.android.model.Addon;
import com.recurly.android.model.Coupon;
import com.recurly.android.model.Plan;
import com.recurly.android.network.RecurlyError;
import com.recurly.android.network.request.CouponRequest;
import com.recurly.android.network.request.PlanRequest;
import com.recurly.androidsdk.MainActivity;
import com.recurly.androidsdk.R;

public class PlanFragment extends BaseFragment {

  private EditText mInputPlanCode;
  private TextView mTextResult;
  private ListView mListView;

  private View mButtonSubmit;


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_plan, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mInputPlanCode = (EditText) view.findViewById(R.id.input_plan);

    mTextResult = (TextView) view.findViewById(R.id.text_result);

    mListView = (ListView) view.findViewById(R.id.list_view);

    mButtonSubmit = view.findViewById(R.id.button_submit);

    mListView.setVisibility(View.INVISIBLE);

    mButtonSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideKeyboard();
        getRecurlyApi().getPlan(new PlanRequest.Builder()
                .setPlanCode(mInputPlanCode.getText().toString())
                .build(),
            new RecurlyApi.PlanResponseHandler() {
              @Override
              public void onPlanSuccess(Plan plan) {

                mTextResult.setText("Plan info:");
                mTextResult.setTextColor(Color.BLACK);
                mListView.setVisibility(View.VISIBLE);

                mListView.setAdapter(new PlanAdapter(plan));
              }

              @Override
              public void onPlanFailure(RecurlyError exception) {
                mTextResult.setText(exception.getErrorMessage());
                mTextResult.setTextColor(Color.RED);
                mListView.setVisibility(View.INVISIBLE);
              }
            });
      }
    });
  }


  private enum PlanField {
    PLAN_FIELD_NAME,
    PLAN_FIELD_CODE,
    PLAN_FIELD_SETUP_FEE,
    PLAN_FIELD_RECURRING_FEE,
    PLAN_FIELD_TRIAL_DURATION,
    PLAN_FIELD_ADDON_HEADER,
  }

  private class PlanAdapter extends BaseAdapter {

    private Plan mPlan;

    public PlanAdapter(Plan plan) {
      super();

      mPlan = plan;
    }

    @Override
    public int getCount() {
      int addonCount = mPlan.getAddons() == null ? 0 : mPlan.getAddons().size();

      return PlanField.values().length + addonCount;
    }

    @Override
    public FieldValue getItem(int position) {

      FieldValue fieldValue = new FieldValue();

      if (position < PlanField.values().length) {
        PlanField field = PlanField.values()[position];
        switch (field) {
          case PLAN_FIELD_NAME: {
            fieldValue.field = "Name:";
            fieldValue.value = mPlan.getName();
            break;
          }
          case PLAN_FIELD_CODE: {
            fieldValue.field = "Code:";
            fieldValue.value = mPlan.getCode();
            break;
          }
          case PLAN_FIELD_SETUP_FEE: {
            fieldValue.field = "Setup fee:";
            fieldValue.value = String.format("%.2f %s", mPlan.getPricing().getSetupFee(), mPlan.getCurrency());
            break;
          }
          case PLAN_FIELD_RECURRING_FEE: {
            fieldValue.field = "Setup fee:";
            fieldValue.value = String.format("%.2f %s/%d %s",
                mPlan.getPricing().getUnitAmount(), mPlan.getCurrency(),
                mPlan.getPeriod().getLength(), mPlan.getPeriod().getInterval());
            break;
          }
          case PLAN_FIELD_TRIAL_DURATION: {
            fieldValue.field = "Trial duration:";
            fieldValue.value = String.format("%d %s",
                mPlan.getTrial().getLength(), mPlan.getTrial().getInterval());
            break;
          }
          case PLAN_FIELD_ADDON_HEADER: {
            fieldValue.field = "Addons:";
            if (mPlan.getAddons() == null || mPlan.getAddons().size() == 0) {
              fieldValue.value = "none";
            }
            break;
          }

        }
      } else {
        // addon...

        Addon addon = mPlan.getAddons().get(position - PlanField.values().length);

        fieldValue.bold = false;
        fieldValue.field = "\t" + addon.getCode();
        fieldValue.value = String.format("%.2f %s",
            addon.getPricing().getUnitAmount(), mPlan.getCurrency());
      }

      return fieldValue;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)  {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_plan_info, parent, false);
      }

      FieldValue fieldValue = getItem(position);

      TextView fieldTextView = (TextView) convertView.findViewById(R.id.text_field);
      TextView valueTextView = (TextView) convertView.findViewById(R.id.text_value);

      SpannableStringBuilder sb = new SpannableStringBuilder();
      sb.append(fieldValue.field);

      if (fieldValue.bold) {
        sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
  }
}
