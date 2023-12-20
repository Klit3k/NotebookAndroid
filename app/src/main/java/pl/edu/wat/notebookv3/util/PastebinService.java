package pl.edu.wat.notebookv3.util;

import android.util.Log;
import okhttp3.*;

import java.io.IOException;

public class PastebinService {
    private final String BASE_URL = "https://pastebin.com/api/api_post.php";
    private OkHttpClient client = new OkHttpClient();
    private String url;

    public int httpCode;
    public String getUrl() {
        return url;
    };

    public int getHttpCode() {
        return httpCode;
    }

    public String share(String pasteName, String title, String body) throws IOException {

        RequestBody formBody = new FormBody.Builder()
                .add("api_dev_key", "")
                .add("api_paste_code", body)
                .add("api_paste_name", title)
                .add("api_paste_private", "1") //2 - private
                .add("api_option", "paste")
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            httpCode = response.code();
            url = response.body().string();
            if(httpCode == 422) {
                Log.d("Test::PastebinService", "Error, http response code:" + httpCode);
            }
            return url;
        }
    }

}
