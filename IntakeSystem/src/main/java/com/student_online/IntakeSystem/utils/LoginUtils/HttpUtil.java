package com.student_online.IntakeSystem.utils.LoginUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultRoutePlanner;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * koishikiss
 * 2025/7/10
 */

@Log4j2
public class HttpUtil {
    public static int maxTotalConnections = 200;
    public static int defaultMaxPerRoute = 20;
    public static int connectionRequestTimeout = 5000;
    public static int connectTimeout = 5000;
    public static int readTimeout = 5000;

    private static volatile PoolingHttpClientConnectionManager connectionManager = null;
    private static volatile HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = null;
    private static volatile ResponseErrorHandler responseErrorHandler = null;
    private static volatile RestTemplate request = null;

    private static final ThreadLocal<RequestConfig> REQUEST_CONTEXT = new NamedThreadLocal<>("HttpRequestContext");
    private static final ThreadLocal<HttpHost> PROXY_CONTEXT = new NamedThreadLocal<>("RoutePlannerContext");
    private static final ThreadLocal<CredentialsProvider> AUTH_CONTEXT = new NamedThreadLocal<>("AuthContext");

    private HttpUtil() {}

    /** get RestTemplate */
    public static RestTemplate getRequest() {
        if (request == null) {
            synchronized (HttpUtil.class) {
                if (request == null) {
                    initial();
                }
            }
        }
        return request;
    }

    public static void customizeConnectionManager(PoolingHttpClientConnectionManager connectionManager) {
        if (HttpUtil.connectionManager == null) {
            synchronized (HttpUtil.class) {
                if (HttpUtil.connectionManager == null) {
                    HttpUtil.connectionManager = connectionManager;
                }
            }
        }
    }

    public static void customizeClientHttpRequestFactory(HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
        if (HttpUtil.clientHttpRequestFactory == null) {
            synchronized (HttpUtil.class) {
                if (HttpUtil.clientHttpRequestFactory == null) {
                    HttpUtil.clientHttpRequestFactory = clientHttpRequestFactory;
                }
            }
        }
    }

    public static void customizeResponseErrorHandler(ResponseErrorHandler responseErrorHandler) {
        if (HttpUtil.responseErrorHandler == null) {
            synchronized (HttpUtil.class) {
                if (HttpUtil.responseErrorHandler == null) {
                    HttpUtil.responseErrorHandler = responseErrorHandler;
                }
            }
        }
    }

    @NotNull
    private static PoolingHttpClientConnectionManager initialConnectionManager() {
        return Objects.requireNonNullElseGet(connectionManager, () -> {
            // 使用Apache HttpClient作为连接池
            connectionManager = new PoolingHttpClientConnectionManager();
            // 最大连接数
            connectionManager.setMaxTotal(maxTotalConnections);
            // 每个路由的最大连接数
            connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
            
            return connectionManager;
        });
    }

    @NotNull
    private static CloseableHttpClient initialHttpClients() {
        return HttpClients.custom()
                .setConnectionManager(initialConnectionManager())
                .setRoutePlanner(new DefaultRoutePlanner(null) {  // 设置动态http代理
                    @Override
                    protected HttpHost determineProxy(HttpHost target, HttpContext context) {
                        return PROXY_CONTEXT.get();
                    }
                }).setDefaultCredentialsProvider((authScope, context) -> {
                    CredentialsProvider provider = AUTH_CONTEXT.get();
                    return provider == null ? null : provider.getCredentials(authScope, context);
                }).build();
    }

    @NotNull
    private static HttpComponentsClientHttpRequestFactory initialClientHttpRequestFactory() {
        // 初始化Apache HttpClient
        CloseableHttpClient httpClient = initialHttpClients();

        if (clientHttpRequestFactory == null) {
            clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            // 连接池获取连接最大等待时间
            clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
            // 完成三次握手建立连接最大时间
            clientHttpRequestFactory.setConnectTimeout(connectTimeout);
            // 建立连接后从服务器读取到可用资源所用的最大时间
            clientHttpRequestFactory.setReadTimeout(readTimeout);
        }
        else {
            clientHttpRequestFactory.setHttpClient(httpClient);
        }

        // 根据http上下文设置请求属性
        clientHttpRequestFactory.setHttpContextFactory((httpMethod, uri) -> {
            RequestConfig requestConfig = REQUEST_CONTEXT.get();
            if (requestConfig == null) {
                return null;
            } else {
                HttpClientContext httpClientContext = HttpClientContext.create();
                httpClientContext.setRequestConfig(requestConfig);
                return httpClientContext;
            }
        });
        return clientHttpRequestFactory;
    }

