package com.moxi.mogublog.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.Setter;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "easy-es") //配置的前缀
@Configuration
public class ClientConfig {

    @Setter
    private String address;

    @Setter
    private String userName;

    @Setter
    private String password;

    /**
     * 解析配置的字符串，转为HttpHost对象数组
     *
     * @return
     */
    private HttpHost[] toHttpHost() {
        if (!StringUtils.hasLength(address)) {
            throw new RuntimeException("invalid elasticsearch configuration");
        }

        String[] hostArray = address.split(",");
        HttpHost[] httpHosts = new HttpHost[hostArray.length];
        HttpHost httpHost;
        for (int i = 0; i < hostArray.length; i++) {
            String[] strings = hostArray[i].split(":");
            httpHost = new HttpHost(strings[0], Integer.parseInt(strings[1]), "http");
            httpHosts[i] = httpHost;
        }

        return httpHosts;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        HttpHost[] httpHosts = toHttpHost();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
        RestClientBuilder builder = RestClient.builder(httpHosts);
        // 配置密码
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        // 配置相关参数
        RestClient restClient = builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(5000)
                .setSocketTimeout(10000)).build();
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @Bean
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
        HttpHost[] httpHosts = toHttpHost();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", password));
        RestClientBuilder builder = RestClient.builder(httpHosts);
        // 配置密码
        builder.setHttpClientConfigCallback(httpAsyncClientBuilder ->
                httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        RestClient restClient = builder.build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchAsyncClient(transport);
    }
}