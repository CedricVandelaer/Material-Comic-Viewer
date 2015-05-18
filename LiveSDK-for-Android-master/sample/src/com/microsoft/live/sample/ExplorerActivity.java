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

package com.microsoft.live.sample;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;

public class ExplorerActivity extends Activity {
    private class OperationListener implements LiveOperationListener {
        @Override
        public void onComplete(LiveOperation operation) {
            dismissProgressDialog();
            try {
                mResponseBodyText.setText(operation.getResult().toString(2));
                mResponseBodyText.requestFocus();
            } catch (JSONException e) {
                makeToast(e.getMessage());
            }
        }

        @Override
        public void onError(LiveOperationException exception, LiveOperation operation) {
            dismissProgressDialog();
            makeToast(exception.getMessage());
        }
    }

    private static final String[] HTTP_METHODS = {
        "GET",
        "DELETE",
        "PUT",
        "POST"
    };

    private static final int GET = 0;
    private static final int DELETE = 1;
    private static final int PUT = 2;
    private static final int POST = 3;

    private LiveConnectClient mConnectClient;
    private EditText mResponseBodyText;
    private EditText mPathText;
    private EditText mRequestBodyText;
    private TextView mRequestBodyTextView;
    private ProgressDialog mProgressDialog;
    private OperationListener mOperationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer);

        LiveSdkSampleApplication app = (LiveSdkSampleApplication) getApplication();
        mConnectClient = app.getConnectClient();
        mOperationListener = new OperationListener();

        mResponseBodyText = (EditText) findViewById(R.id.responseBodyText);
        mPathText = (EditText) findViewById(R.id.pathText);
        mRequestBodyText = (EditText) findViewById(R.id.requestBodyText);
        mRequestBodyTextView = (TextView) findViewById(R.id.requestBodyTextView);

        final Spinner httpMethodSpinner = (Spinner) findViewById(R.id.httpMethodSpinner);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, HTTP_METHODS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        httpMethodSpinner.setAdapter(adapter);
        httpMethodSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case GET:
                    case DELETE:
                        hideRequestBody();
                        break;
                    case POST:
                    case PUT:
                        showRequestBody();
                        break;
                    default: {
                        makeToast("Unknown HTTP method selected: " +
                                  httpMethodSpinner.getSelectedItem().toString());
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        });

        findViewById(R.id.submitButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = mPathText.getText().toString();
                String bodyString = mRequestBodyText.getText().toString();

                if (TextUtils.isEmpty(path)) {
                    makeToast("Path must not be empty.");
                    return;
                }

                int selectedPosition = httpMethodSpinner.getSelectedItemPosition();
                boolean httpMethodRequiresBody =
                        selectedPosition == POST || selectedPosition == PUT;
                if (httpMethodRequiresBody && TextUtils.isEmpty(bodyString)) {
                    makeToast("Request body must not be empty.");
                    return;
                }

                mProgressDialog = showProgressDialog("Loading. Please wait...");
                switch (selectedPosition) {
                    case GET: {
                        mConnectClient.getAsync(path, mOperationListener);
                        break;
                    }
                    case DELETE: {
                        mConnectClient.deleteAsync(path, mOperationListener);
                        break;
                    }
                    case POST: {
                        mConnectClient.postAsync(path, bodyString, mOperationListener);
                        break;
                    }
                    case PUT: {
                        mConnectClient.putAsync(path, bodyString, mOperationListener);
                        break;
                    }
                    default: {
                        makeToast("Unknown HTTP method selected: " +
                                  httpMethodSpinner.getSelectedItem().toString());
                        break;
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Since this activity is part of a TabView we want to send
        // the back button to the TabView activity.
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void showRequestBody() {
        mRequestBodyText.setVisibility(View.VISIBLE);
        mRequestBodyTextView.setVisibility(View.VISIBLE);
    }

    private void hideRequestBody() {
        mRequestBodyText.setVisibility(View.GONE);
        mRequestBodyTextView.setVisibility(View.GONE);
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private ProgressDialog showProgressDialog(String message) {
        return ProgressDialog.show(this, "", message);
    }
}