    @NotNull
    private static ResponseErrorHandler initialResponseErrorHandler() {
        return Objects.requireNonNullElseGet(responseErrorHandler, () -> {
            responseErrorHandler = new ResponseErrorHandler() {  // 默认错误处理
                @Override
                public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                    return response.getStatusCode().is4xxClientError() ||
                            response.getStatusCode().is5xxServerError();
                }

                @Override
                public void handleError(@NotNull URI url, @NotNull HttpMethod method, @NotNull ClientHttpResponse response) throws IOException {
                    String message = String.format("%s %s 失败 [%d %s]: %s",
                            method, url, response.getStatusCode().value(), response.getStatusText(),
                            new BufferedReader(new InputStreamReader(response.getBody()))).lines().collect(Collectors.joining(System.lineSeparator()));

                    if (response.getStatusCode().is5xxServerError()) {
                        throw new HttpServerErrorException(response.getStatusCode(), message);
                    } else if (response.getStatusCode().is4xxClientError()) {
                        throw new HttpClientErrorException(response.getStatusCode(), message);
                    }
                }
            };
            return responseErrorHandler;
        });
    }
    
    public static void initial() {
        // 创建 RestTemplate
        request = new RestTemplate(initialClientHttpRequestFactory());
        request.setErrorHandler(initialResponseErrorHandler());
    }

    /** start a http request */
    public static Connection connect(String url) throws URISyntaxException {
        return connect(new URI(url));
    }

    /** start a http request */
    public static Connection connect(URI url) {
        return new Connection(url);
    }

    /** connection pool monitor */
    public static void printConnectionPoolStats() {
        log.info("Total connections: " + connectionManager.getTotalStats().getAvailable());
        log.info("Leased connections: " + connectionManager.getTotalStats().getLeased());
    }

    /** connection pool shutdown */
    public static void shutdown() {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }

    public static class Connection {
        private final URI url;
        private HttpMethod httpMethod = HttpMethod.GET;
        private final HttpHeaders httpHeaders = new HttpHeaders();
        private RequestConfig requestConfig = null;
        private HttpHost httpHost = null;
        private Credentials credentials = null;
        private Object body;
        private RestTemplate client;

        private Connection(URI url) {
            this.url = url;
            client = new RestTemplate(initialClientHttpRequestFactory());
            client.setErrorHandler(initialResponseErrorHandler());
        }

        /** set http method */
        public Connection method(@NotNull HttpMethod method) {
            httpMethod = method;
            return this;
        }

        /** add header */
        public Connection header(@NotNull String key, @NotNull String value) {
            httpHeaders.add(key, value);
            return this;
        }

        /** add all headers */
        public Connection headers(MultiValueMap<String, String> headers) {
            if (headers == null) {
                return this;
            }
            httpHeaders.addAll(headers);
            return this;
        }

        /** set content type */
        public Connection contentType(@NotNull MediaType mediaType) {
            httpHeaders.setContentType(mediaType);
            return this;
        }

        /** add cookie */
        public Connection cookie(@NotNull String key,@NotNull String value) {
            httpHeaders.add(HttpHeaders.COOKIE, String.format("%s=%s", key, value));
            return this;
        }

        /** add all cookies */
        public Connection cookies(@NotNull Map<String, String> cookies) {
            cookies.entrySet().iterator().forEachRemaining(entry ->
                    httpHeaders.add(HttpHeaders.COOKIE, String.format("%s=%s", entry.getKey(), entry.getValue()))
            );
            return this;
        }

        public Connection cookies(@NotNull String cookies) {
            Arrays.stream(cookies.split(";")).iterator().forEachRemaining(
                    cookie -> {
                        String[] kv = cookie.split("=");
                        this.cookie(kv[0], kv.length == 2 ? kv[1] : "");
                    }
            );
            return this;
        }

        /** set body of json */
        public Connection jsonBody(Object body) {
            this.body = body;
            return this;
        }

        /** set body of formData */
        public Connection formData(Map<String, ?> formData) {
            this.body = MultiValueMap.fromSingleValue(formData);
            return this;
        }

        /** set body of formData */
        public Connection formData(MultiValueMap<String, ?> formData) {
            this.body = formData;
            return this;
        }

        /** set body of formData by builder */
        public ConnectionFormDataBuilder formData() {
            return new ConnectionFormDataBuilder(this);
        }

        /** set request config */
        public Connection config(RequestConfig requestConfig) {
            this.requestConfig = requestConfig;
            return this;
        }

        /** ser request config by builder */
        public ConnectionRequestConfigBuilder config() {
            return new ConnectionRequestConfigBuilder(this);
        }

        /** execute http request */
        public Response<String> execute() {
            return execute(String.class);
        }

        /** execute http request */
        public <T> Response<T> execute(Class<T> responseType) {
            return execute((Type) responseType);
        }

        /** execute http request */
        public <T> Response<T> execute(TypeReference<T> typeReference) {
            return execute(typeReference.getType());
        }

        private <T> Response<T> execute(Type type) {
            try {
                this.loadBeforeRequest();
                RequestCallback requestCallback = client.httpEntityCallback(new HttpEntity<>(body, httpHeaders), type);
                ResponseExtractor<ResponseEntity<T>> responseExtractor = client.responseEntityExtractor(type);
                return Response.of(client.execute(url, httpMethod, requestCallback, responseExtractor));
            } finally {
                this.unloadAfterRequest();
            }
        }

        private void loadBeforeRequest() {
            // 加载上下文
            if (requestConfig != null) {
                REQUEST_CONTEXT.set(requestConfig);
            }
            if (httpHost != null) {
                PROXY_CONTEXT.set(httpHost);
                if (credentials != null) {
                    BasicCredentialsProvider provider = new BasicCredentialsProvider();
                    provider.setCredentials(new AuthScope(httpHost), credentials);
                    AUTH_CONTEXT.set(provider);
                }
            }
            log.debug("Sending {} request to {} with headers: {}", httpMethod, url, httpHeaders);
        }

        private void unloadAfterRequest() {
            REQUEST_CONTEXT.remove();
            PROXY_CONTEXT.remove();
            AUTH_CONTEXT.remove();
        }
    }

    public static class ConnectionFormDataBuilder {
        private final Connection connection;
        private final MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        private boolean hasFile = false;
        private ConnectionFormDataBuilder(Connection connection) {
            this.connection = connection;
        }

        /** build formData param */
        public ConnectionFormDataBuilder data(@NotNull String key, @NotNull Object value) {
            formData.add(key, value);
            return this;
        }

        /** build formData param of file */
        public ConnectionFormDataBuilder file(@NotNull String name, @NotNull Resource file) {
            formData.add(name, file);
            hasFile = true;
            return this;
        }

        /** build formData param of file */
        public ConnectionFormDataBuilder file(@NotNull String name, @NotNull File file) {
            formData.add(name, new FileSystemResource(file));
            hasFile = true;
            return this;
        }

        /** build formData param of file from stream */
        public ConnectionFormDataBuilder file(String name, InputStream stream, String filename) {
            formData.add(name, new InputStreamResource(stream) {
                @Override
                public @NotNull String getFilename() {
                    return filename;
                }
            });
            hasFile = true;
            return this;
        }

        /** build formData */
        public Connection set() {
            return connection
                    .formData(formData)
                    .contentType(hasFile ? MediaType.MULTIPART_FORM_DATA : MediaType.APPLICATION_FORM_URLENCODED);
        }
    }

    public static class ConnectionRequestConfigBuilder {
        private final Connection connection;
        private final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        private HttpHost httpHost = null;
        private ConnectionRequestConfigBuilder(Connection connection) {
            this.connection = connection;
        }

        /** set if allow redirect */
        public ConnectionRequestConfigBuilder followRedirects(boolean redirectsEnabled) {
            requestConfigBuilder.setRedirectsEnabled(redirectsEnabled);
            return this;
        }

        /** set max redirect times */
        public ConnectionRequestConfigBuilder maxRedirects(int maxRedirect) {
            requestConfigBuilder.setMaxRedirects(maxRedirect);
            return this;
        }

        /** set total timeout */
        public ConnectionRequestConfigBuilder timeout(long timeout, TimeUnit timeUnit) {
            requestConfigBuilder.setResponseTimeout(Timeout.of(timeout, timeUnit));
            return this;
        }

        /** set total timeout */
        public ConnectionRequestConfigBuilder timeout(Timeout timeout) {
            requestConfigBuilder.setResponseTimeout(timeout);
            return this;
        }

        /** set if keep alive */
        public ConnectionRequestConfigBuilder keepAlive(boolean keepAlive) {
            requestConfigBuilder.setConnectionKeepAlive(
                    Timeout.of(keepAlive ? 30 : 0, TimeUnit.SECONDS));
            return this;
        }

        /** set http proxy */
        public ConnectionRequestConfigBuilder proxy(String host, int port) {
            httpHost = new HttpHost(host, port);
            return this;
        }

        /** set http proxy */
        public ConnectionRequestConfigBuilder proxy(String scheme, String host, int port) {
            httpHost = new HttpHost(scheme, host, port);
            return this;
        }

        /** set http proxy */
        public ConnectionRequestConfigBuilder proxy(HttpHost proxy) {
            httpHost = proxy;
            return this;
        }

        /** set http proxy credentials */
        public ConnectionRequestConfigBuilder proxyAuth(String username, String password) {
            connection.credentials = new UsernamePasswordCredentials(username, password.toCharArray());
            return this;
        }

        /** set http proxy credentials */
        public ConnectionRequestConfigBuilder proxyAuth(Credentials credentials) {
            connection.credentials = credentials;
            return this;
        }

        // 支持多种认证类型
//        public ConnectionRequestConfigBuilder proxyAuth(String username, String password, AuthScope scope) {
//            connection.credentials = new UsernamePasswordCredentials(username, password.toCharArray());
//            connection.authScope = scope;
//            return this;
//        }

        /** build requestConfig */
        public Connection set() {
            connection.httpHost = httpHost;
            connection.requestConfig = requestConfigBuilder.build();
            return connection;
        }

    }

    public static class Response<T> {
        private static final ObjectMapper objectMapper = new ObjectMapper();
        @Getter
        private HttpStatusCode statusCode;
        @Getter
        private HttpHeaders httpHeaders;
        private T body;
        private Map<String, HttpCookie> cookies;

        private Response() {}

        public static <T> Response<T> of(ResponseEntity<T> responseEntity) {
            Response<T> response = new Response<>();
            response.statusCode = responseEntity.getStatusCode();
            response.httpHeaders = responseEntity.getHeaders();
            response.body = responseEntity.getBody();
            response.parseCookies();
            return response;
        }

        @SuppressWarnings("all") // idea这个神经病把cookieHeaders == null标黄了，迫不得已使用SuppressWarnings
        private void parseCookies() {
            List<String> cookieHeaders = httpHeaders.get(HttpHeaders.SET_COOKIE);
            cookies = cookieHeaders == null ? new LinkedHashMap<>() : cookieHeaders.stream()
                    .map(java.net.HttpCookie::parse)
                    .collect(Collectors.toMap(
                            httpCookies -> httpCookies.get(0).getName(),
                            httpCookies -> httpCookies.get(0),
                            (value1, value2) -> value1,
                            LinkedHashMap::new));
        }

        public int statusCode() {
            return statusCode.value();
        }

        @Nullable
        @SuppressWarnings("all")
        public String header(String name) {
            List<String> headerList = httpHeaders.get(name);
            return headerList == null || headerList.size() == 0 ? null : StringUtil.join(headerList, ",");
        }

        @Nullable
        public List<String> headerList(String name) {
            return httpHeaders.get(name);
        }

        public List<Locale.LanguageRange> languages() {
            return httpHeaders.getAcceptLanguage();
        }

        @Nullable
        @SuppressWarnings("all")
        public String location() {
            return httpHeaders.getLocation() == null ? null : httpHeaders.getLocation().toString();
        }

        @Nullable
        public URI locationURI() {
            return httpHeaders.getLocation();
        }

        public long contentLength() {
            return httpHeaders.getContentLength();
        }

        public MediaType contentType() {
            return httpHeaders.getContentType();
        }

        public long date() {
            return httpHeaders.getDate();
        }

        public InetSocketAddress host() {
            return httpHeaders.getHost();
        }

        public String origin() {
            return httpHeaders.getOrigin();
        }

        public Map<String, String> cookies() {
            return cookies.entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getValue().getName(),
                    entry -> entry.getValue().getValue()));
        }

        public Map<String, HttpCookie> httpCookies() {
            return cookies;
        }

        public String cookie(String name) {
            HttpCookie httpCookie = cookies.get(name);
            return httpCookie == null ? null : httpCookie.getValue();
        }

        public HttpCookie httpCookie(String name) {
            return cookies.get(name);
        }

        public T body() {
            return body;
        }

        public String stringBody() throws JsonProcessingException {
            if (body instanceof String) {
                return (String) body;
            }
            return objectMapper.writeValueAsString(body);
        }

        public <S> S convert(Class<S> clazz) throws JsonProcessingException {
            if (body instanceof String) {
                return objectMapper.readValue((String) body, clazz);
            }
            return objectMapper.convertValue(body, clazz);
        }

        public <S> S convert(TypeReference<S> typeReference) throws JsonProcessingException {
            if (body instanceof String) {
                return objectMapper.readValue((String) body, typeReference);
            }
            return objectMapper.convertValue(body, typeReference);
        }

        public Document parse() {
            return Jsoup.parse(String.valueOf(body));
        }

        @Override
        public String toString() {
            return "Response{" +
                    "statusCode=" + statusCode.value() +
                    ", httpHeaders=" + httpHeaders.toString() +
                    ", body=" + body.toString() +
                    '}';
        }
    }

}
