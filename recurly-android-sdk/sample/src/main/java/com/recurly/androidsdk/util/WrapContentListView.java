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

package com.recurly.androidsdk.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class WrapContentListView extends ListView {

  private boolean mShouldResize;
  private int mMaxHeight = 0;

  public WrapContentListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setNeedsResize() {
    mShouldResize = true;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (mShouldResize) {

      if (getParent() != null && getParent() instanceof View) {
        View parent = (View) getParent();
        mMaxHeight = parent.getHeight();
      }

      mShouldResize = false;
      int count = getCount();
      android.view.ViewGroup.LayoutParams layoutParams = getLayoutParams();

      int height = 0;
      for (int i=0; i<count; i++) {
        if (getChildAt(i) != null) {
          height += getChildAt(i).getHeight();
        } else {
          mShouldResize = true;
        }
      }

      if (mMaxHeight > 0 && height > mMaxHeight) {
        height = mMaxHeight;
      }
      if (height <= 0) {
        height = 1;
      }

      layoutParams.height = height;
      setLayoutParams(layoutParams);
    }

    super.onDraw(canvas);
  }
}
