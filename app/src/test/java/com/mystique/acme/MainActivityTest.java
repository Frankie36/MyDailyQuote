package com.mystique.acme;

import android.os.Build;

import com.mystique.acme.model.FortuneResponse;
import com.mystique.acme.rest.ApiInterface;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

import static androidx.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1)
public class MainActivityTest {

    @Rule
    public MockWebServer mockBackend = new MockWebServer();

    private ApiInterface mockApi() {
        return MainApplication.getInstance().provideRetrofit(mockBackend.url("/").toString()).create(ApiInterface.class);
    }

    private String fileName = "response_ok";

    @Test
    public void getsBuilderInstance() {
        final ApiInterface messagesApi = MainApplication.getInstance().provideRetrofit(Constants.BASE_URL).create(ApiInterface.class);

        assertNotNull(messagesApi);
    }

    @Test
    public void getsScriptedSuccessResponseFromMockBackend() throws IOException {
        try {
            mockBackend.enqueue(
                    new MockResponse()
                            .setBody(RestServiceTestHelper.getStringFromFile(getContext(), fileName))
                            .setResponseCode(200)
                            .addHeader("Content-Type", "application/json;charset=utf-8")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Response<FortuneResponse> response = mockApi().getData().execute();

        assertEquals(200, response.code());
        assertTrue(response.headers().get("Content-Type").contains("application/json"));

        final ResponseBody rawResponseBody = response.raw().body();
        assertEquals("application", rawResponseBody.contentType().type());
        assertEquals("json", rawResponseBody.contentType().subtype());
    }

    @Test
    public void getsScriptedErrorResponseFromMockBackend() throws IOException {
        String fileName = "quote_500";

        try {
            mockBackend.enqueue(
                    new MockResponse()
                            .setBody(RestServiceTestHelper.getStringFromFile(getContext(), fileName))
                            .setResponseCode(500)
                            .addHeader("Content-Type", "application/json;charset=utf-8")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Response<FortuneResponse> response = mockApi().getData().execute();

        assertEquals(500, response.code());

        final FortuneResponse fortuneResponse = response.body();
        assertNull(fortuneResponse);
    }

    @Test
    public void getsEntityObject() throws IOException {

        try {
            mockBackend.enqueue(new MockResponse()
                    .setBody(RestServiceTestHelper.getStringFromFile(getContext(), fileName))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Response<FortuneResponse> response = mockApi().getData().execute();
        final FortuneResponse fortuneResponse = response.body();

        assertEquals("Many pages make a thick book.", fortuneResponse.getFortune().get(0));
    }

    @Test
    public void getsExpectedRequest() throws IOException, InterruptedException {
        mockBackend.enqueue(new MockResponse().setBody("{\n" +
                "  \"fortune\": [\n" +
                "    \"Many pages make a thick book.\"\n" +
                "  ]\n" +
                "}"));

        mockApi().getData().execute();

        final RecordedRequest recordedRequest = mockBackend.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/fortune", recordedRequest.getPath());
    }

    @After
    public void tearDown() throws Exception {
        mockBackend.shutdown();
    }
}
