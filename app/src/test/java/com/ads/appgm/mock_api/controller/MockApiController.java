package com.ads.appgm.mock_api.controller;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public abstract class MockApiController {

    protected MockApiController() {
        response = defaultResponse();
    }

    protected MockResponse response;

    protected long delay = 0L;

    public abstract boolean check(RecordedRequest request);

    public abstract MockResponse response(RecordedRequest request);

    public abstract MockResponse defaultResponse();

    public void backToDefaul() {
        delay = 0L;
        response = defaultResponse();
    }

    public boolean shouldDelay() {
        return delay > 0L;
    }

    public long getDelay() {
        return delay;
    }

    public boolean isGet(RecordedRequest request) {
        return "GET".equals(request.getMethod().toUpperCase());
    }

    public boolean isPost(RecordedRequest request) {
        return "POST".equals(request.getMethod().toUpperCase());
    }

    protected boolean isPut(RecordedRequest request) {
        return "PUT".equals(request.getMethod().toUpperCase());
    }

    protected boolean isDelete(RecordedRequest request) {
        return "DELETE".equals(request.getMethod().toUpperCase());
    }

    public MockResponse getResponse() {
        return response;
    }
}
