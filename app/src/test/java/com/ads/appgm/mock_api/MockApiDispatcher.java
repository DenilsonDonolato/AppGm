package com.ads.appgm.mock_api;

import com.ads.appgm.mock_api.controller.MockApiController;
import com.ads.appgm.mock_api.controller.MockPostLogin;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;


public class MockApiDispatcher extends Dispatcher {

    private List<MockApiController> controllers = Arrays.asList(
            MockPostLogin.getInstance()
    );

    @NotNull
    @Override
    public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
        Optional<MockApiController> api = controllers.stream().filter(controller -> controller.check(request)).findFirst();

        if (api.isPresent()) {
            MockApiController controller = api.get();
            if (controller.shouldDelay()) {
                Thread.sleep(controller.getDelay());
            }
            return controller.response(request);
        }

        return new MockResponse().setResponseCode(404).setBody("API inválida");
    }

    public void resetControllersToDefault(){
        controllers.forEach(MockApiController::backToDefaul);
    }
}
