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

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.microsoft.live.OAuth.ErrorType;

/**
 * OAuthErrorResponse represents the an Error Response from the OAuth server.
 */
class OAuthErrorResponse implements OAuthResponse {

    /**
     * Builder is a helper class to create a OAuthErrorResponse.
     * An OAuthResponse must contain an error, but an error_description and
     * error_uri are optional
     */
    public static class Builder {
        private final ErrorType error;
        private String errorDescription;
        private String errorUri;

        public Builder(ErrorType error) {
            assert error != null;

            this.error = error;
        }

        /**
         * @return a new instance of an OAuthErrorResponse containing
         *         the values called on the builder.
         */
        public OAuthErrorResponse build() {
            return new OAuthErrorResponse(this);
        }

        public Builder errorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
            return this;
        }

        public Builder errorUri(String errorUri) {
            this.errorUri = errorUri;
            return this;
        }
    }

    /**
     * Static constructor that creates an OAuthErrorResponse from the given OAuth server's
     * JSONObject response
     * @param response from the OAuth server
     * @return A new instance of an OAuthErrorResponse from the given response
     * @throws LiveAuthException if there is an JSONException, or the error type cannot be found.
     */
    public static OAuthErrorResponse createFromJson(JSONObject response) throws LiveAuthException {
        final String errorString;
        try {
            errorString = response.getString(OAuth.ERROR);
        } catch (JSONException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        }

        final ErrorType error;
        try {
            error = ErrorType.valueOf(errorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        } catch (NullPointerException e) {
            throw new LiveAuthException(ErrorMessages.SERVER_ERROR, e);
        }

        final Builder builder = new Builder(error);
        if (response.has(OAuth.ERROR_DESCRIPTION)) {
            final String errorDescription;
            try {
                errorDescription = response.getString(OAuth.ERROR_DESCRIPTION);
            } catch (JSONException e) {
                throw new LiveAuthException(ErrorMessages.CLIENT_ERROR, e);
            }
            builder.errorDescription(errorDescription);
        }

        if (response.has(OAuth.ERROR_URI)) {
            final String errorUri;
            try {
                errorUri = response.getString(OAuth.ERROR_URI);
            } catch (JSONException e) {
                throw new LiveAuthException(ErrorMessages.CLIENT_ERROR, e);
            }
            builder.errorUri(errorUri);
        }

        return builder.build();
    }

    /**
     * @param response to check
     * @return true if the given JSONObject is a valid OAuth response
     */
    public static boolean validOAuthErrorResponse(JSONObject response) {
        return response.has(OAuth.ERROR);
    }

    /** REQUIRED. */
    private final ErrorType error;

    /**
     * OPTIONAL.  A human-readable UTF-8 encoded text providing
     * additional information, used to assist the client developer in
     * understanding the error that occurred.
     */
    private final String errorDescription;

    /**
     * OPTIONAL.  A URI identifying a human-readable web page with
     * information about the error, used to provide the client
     * developer with additional information about the error.
     */
    private final String errorUri;

    /**
     * OAuthErrorResponse constructor. It is private to enforce
     * the use of the Builder.
     *
     * @param builder to use to construct the object.
     */
    private OAuthErrorResponse(Builder builder) {
        this.error = builder.error;
        this.errorDescription = builder.errorDescription;
        this.errorUri = builder.errorUri;
    }

    @Override
    public void accept(OAuthResponseVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * error is a required field.
     * @return the error
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * error_description is an optional field
     * @return error_description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * error_uri is an optional field
     * @return error_uri
     */
    public String getErrorUri() {
        return errorUri;
    }

    @Override
    public String toString() {
        return String.format("OAuthErrorResponse [error=%s, errorDescription=%s, errorUri=%s]",
                             error.toString().toLowerCase(Locale.US), errorDescription, errorUri);
    }
}
