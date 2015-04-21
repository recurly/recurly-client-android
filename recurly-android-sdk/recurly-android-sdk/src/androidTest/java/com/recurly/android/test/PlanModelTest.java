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

package com.recurly.android.test;

import com.google.gson.Gson;
import com.recurly.android.network.dto.AddonDTO;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.dto.PlanPeriodDTO;
import com.recurly.android.network.dto.PlanPriceDTO;

public class PlanModelTest extends UnitTest {

  private static String planWithAddonsJson =
      "{\"code\":\"kale_fan\",\"name\":\"Kale Fan\",\"price\":{\"USD\":{\"setup_fee\":0.0,\"unit_amount\":19.99,\"symbol\":\"$\"}},\"period\":{\"interval\":\"months\",\"length\":1},\"tax_exempt\":false," +
          "\"addons\":[{\"code\":\"almonds\",\"name\":\"Sliced Almonds\",\"quantity\":1,\"price\":{\"USD\":{\"unit_amount\":3.0}}}," +
          "{\"code\":\"fruit\",\"name\":\"Mixed Fruit\",\"quantity\":1,\"price\":{\"USD\":{\"unit_amount\":3.0}}}," +
          "{\"code\":\"salad\",\"name\":\"Salad Spinner\",\"quantity\":1,\"price\":{\"USD\":{\"unit_amount\":9.99}}}," +
          "{\"code\":\"veggies\",\"name\":\"Chopped Veggies\",\"quantity\":1,\"price\":{\"USD\":{\"unit_amount\":5.0}}}]}";

  private static String basicPlanJson =
      "{\"code\":\"kale_krazy\",\"name\":\"Kale Krazy\",\"price\":{\"USD\":{\"setup_fee\":0.0,\"unit_amount\":39.99,\"symbol\":\"$\"}},\"period\":{\"interval\":\"months\",\"length\":1},\"tax_exempt\":false}";

  public void testParsePlanWithAddons() {

    PlanDTO plan = new Gson().fromJson(planWithAddonsJson, PlanDTO.class);

    assertEquals(plan.getCode(), "kale_fan");
    assertEquals(plan.getName(), "Kale Fan");

    PlanPriceDTO planPrice = plan.getPrice().get("USD");
    assertNotNull(planPrice);

    assertEquals(planPrice.getUnitAmount(), 19.99f);
    assertEquals(planPrice.getSymbol(), "$");
    assertEquals(planPrice.getSetupFee(), 0.0f);

    PlanPeriodDTO planPeriod = plan.getPeriod();

    assertNotNull(planPeriod);

    assertEquals(planPeriod.getInterval(), "months");
    assertEquals(planPeriod.getLength(), 1);


    AddonDTO almonds = plan.getAddon("almonds");

    assertNotNull(almonds);
    assertEquals(almonds.getCode(), "almonds");
    assertEquals(almonds.getName(), "Sliced Almonds");
    assertEquals(almonds.getQuantity(), 1);
    assertEquals(almonds.getPricing().getUnitAmount(), 3.0f);

    AddonDTO fruit = plan.getAddon("fruit");

    assertNotNull(fruit);
    assertEquals(fruit.getCode(), "fruit");
    assertEquals(fruit.getName(), "Mixed Fruit");
    assertEquals(fruit.getQuantity(), 1);
    assertEquals(fruit.getPricing().getUnitAmount(), 3.0f);


  }

  public void testParseBasicPlan() {

    PlanDTO plan = new Gson().fromJson(basicPlanJson, PlanDTO.class);

    assertEquals(plan.getCode(), "kale_krazy");
    assertEquals(plan.getName(), "Kale Krazy");

    PlanPriceDTO planPrice = plan.getPrice().get("USD");
    assertNotNull(planPrice);

    assertEquals(planPrice.getUnitAmount(), 39.99f);
    assertEquals(planPrice.getSymbol(), "$");
    assertEquals(planPrice.getSetupFee(), 0.0f);

    PlanPeriodDTO planPeriod = plan.getPeriod();

    assertNotNull(planPeriod);

    assertEquals(planPeriod.getInterval(), "months");
    assertEquals(planPeriod.getLength(), 1);

  }

}
