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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.recurly.androidsdk.MainActivity;
import com.recurly.androidsdk.R;

import java.util.HashMap;

public class FragmentSelectorFragment extends BaseFragment {

  private enum FragmentType {
    FRAGMENT_TYPE_TOKEN,
    FRAGMENT_TYPE_TAXES,
    FRAGMENT_TYPE_PLAN,
    FRAGMENT_TYPE_COUPON,
    FRAGMENT_TYPE_PRICING,
    FRAGMENT_TYPE_KALE_BASIC,
    FRAGMENT_TYPE_KALE_ADVANCED,
  }
  private static HashMap<FragmentType, FragmentInfo> sFragmentInfo = new HashMap<FragmentType, FragmentInfo>();

  static {

    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_TOKEN, new FragmentInfo(TokenFragment.class, "Token from Credit Card"));
    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_TAXES, new FragmentInfo(TaxesFragment.class, "Find tax rate"));
    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_PLAN, new FragmentInfo(PlanFragment.class, "Display plan info"));
    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_COUPON, new FragmentInfo(CouponFragment.class, "Check coupon"));
    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_PRICING, new FragmentInfo(PricingFragment.class, "Show pricing"));

    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_KALE_BASIC, new FragmentInfo(KaleBasicFragment.class, "Subscription Minimal (Kale Kart)"));
    sFragmentInfo.put(FragmentType.FRAGMENT_TYPE_KALE_ADVANCED, new FragmentInfo(KaleAdvancedFragment.class, "Subscription Advanced (Kale Kart)"));
  }

  private ListView mListView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_selector, container, false);

    return view;

  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mListView = (ListView) view.findViewById(R.id.list_view);

    FragmentSelectorAdapter adapter = new FragmentSelectorAdapter();
    mListView.setAdapter(adapter);

    mListView.setOnItemClickListener(adapter);

  }

  private class FragmentSelectorAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

    public FragmentSelectorAdapter() {
      super();

    }

    @Override
    public int getCount() {
      return sFragmentInfo.size();
    }

    @Override
    public FragmentInfo getItem(int position) {
      FragmentType type = FragmentType.values()[position];

      FragmentInfo info = sFragmentInfo.get(type);

      return info;
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)  {

      if (convertView == null) {
        convertView = LayoutInflater.from(getActivity()).inflate(R.layout.row_fragment_selector, parent, false);
      }

      TextView textView = (TextView) convertView.findViewById(R.id.text_view);

      FragmentInfo info = getItem(position);

      textView.setText(info.displayText);

      return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      FragmentInfo info = getItem(position);

      ((MainActivity)getActivity()).pushFragment(info.fragmentClass);

    }
  }

  private static class FragmentInfo<T extends Fragment> {
    String displayText;
    Class<T> fragmentClass;

    private FragmentInfo(Class<T> fragmentClass, String displayText) {
      this.displayText = displayText;
      this.fragmentClass = fragmentClass;
    }
  }

}
