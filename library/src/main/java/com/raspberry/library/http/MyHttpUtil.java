package com.raspberry.library.http;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.cookie.PersistentCookieStore;
import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.common.okhttp.io.IOParam;
import net.qiujuer.common.okhttp.io.Param;
import net.qiujuer.common.okhttp.io.StrParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaohu on 2016/9/6.
 * email:luoxiaohu93@163.com
 */
public class MyHttpUtil {

    public static void init() {
    }

    public static void enableSaveCookie(Context context) {
        Http.getClient().setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy
                .ACCEPT_ALL));
    }

    public static void removeCookie() {
        Http.removeCookie();
    }

    public static String getCookie() {
        return Http.getCookie();
    }

    public static OkHttpClient getClient() {
        return Http.getClient();
    }

    public static void cancel(Object tag) {
        Http.cancel(tag);
    }


    /**
     * ============GET SYNC===============
     */
    public static String getSync(String url) {
        return Http.getSync(url);
    }

    public static String getSync(String url, StrParam... strParams) {
        return Http.getSync(url, strParams);
    }

    public static String getSync(String url, Object tag) {
        return Http.getSync(url, tag);
    }

    public static <T> T getSync(Class<T> tClass, String url) {
        return Http.getSync(tClass, url);
    }

    public static <T> T getSync(Class<T> tClass, String url, StrParam... strParams) {
        return Http.getSync(tClass, url, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, List<StrParam> strParams) {
        return Http.getSync(tClass, url, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, Map<String, String> params) {
        return Http.getSync(tClass, url, params);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag) {
        return Http.getSync(tClass, url, tag);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, StrParam... strParams) {
        return Http.getSync(tClass, url, tag, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, List<StrParam> strParams) {
        return Http.getSync(tClass, url, tag, strParams);
    }

    public static <T> T getSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return Http.getSync(tClass, url, tag, params);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback) {
        return Http.getSync(url, callback);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback) {
        return Http.getSync(url, tag, callback);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, StrParam... strParams) {
        return Http.getSync(url, callback, strParams);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, List<StrParam> strParams) {
        return Http.getSync(url, callback, strParams);
    }

    public static <T> T getSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return Http.getSync(url, callback, params);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, StrParam... strParams) {
        return Http.getSync(url, tag, callback, strParams);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, List<StrParam> strParams) {
        return Http.getSync(url, tag, callback, strParams);
    }

    public static <T> T getSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return Http.getSync(url, tag, callback, params);
    }

    /**
     * ============GET ASYNC===============
     */
    public static void getAsync(String url, HttpCallback callback) {
        Http.getAsync(url, callback);
    }

    public static void getAsync(String url, HttpCallback callback, StrParam... strParams) {
        Http.getAsync(url, callback, strParams);
    }

    public static void getAsync(String url, HttpCallback callback, List<StrParam> strParams) {
        Http.getAsync(url, callback, strParams);
    }

    public static void getAsync(String url, HttpCallback callback, Map<String, String> params) {
        Http.getAsync(url, callback, params);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback) {
        Http.getAsync(url, tag, callback);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, StrParam... strParams) {
        Http.getSync(url, tag, callback, strParams);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, List<StrParam> strParams) {
        Http.getAsync(url, tag, callback, strParams);
    }

    public static void getAsync(String url, Object tag, HttpCallback callback, Map<String, String> params) {
        Http.getAsync(url, tag, callback, params);
    }

    /**
     * ============POST SYNC===============
     */
    public static String postSync(String url, List<StrParam> strParams) {
        return Http.postSync(url, strParams);
    }

    public static String postSync(String url, Map<String, String> params) {
        return Http.postSync(url, params);
    }

    public static String postSync(String url, StrParam... strParams) {
        return Http.postSync(url, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, List<StrParam> strParams) {
        return Http.postSync(tClass, url, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, Map<String, String> params) {
        return Http.postSync(tClass, url, params);
    }

    public static <T> T postSync(Class<T> tClass, String url, StrParam... strParams) {
        return Http.postSync(tClass, url, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, List<StrParam> strParams) {
        return Http.postSync(tClass, url, tag, strParams);
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, Map<String, String> params) {
        return Http.postSync(tClass, url, tag, params);
    }

    public static <T> T postSync(Class<T> tClass, String url, Object tag, StrParam... strParams) {
        return Http.postSync(tClass, url, tag, strParams);
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, List<StrParam> strParams) {
        return Http.postSync(url, callback, strParams);
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, Map<String, String> params) {
        return Http.postSync(url, callback, params);
    }

    public static <T> T postSync(String url, HttpCallback<T> callback, StrParam... strParams) {
        return Http.postSync(url, callback, strParams);
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, List<StrParam> strParams) {
        return Http.postSync(url, tag, callback, strParams);
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, Map<String, String> params) {
        return Http.postSync(url, tag, callback, params);
    }

    public static <T> T postSync(String url, Object tag, HttpCallback<T> callback, StrParam... strParams) {
        return Http.postSync(url, tag, callback, strParams);
    }

    /**
     * ============POST ASYNC===============
     */
    public static void postAsync(String url, final HttpCallback callback, List<StrParam> strParams) {
        Http.postAsync(url, callback, strParams);
    }

    public static void postAsync(String url, final HttpCallback callback, Map<String, String> params) {
        Http.postAsync(url, callback, params);
    }

    public static void postAsync(String url, final HttpCallback callback, StrParam... strParams) {
        Http.postAsync(url, callback, strParams);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, List<StrParam> strParams) {
        Http.postAsync(url, tag, callback, strParams);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, Map<String, String> params) {
        Http.postAsync(url, tag, callback, params);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, StrParam... strParams) {
        Http.postAsync(url, tag, callback, strParams);
    }

    public static void postAsync(String url, final HttpCallback callback, RequestBody body) {
        Http.postAsync(url, callback, body);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, RequestBody body) {
        Http.postAsync(url, tag, callback, body);
    }

    public static void postAsync(String url, final HttpCallback callback, String bodyStr) {
        Http.postAsync(url, callback, bodyStr);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, String bodyStr) {
        Http.postAsync(url, tag, callback, bodyStr);
    }

    public static void postAsync(String url, final HttpCallback callback, byte[] bytes) {
        Http.postAsync(url, callback, bytes);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, byte[] bytes) {
        Http.postAsync(url, tag, callback, bytes);
    }

    public static void postAsync(String url, final HttpCallback callback, File file) {
        Http.postAsync(url, callback, file);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, File file) {
        Http.postAsync(url, tag, callback, file);
    }

    public static void postAsync(String url, final HttpCallback callback, JSONObject jsonObject) {
        Http.postAsync(url, callback, jsonObject);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, JSONObject jsonObject) {
        Http.postAsync(url, tag, callback, jsonObject);
    }

    public static void postAsync(String url, final HttpCallback callback, JSONArray jsonArray) {
        Http.postAsync(url, callback, jsonArray);
    }

    public static void postAsync(String url, Object tag, final HttpCallback callback, JSONArray jsonArray) {
        Http.postAsync(url, tag, callback, jsonArray);
    }

    /**
     * ============UPLOAD ASYNC===============
     */

    public static void uploadAsync(String url, String key, File file, HttpCallback callback) {
        Http.uploadAsync(url, key, file, callback);
    }

    public static void uploadAsync(String url, Object tag, String key, File file, HttpCallback callback) {
        Http.uploadAsync(url, tag, key, file, callback);
    }

    public static void uploadAsync(String url, HttpCallback callback, IOParam... params) {
        Http.uploadAsync(url, callback, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, IOParam... params) {
        Http.uploadAsync(url, tag, callback, params);
    }

    public static void uploadAsync(String url, HttpCallback callback, Param... params) {
        Http.uploadAsync(url, callback, params);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, Param... params) {
        Http.uploadAsync(url, tag, callback, params);
    }

    public static void uploadAsync(String url, HttpCallback callback, StrParam[] strParams, IOParam... IOParams) {
        Http.uploadAsync(url, callback, strParams, IOParams);
    }

    public static void uploadAsync(String url, Object tag, HttpCallback callback, StrParam[] strParams, IOParam...
            IOParams) {
        Http.uploadAsync(url, tag, callback, strParams, IOParams);
    }

    /**
     * ============DOWNLOAD ASYNC===============
     */
    public static void downloadAsync(String url, String file, HttpCallback<File> callback) {
        Http.downloadAsync(url, file, callback);
    }

    public static void downloadAsync(String url, String file, HttpCallback<File> callback, StrParam... params) {
        Http.downloadAsync(url, file, callback, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback) {
        Http.downloadAsync(url, file, tag, callback);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, StrParam...
            params) {
        Http.downloadAsync(url, file, tag, callback, params);
    }

    public static void downloadAsync(String url, String file, Object tag, HttpCallback<File> callback, int method,
                                     StrParam... params) {
        Http.downloadAsync(url, file, tag, callback, method, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File>
            callback, StrParam... params) {
        Http.downloadAsync(url, fileDir, fileName, tag, callback, params);
    }

    public static void downloadAsync(String url, String fileDir, String fileName, Object tag, HttpCallback<File>
            callback, int method, StrParam... params) {
        Http.downloadAsync(url, fileDir, fileName, tag, callback, method, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, StrParam...
            params) {
        Http.downloadAsync(url, outFile, tag, callback, params);
    }

    public static void downloadAsync(String url, File outFile, Object tag, HttpCallback<File> callback, int method,
                                     StrParam... params) {
        Http.downloadAsync(url, outFile, tag, callback, method, params);
    }

}
