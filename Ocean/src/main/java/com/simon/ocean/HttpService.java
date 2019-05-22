package com.simon.ocean;

import com.alibaba.fastjson.JSON;
import com.simon.neo.NeoMap;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhouzhenyong
 * @since 2019/5/20 下午4:44
 */
@Slf4j
@Service
public class HttpService {

    static OkHttpClient httpClient;

    static {
        httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(60, TimeUnit.SECONDS).build();
    }

    private ThreadLocal<String> bodyJson = new ThreadLocal<>();
    private ThreadLocal<Builder> builderLocal = new ThreadLocal<>();
    private ThreadLocal<HttpMethod> httpMethodLocal = new ThreadLocal<>();

    /**
     * 将含有命名空间的前缀解析为指定的ip域名，比如：name/shop/getCount 到 http://xxx.xxx.xxx.xxx:port/shop/getCount
     *
     * @param url restful路径，比如name/shop/getCount
     * @return 转换后的全url路径
     */

    public HttpService get(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.GET);
        return this;
    }

    public HttpService post(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.POST);
        return this;
    }

    public HttpService head(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.HEAD);
        return this;
    }

    public HttpService put(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.PUT);
        return this;
    }

    public HttpService patch(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.PATCH);
        return this;
    }

    public HttpService delete(String url){
        builderLocal.set(new Builder());
        builderLocal.get().url(urlNameChg(url));
        httpMethodLocal.set(HttpMethod.DELETE);
        return this;
    }


    public HttpService headers(NeoMap headMap) {
        builderLocal.get().headers(Headers.of(headMap.getDataMapAssignValueType(String.class)));
        return this;
    }

    public HttpService body(NeoMap bodyMap) {
        bodyJson.set(JSON.toJSONString(bodyMap));
        return this;
    }

    public String send() {
        HttpMethod method = httpMethodLocal.get();
        Builder builder = builderLocal.get();
        String result = null;
        try {
            switch (method) {
                case GET: {
                    result = getResponseBody(httpClient.newCall(builder.get().build()).execute());
                    break;
                }
                case POST:{
                    result = getResponseBody(httpClient.newCall(builder
                        .post(RequestBody.create(MediaType.parse("application/json"), bodyJson.get()))
                        .build()).execute());
                    break;
                }
                case HEAD:{
                    result = JSON.toJSONString(getResponseHead(httpClient.newCall(builder.head().build()).execute()));
                    break;
                }
                case PUT:{
                    result = getResponseBody(httpClient.newCall(builder
                        .put(RequestBody.create(MediaType.parse("application/json"), bodyJson.get()))
                        .build()).execute());
                    break;
                }
                case PATCH:{
                    result = getResponseBody(httpClient.newCall(builder
                        .patch(RequestBody.create(MediaType.parse("application/json"), bodyJson.get()))
                        .build()).execute());
                    break;
                }
                case DELETE:{
                    result = getResponseBody(httpClient.newCall(builder
                        .delete(RequestBody.create(MediaType.parse("application/json"), bodyJson.get()))
                        .build()).execute());
                    break;
                }
                default:
                    break;
            }
        } catch (IOException e) {
            log.error("获取异常， {}", e);
            e.printStackTrace();
        } finally {
            httpMethodLocal.remove();
            bodyJson.remove();
            builderLocal.remove();
        }
        return result;
    }

    private String getResponseBody(Response response) throws IOException {
        if (!response.isSuccessful()) {
            try (ResponseBody body = response.body()) {
                assert body != null;
                throw new HttpException("code = " + response.code()
                    + ", url = " + response.request().url().toString()
                    + ", body = " + body.string());
            }
        }else{
            return response.body().string();
        }
    }

    private NeoMap getResponseHead(Response response) throws IOException {
        if (!response.isSuccessful()) {
            try (ResponseBody body = response.body()) {
                assert body != null;
                throw new HttpException("code = " + response.code()
                    + ", url = " + response.request().url().toString()
                    + ", body = " + body.string());
            }
        }else{
            return NeoMap.fromMap(response.headers().toMultimap());
        }
    }

    public class HttpException extends RuntimeException{
        HttpException(){
            super();
        }

        HttpException(String msg){
            super(msg);
        }
    }

    private String urlNameChg(String url) {
        if (url.contains("/") && !url.contains(":")) {
            Integer index = url.indexOf("/");
            String namespace = url.substring(0, index);
            //String ipData = nameSpaceInterface.getIpAndPort(namespace);
            String ipData = "";
            if(!StringUtils.isEmpty(ipData)){
                return ipData + url.substring(index);
            }
        }
        return url;
    }

    @SuppressWarnings("all")
    public enum HttpMethod{
        /**
         * 暂时不支持
         */
        HEAD,
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }
}
