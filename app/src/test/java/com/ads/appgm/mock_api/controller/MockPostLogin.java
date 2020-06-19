package com.ads.appgm.mock_api.controller;



import com.ads.appgm.mock_api.model.MockLogin;
import com.ads.appgm.mock_api.model.MockLoginRequest;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockPostLogin extends MockApiController {

    private static MockPostLogin instance;

    private MockPostLogin(){
        super();
    }

    public static MockPostLogin getInstance(){
        if (instance == null) {
            instance = new MockPostLogin();
        }
        return instance;
    }

    public static void setDelay(long delay) {
        instance.delay = delay;
    }

    public static void setResponse(MockResponse newResponse) {
        getInstance().response = newResponse;
    }

    @Override
    public boolean check(RecordedRequest request) {
        return isPost(request) && "/login".equals(request.getPath());
    }

    @Override
    public MockResponse response(RecordedRequest request) {
        final String body = request.getBody().clone().readUtf8();
        final MockLoginRequest loginRequest = MockApiHelper.fromJson(body, MockLoginRequest.class);

        if (loginRequest == null) {
            return new MockResponse().setResponseCode(400).setBody("Payload inválido: " + request.getBody().clone().readUtf8());
        }

        return response;
    }

    @Override
    public MockResponse defaultResponse() {
        final MockLogin login = new MockLogin(
                "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1OTIyNTQyNTcsImF1ZCI6ImdtLWFwcC1hc3Npc3RlZCIsInN1YiI6IjEifQ.Zo6j3Yio5_TYeh45TFjCUoMQe0g3sxVZ82rfwnK7ypw",
                1,
                "Mariana"
        );

        return new MockResponse().setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(MockApiHelper.toJson(login));
    }
}
