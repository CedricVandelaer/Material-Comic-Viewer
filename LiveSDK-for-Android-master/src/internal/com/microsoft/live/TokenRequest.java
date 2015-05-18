// ------------------------------------------------------------------------------
// Copyright (c) 2014 Microsoft Corporation
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
// ------------------------------------------------------------------------------

package com.microsoft.live;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.text.TextUtils;

/**
 * Abstract class that represents an OAuth token request.
 * Known subclasses include AccessTokenRequest and RefreshAccessTokenRequest
 */
abstract class TokenRequest {

    private static final String CONTENT_TYPE =
            URLEncodedUtils.CONTENT_TYPE + ";charset=" + HTTP.UTF_8;

    protected final HttpClient client;
    protected final String clientId;

    /**
     * Constructs a new TokenRequest instance and initializes its parameters.
     *
     * @param client the HttpClient to make HTTP requests on
     * @param clientId the client_id of the calling application
     */
    public TokenRequest(HttpClient client, String clientId) {
        assert client != null;
        assert clientId != null;
        assert !TextUtils.isEmpty(clientId);

        this.client = client;
        this.clientId = clientId;
    }

    /**
     * Performs the Token Request and returns the OAuth server's response.
     *
     * @return The OAuthResponse from the server
     * @throws LiveAuthException if there is any exception while executing the request
     *                           (e.g., IOException, JSONException)
     */
    public OAuthResponse execute() throws LiveAuthException {
        final Uri requestUri = Config.INSTANCE.getOAuthTokenUri();

        final HttpPost request = new HttpPost(requestUri.toString());

        final List<NameValuePair> body = new ArrayList<NameValuePair>();
        body.add(new BasicNameValuePair(OAuth.CLIENT_ID, this.clientId));

        // constructBody allows subclasses to add to body
        this.constructBody(body);

        try {
            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(body, HTTP.UTF_8);
            entity.setContentType(CONTENT_TYPE);
            request.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            throw new LiveAuthException(ErrorMessages.CLIENT_ERROR, e);
        }

        final HttpResponse response;
        try {
            response = this.client.execute(request);
        } catch (ClientProtocolException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        } catch (IOException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        }

        final HttpEntity entity = response.getEntity();
        final String stringResponse;
        try {
            stringResponse = EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        }

        final JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject(stringResponse);
        } catch (JSONException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        }

        if (OAuthErrorResponse.validOAuthErrorResponse(jsonResponse)) {
            return OAuthErrorResponse.createFromJson(jsonResponse);
        } else if (OAuthSuccessfulResponse.validOAuthSuccessfulResponse(jsonResponse)) {
            return OAuthSuccessfulResponse.createFromJson(jsonResponse);
        } else {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR);
        }
    }

    /**
     * This method gives a hook in the execute process, and allows subclasses
     * to add to the HttpRequest's body.
     * NOTE: The content type has already been added
     *
     * @param body of NameValuePairs to add to
     */
    protected abstract void constructBody(List<NameValuePair> body);
}
