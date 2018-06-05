package com.yanzhenjie.kalle.sample.util;

import android.text.TextUtils;
import android.util.Log;

import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.Request;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.connect.Interceptor;
import com.yanzhenjie.kalle.connect.http.Chain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LogInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String url = request.url().toString();

        StringBuilder requestLog = new StringBuilder(String.format(" \nPrint Request: %1$s.", url));
        requestLog.append(String.format("\nMethod: %1$s.", request.method().name()));

        Headers toHeaders = request.headers();
        for (Map.Entry<String, List<String>> entry : toHeaders.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            requestLog.append(String.format("\n%1$s: %2$s.", key, TextUtils.join(";", values)));
        }
//        Log.w("tag", requestLog.toString());
        String params = getParams(request);
        requestLog.append("\nparams: "+params);

        Response response = chain.proceed(request);
        StringBuilder responseLog = new StringBuilder("\n");
//        StringBuilder responseLog = new StringBuilder(String.format(" \nPrint Response: %1$s.", url));
        responseLog.append(String.format(Locale.getDefault(), "\nCode: %1$d", response.code()));

        Headers fromHeaders = response.headers();
        for (Map.Entry<String, List<String>> entry : fromHeaders.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            responseLog.append(String.format("\n%1$s: %2$s.", key, TextUtils.join(";", values)));
        }
        responseLog.append("\nserver data: "+response.body().string());

//        Log.w("tag", responseLog.toString());
        Log.w("tag",requestLog.toString() +responseLog.toString());

        return response;
    }

    private String getParams(Request request) {
        // 第一步，获取所有请求参数。
        Params params = request.copyParams();
        // 第二步，定义List用于存储所有请求参数的key。
        List<String> keyList = new ArrayList<>();
        // 第三步，定义Map用于存储所有请求参数的value。
        Map<String, String> paramMap = new HashMap<>();
        // 第四步，拿到所有具体请求参数。
        for (Map.Entry<String, List<Object>> paramsEntry : params.entrySet()) {
            String key = paramsEntry.getKey();
            List<Object> values = paramsEntry.getValue();
            for (Object value : values) {
                if (value instanceof String) {

                    //第五步，将请求参数的key添加到list中用于排序。
                    keyList.add(key);

                    //第六步，将请求参数的value添加到Map中。
                    paramMap.put(key, (String) value);
                }
            }
        }
        // 第七步，对请求参数key进行排序。
        Collections.sort(keyList);

        StringBuilder builder = new StringBuilder();

        // 第八步，依次取出排序之后的key-value，并拼接。
        Iterator<String> keyIterator = keyList.iterator();
        if (keyIterator.hasNext()) {
            String key = keyIterator.next();
            builder.append(key).append("=").append(paramMap.get(key));
            while (keyIterator.hasNext()) {
                key = keyIterator.next();
                builder.append("&").append(key).append("=").append(paramMap.get(key));
            }
        }

        String query = builder.toString();
        return query;
//        Log.w("tag","params: "+query);
    }
}
