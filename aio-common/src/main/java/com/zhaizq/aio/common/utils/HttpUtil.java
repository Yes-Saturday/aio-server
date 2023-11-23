package com.zhaizq.aio.common.utils;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    public final static HttpUtil DEFAULT = new HttpUtil();

    private final HttpClient httpClient;

    public HttpUtil() {
        this(HttpUtil.defaultHttpClient());
    }

    public HttpUtil(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, null, params, StandardCharsets.UTF_8);
    }

    public String doPost(String url, Header[] headers, Map<String, String> params, Charset charset) throws IOException {
        List<NameValuePair> pairList = new LinkedList<>();
        if (params != null)
            params.forEach((k, v) -> pairList.add(new BasicNameValuePair(k, v)));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(new UrlEncodedFormEntity(pairList, charset));
        return doRequest(httpPost);
    }

    public String doPostJson(String url, String json) throws IOException {
        return doPostJson(url, null, json);
    }

    public String doPostJson(String url, Header[] headers, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return doRequest(httpPost);
    }

    public String doPostFile(String url, Header[] headers, Map<String, ContentBody> params) throws IOException {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.RFC6532);

        if (params != null)
            params.forEach(builder::addPart);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeaders(headers);
        httpPost.setEntity(builder.build());
        return doRequest(httpPost);
    }

    public String doGet(String url) throws IOException {
        return doGet(url, null, null);
    }

    public String doGet(String url, Map<String, String> map) throws IOException {
        return doGet(url, null, map);
    }

    public String doGet(String url, Header[] headers) throws IOException {
        return doGet(url, headers, null);
    }

    public String doGet(String url, Header[] headers, Map<String, String> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            URIBuilder uriBuilder = new URIBuilder(URI.create(url));
            params.forEach(uriBuilder::setParameter);
            url = uriBuilder.toString();
        }

        HttpGet request = new HttpGet(url);
        request.setHeaders(headers);
        return doRequest(request);
    }

    public byte[] doGetBytes(String url) throws IOException {
        HttpResponse response = httpClient.execute(new HttpGet(url));
        return EntityUtils.toByteArray(response.getEntity());
    }

    public String doRequest(HttpUriRequest request) throws IOException {
        HttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity());
    }

    public HttpResponse doExecute(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    /**
     * HttpClient
     */
    public static HttpClient createSkipSSLClient() {
        try {
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build(), NoopHostnameVerifier.INSTANCE);
            HttpClientBuilder builder = createHttpClient0(200, 200, 12000, 12000, 12000);
            return builder.setSSLSocketFactory(sslSocketFactory).build();
        } catch (Exception e) {
            throw new RuntimeException("创建忽略SSL认证HTTPClient异常", e);
        }
    }

    public static HttpClient defaultHttpClient() {
        return createHttpClient(200, 200, 12000, 12000, 12000);
    }

    public static HttpClient createHttpClient(int maxTotal, int defaultMaxPerRoute,
                                              int connectTimeout, int connectionRequestTimeout, int socketTimeout) {
        return createHttpClient0(maxTotal, defaultMaxPerRoute, connectTimeout, connectionRequestTimeout, socketTimeout).build();
    }

    public static HttpClientBuilder createHttpClient0(int maxTotal, int defaultMaxPerRoute,
                                                      int connectTimeout, int connectionRequestTimeout, int socketTimeout) {
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .setRedirectsEnabled(true)
                .build();

        ConnectionKeepAliveStrategy myStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 60 * 1000;
        };

        HttpClientBuilder clientBuilder = HttpClients.custom();
        clientBuilder.setConnectionManager(manager);
        clientBuilder.setKeepAliveStrategy(myStrategy);
        clientBuilder.setDefaultRequestConfig(requestConfig);
        return clientBuilder;
    }
}