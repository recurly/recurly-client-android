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

package com.recurly.android.network;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.recurly.android.network.dto.BaseDTO;
import com.recurly.android.network.request.BaseRequest;
import com.recurly.android.util.RecurlyLog;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper request for Recurly API calls that return a list of BaseModel types as a response.
 * @param <Res>
 */
public class RecurlyListRequest<Res extends BaseDTO> extends Request<List<Res>> {

  private static Gson sErrorParser = new Gson();

  private BaseRequest mOriginalRequest;

  private Class<Res> mResponseClass;
  private ResponseHandler<List<Res>> mHandler;
  private Map<String, String> mHeaders = new HashMap<String, String>();

  private Map<String, Object> mPostParams;

  public RecurlyListRequest(BaseRequest originalRequest, Class<Res> responseClass, int method, String url, ResponseHandler<List<Res>> handler) {
    super(method, url, handler);

    mOriginalRequest = originalRequest;
    mResponseClass = responseClass;
    mHandler = handler;
  }

  public static String buildRequestUrl(String baseUrl, HashMap<String, String> params) {

    Uri.Builder builder = Uri.parse(baseUrl).buildUpon();

    for (Map.Entry<String, String> entry : params.entrySet()) {
      builder.appendQueryParameter(entry.getKey(), entry.getValue());
    }

    return builder.build().toString();
  }


  @Override
  public String getBodyContentType() {
    if (mPostParams != null) {
      return "application/json";
    }

    return super.getBodyContentType();
  }

  @Override
  public byte[] getBody() throws AuthFailureError {
    if (mPostParams != null) {
      Gson gson = new Gson();
      String body = gson.toJson(mPostParams);

      return body.getBytes();
    }

    return super.getBody();
  }

  @Override
  protected Response<List<Res>> parseNetworkResponse(NetworkResponse networkResponse) {
    String responseString = null;
    try {
      responseString = new String(networkResponse.data, "UTF-8");
    } catch (UnsupportedEncodingException e) {
    }

    List<Res> parsedResponse;

    // check if this is an error, since errors come back as 200s
    try {
      NetworkResponseError model = sErrorParser.fromJson(responseString, NetworkResponseError.class);

      if (model.isError()) {
        mOriginalRequest.setFinished(true);
        return Response.error(model);
      }
    } catch (Exception ex) {
      // not an error
    }

    RecurlyLog.d("Response code " + networkResponse.statusCode);
    RecurlyLog.d("Response string " + responseString);

    try {
      Type listType = new TypeToken<List<Res>>() {}.getType();
      parsedResponse = new ArrayList<Res>();


      JsonArray array = BaseDTO.getParser().fromJson(responseString, JsonArray.class);

      for (JsonElement jsonElement : array) {
        Res entry = BaseDTO.getParser().fromJson(jsonElement, mResponseClass);
        parsedResponse.add(entry);
      }

    } catch (Exception ex) {
      RecurlyLog.d("Error parsing response " + responseString);
      ex.printStackTrace();

      mOriginalRequest.setFinished(true);
      return Response.error(new VolleyError(ex));
    }

    mOriginalRequest.setFinished(true);
    return Response.success(parsedResponse, HttpHeaderParser.parseCacheHeaders(networkResponse));
  }

  @Override
  protected void deliverResponse(List<Res> res) {
    if (mHandler != null) {
      mHandler.onResponse(res);
    }
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    Map<String, String> headers = super.getHeaders();

    if (headers == null) {
      return mHeaders;
    }

    mHeaders.putAll(headers);
    return mHeaders;
  }

  public void setHeaders(Map<String, String> headers) {
    mHeaders = headers;
  }

  /**
   * Set up the parameters for post.  Only JSON requests are currently supported, and request
   * will automatically be sent up as JSON.
   * @param params
   */
  public void setPostParams(Map<String, Object> params) {
    mPostParams = params;
  }

}
