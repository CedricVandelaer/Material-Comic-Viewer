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

import java.io.InputStream;

import android.text.TextUtils;

/**
 * Represents data returned from a download call to the Live Connect Representational State
 * Transfer (REST) API.
 */
public class LiveDownloadOperation {
    static class Builder {
        private ApiRequestAsync<InputStream> apiRequestAsync;
        private final String method;
        private final String path;
        private InputStream stream;
        private Object userState;

        public Builder(String method, String path) {
            assert !TextUtils.isEmpty(method);
            assert !TextUtils.isEmpty(path);

            this.method = method;
            this.path = path;
        }

        /**
         * Set if the operation to build is an async operation.
         *
         * @param apiRequestAsync
         * @return this Builder
         */
        public Builder apiRequestAsync(ApiRequestAsync<InputStream> apiRequestAsync) {
            assert apiRequestAsync != null;

            this.apiRequestAsync = apiRequestAsync;
            return this;
        }

        public LiveDownloadOperation build() {
            return new LiveDownloadOperation(this);
        }

        public Builder stream(InputStream stream) {
            assert stream != null;

            this.stream = stream;
            return this;
        }

        public Builder userState(Object userState) {
            this.userState = userState;
            return this;
        }
    }

    private final ApiRequestAsync<InputStream> apiRequestAsync;
    private int contentLength;
    private final String method;
    private final String path;
    private InputStream stream;
    private final Object userState;

    LiveDownloadOperation(Builder builder) {
        this.apiRequestAsync = builder.apiRequestAsync;
        this.method = builder.method;
        this.path = builder.path;
        this.stream = builder.stream;
        this.userState = builder.userState;
    }

    public void cancel() {
        final boolean isCancelable = this.apiRequestAsync != null;
        if (isCancelable) {
            this.apiRequestAsync.cancel(true);
        }
    }

    /**
     * @return The type of HTTP method used to make the call.
     */
    public String getMethod() {
        return this.method;
    }

    /**
     * @return The length of the stream.
     */
    public int getContentLength() {
        return this.contentLength;
    }

    /**
     * @return The path for the stream object.
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @return The stream object that contains the downloaded file.
     */
    public InputStream getStream() {
        return this.stream;
    }

    /**
     * @return The user state.
     */
    public Object getUserState() {
        return this.userState;
    }

    void setContentLength(int contentLength) {
        assert contentLength >= 0;

        this.contentLength = contentLength;
    }

    void setStream(InputStream stream) {
        assert stream != null;

        this.stream = stream;
    }
}
