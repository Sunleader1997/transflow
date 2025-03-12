package org.sunyaxing.transflow.pluginesoutput;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Extension
public class EsOutputExtension extends TransFlowOutput {
    private static final Logger log = LoggerFactory.getLogger(EsOutputExtension.class);
    private RestHighLevelClient restHighLevelClient;
    private String indexName;

    public EsOutputExtension(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public void output(List<TransData> data) {
        BulkRequest bulkRequest = new BulkRequest();
        data.forEach(transData -> {
            Map sourceData = transData.getData(Map.class);
            bulkRequest.add(new IndexRequest(indexName, "_doc").source(sourceData));
        });
        try {
            this.restHighLevelClient.bulk(bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es 存储异常", e);
        }
    }

    @Override
    public void init(Properties config) {
        this.indexName = config.getProperty("index-name", "transflow");
        HttpHost targetHost = new HttpHost(
                config.getProperty("host", "127.0.0.1"),
                Integer.valueOf(config.getProperty("port", "9200")),
                config.getProperty("scheme", "http")
        );
        RestClientBuilder builder = RestClient.builder(targetHost);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            config.getProperty("username", "elastic"),
                            config.getProperty("password", "elastic")
                    )
            );
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        });
        this.restHighLevelClient = new RestHighLevelClient(builder);
    }

    @Override
    public void destroy() {
        if(restHighLevelClient!=null){
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
