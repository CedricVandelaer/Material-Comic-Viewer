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

package com.microsoft.live.test.util;

import java.util.concurrent.BlockingQueue;

import org.json.JSONObject;

import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationListener;

public class PostAsyncRunnable extends AsyncRunnableWithBody<LiveOperation, LiveOperationListener> {

    public PostAsyncRunnable(BlockingQueue<LiveOperation> queue,
                             LiveConnectClient connectClient,
                             String path,
                             JSONObject body,
                             LiveOperationListener listener) {
        super(queue, connectClient, path, body, listener);
    }

    public PostAsyncRunnable(BlockingQueue<LiveOperation> queue,
                             LiveConnectClient connectClient,
                             String path,
                             JSONObject body,
                             LiveOperationListener listener,
                             Object userState) {
        super(queue, connectClient, path, body, listener, userState);
    }

    public PostAsyncRunnable(BlockingQueue<LiveOperation> queue,
                             LiveConnectClient connectClient,
                             String path,
                             String body,
                             LiveOperationListener listener) {
        super(queue, connectClient, path, body, listener);
    }

    public PostAsyncRunnable(BlockingQueue<LiveOperation> queue,
                             LiveConnectClient connectClient,
                             String path,
                             String body,
                             LiveOperationListener listener,
                             Object userState) {
        super(queue, connectClient, path, body, listener, userState);
    }

    @Override
    protected LiveOperation calledWithoutUserState(JSONObject body) {
        return connectClient.postAsync(path, body, listener);
    }

    @Override
    protected LiveOperation calledWithoutUserState(String body) {
        return connectClient.postAsync(path, body, listener);
    }

    @Override
    protected LiveOperation calledWithUserState(JSONObject body, Object userState) {
        return connectClient.postAsync(path, body, listener, userState);
    }

    @Override
    protected LiveOperation calledWithUserState(String body, Object userState) {
        return connectClient.postAsync(path, body, listener, userState);
    }

}
