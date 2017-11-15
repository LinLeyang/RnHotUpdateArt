package com.penta.rnhotupdatelibrary.http;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linyueyang on 11/14/17.
 */

public class HttpUtil {

    public static <T> void get(String url, final HttpCallBack<T> httpCallBack) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (null != httpCallBack) {

                    T t = JSON.parseObject(response.body().string(), httpCallBack.getClazz());
                    httpCallBack.onSuccess(t);
                }
            }
        });
    }


}
