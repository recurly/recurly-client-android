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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.recurly.android.network.*;
import com.recurly.android.network.dto.PlanDTO;
import com.recurly.android.network.dto.TaxDTO;
import com.recurly.android.network.request.PlanRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderTest extends UnitTest {

    public void testHeaders() {

        RecurlyNetwork network = new RecurlyNetwork();


        PlanRequest planRequest = new PlanRequest.Builder().build();
        RecurlyRequest<PlanDTO> request = new RecurlyRequest<PlanDTO>(
                planRequest,
                PlanDTO.class, Request.Method.GET, "http://none",
                new ResponseHandler<PlanDTO>() {
                    @Override
                    public void onSuccess(PlanDTO plan) {

                    }

                    @Override
                    public void onFailure(RecurlyError ex) {

                    }
                });

        try {
            network.transmitRequest(request);
        } catch (Exception ex) {
            // this won't work, since the network isn't initialized
        }

        try {
            Map<String, String> headers = request.getHeaders();

            HashMap<String, String> pairs = new HashMap<>();

            boolean foundUserAgentHeader = false;

            for (Map.Entry<String, String> e : headers.entrySet()) {

                String header = e.getKey();
                String value = e.getValue();


                if (header.equalsIgnoreCase("User-Agent")) {
                    foundUserAgentHeader = true;
                    String[] tokens = value.split(";");

                    for (String token : tokens) {
                        String[] keyVals = token.split("/");
                        String key = keyVals[0].trim();
                        String val = keyVals[1].trim();
                        pairs.put(key, val);
                    }
                }
            }

            assertTrue(foundUserAgentHeader);
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_PLATFORM));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_APP_NAME));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_CARRIER_NAME));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_COUNTRY_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_DEVICE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_MOBILE_COUNTRY_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_MOBILE_NETWORK_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_NETWORK_LIB));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_OS));

            assertTrue(pairs.get(RecurlyNetwork.UA_PLATFORM).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_APP_NAME).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_CARRIER_NAME).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_COUNTRY_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_DEVICE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_MOBILE_COUNTRY_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_MOBILE_NETWORK_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_NETWORK_LIB).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_OS).length() > 0);


        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
            assertTrue(false);
        }


        PlanRequest taxRequest = new PlanRequest.Builder().build();
        RecurlyListRequest listRequest = new RecurlyListRequest<TaxDTO>(
                taxRequest,
                TaxDTO.class, Request.Method.GET, "http://none",
                new ResponseHandler<List<TaxDTO>>() {
                    @Override
                    public void onSuccess(List<TaxDTO> taxDTOs) {

                    }

                    @Override
                    public void onFailure(RecurlyError ex) {

                    }
                });

        try {
            network.transmitRequest(listRequest);
        } catch (Exception ex) {
            // this won't work, since the network isn't initialized
        }

        try {
            Map<String, String> headers = listRequest.getHeaders();

            HashMap<String, String> pairs = new HashMap<>();

            boolean foundUserAgentHeader = false;

            for (Map.Entry<String, String> e : headers.entrySet()) {

                String header = e.getKey();
                String value = e.getValue();


                if (header.equalsIgnoreCase("User-Agent")) {
                    foundUserAgentHeader = true;
                    String[] tokens = value.split(";");

                    for (String token : tokens) {
                        String[] keyVals = token.split("/");
                        String key = keyVals[0].trim();
                        String val = keyVals[1].trim();
                        pairs.put(key, val);
                    }
                }
            }

            assertTrue(foundUserAgentHeader);
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_PLATFORM));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_APP_NAME));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_CARRIER_NAME));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_COUNTRY_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_DEVICE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_MOBILE_COUNTRY_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_MOBILE_NETWORK_CODE));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_NETWORK_LIB));
            assertTrue(pairs.containsKey(RecurlyNetwork.UA_OS));

            assertTrue(pairs.get(RecurlyNetwork.UA_PLATFORM).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_APP_NAME).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_CARRIER_NAME).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_COUNTRY_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_DEVICE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_MOBILE_COUNTRY_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_MOBILE_NETWORK_CODE).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_NETWORK_LIB).length() > 0);
            assertTrue(pairs.get(RecurlyNetwork.UA_OS).length() > 0);


        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
            assertTrue(false);
        }
    }
}
