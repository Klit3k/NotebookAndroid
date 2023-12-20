package pl.edu.wat.notebookv3.util;

import android.util.Log;
import com.google.gson.Gson;
import okhttp3.*;
import pl.edu.wat.notebookv3.model.safenote.*;

import java.io.IOException;

public class SafenoteService {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final String BASE_URL = "https://safenote.co/api/note";
    private OkHttpClient client = new OkHttpClient();
    private String url;

    public int httpCode;

    public String getUrl() {
        return url;
    }

    ;

    public int getHttpCode() {
        return httpCode;
    }

    public SafenoteResponse share(String title, String body, String password, int lifetime, int readCount) throws IOException {
        Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: ")
                .append(title)
                .append("\n---------------\n")
                .append(body);
        SafenoteRequest safenoteRequest = SafenoteRequestBuilder.aSafenoteRequest()
                .setNote(stringBuilder.toString())
//                    .setReadCount(readCount)
                .build();
        if (!password.isEmpty()) safenoteRequest.setPassword(password);
        if (lifetime != 0) safenoteRequest.setLiftime(lifetime);


        RequestBody requestBody = RequestBody.create(gson.toJson(safenoteRequest), JSON);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            httpCode = response.code();
            SafenoteResponse safenoteResponse = gson.fromJson(response.body().string(), SafenoteResponse.class);

            if (!safenoteResponse.isSuccess()) {
                Log.d("Test::SafenoteService", "Error, http response code:" + httpCode
                        + "\nerror:" + safenoteResponse.getError() + "\nmsg:" + safenoteResponse.getMessage());
            } else
                Log.d("Test::SafenoteService", "Note created: " + safenoteResponse.getLink());
            return safenoteResponse;
        } catch (IOException ioException) {
            Log.d("Test:SafenoteService", ioException.toString());
        }
//        SafenoteResponse response = new SafenoteResponse();
//        response.setLink("https://google.com");
//        response.setSuccess(true);
//        return response;
        return null;
    }

}
