package com.ads.appgm.mock_api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;


public class MockApi {

    public static void main(String[] args) {
        MockApi.createServer();
    }

    private static MockWebServer server;

    private static final MockApiDispatcher dispatcher = new MockApiDispatcher();

    public static MockWebServer startServer(){
        if (server == null) {
            createServer();
        }
        return server;
    }

    private static void createServer(){
        server = new MockWebServer();

        server.setDispatcher(dispatcher);
        try {
            server.start(3000);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void killServer(){
        if (server != null) {
            try {
                server.shutdown();
                server = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void resetApi(){
        dispatcher.resetControllersToDefault();
    }

    public static RecordedRequest takeRequest(){
        if (server != null) {
            try {
                return server.takeRequest(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void cleanRequests(){
        if (server != null) {
            try {
                RecordedRequest recordedRequest;
                do {
                    recordedRequest = server.takeRequest(0L, TimeUnit.SECONDS);
                } while (recordedRequest != null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
