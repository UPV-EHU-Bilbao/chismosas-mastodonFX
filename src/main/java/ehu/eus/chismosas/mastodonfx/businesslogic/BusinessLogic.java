package ehu.eus.chismosas.mastodonfx.businesslogic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ehu.eus.chismosas.mastodonfx.domain.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class BusinessLogic {
    static OkHttpClient client = new OkHttpClient();
    static Gson gson = new Gson();
    static TypeToken<List<Status>> statusListType = new TypeToken<>() {};

    public static List<Status> getStatuses(String id) {
        String json = request("accounts/" + id + "/statuses");
        TypeToken<List<Status>> statusListType = new TypeToken<List<Status>>() {};
        return gson.fromJson(json, statusListType);
    }

    public static String request(String endpoint) {
        String result = "";
        Request request = new Request.Builder()
                .url("https://mastodon.social/api/v1/"+ endpoint)
                .get()
                .addHeader("Authorization", "Bearer " + System.getenv("TOKEN"))
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
