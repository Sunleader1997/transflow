package org.sunyaxing.transflow.pluginesoutput;

import com.alibaba.fastjson2.JSONObject;
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
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowOutputWithHandler;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.handlers.Handler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Extension
public class EsOutputExtension extends TransFlowOutputWithHandler<List<IndexRequest>> {
    private static final Logger log = LoggerFactory.getLogger(EsOutputExtension.class);
    private RestHighLevelClient restHighLevelClient;
    private String indexName;

    public EsOutputExtension(ExtensionContext extensionContext) {
        super(extensionContext);
    }


    @Override
    public List<HandleData> exec(HandleData handleData) {
        return Collections.emptyList();
    }

    @Override
    protected void execData(List<IndexRequest> indexRequests) {
        BulkRequest bulkRequest = new BulkRequest();
        try {
            this.restHighLevelClient.bulk(bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL), RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("es 存储异常", e);
        }
    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
        this.indexName = config.getString("index-name");
        HttpHost targetHost = new HttpHost(
                config.getString("host"),
                config.getInteger("port"),
                config.getString("scheme")
        );
        RestClientBuilder builder = RestClient.builder(targetHost);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY,
                    new UsernamePasswordCredentials(
                            config.getString("username"),
                            config.getString("password")
                    )
            );
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        });
        this.restHighLevelClient = new RestHighLevelClient(builder);
    }

    @Override
    public Handler<List<TransData>, List<IndexRequest>> parseHandleToHandler(String handleId, String indexName) {
        return new EsOutputHandler(indexName);
    }

    @Override
    public void destroy() {
        log.info("es client 执行清理");
        if (restHighLevelClient != null) {
            try {
                restHighLevelClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class EsOutputHandler implements Handler<List<TransData>, List<IndexRequest>> {
        private final String indexName;

        public EsOutputHandler(String indexName) {
            this.indexName = indexName;
        }

        @Override
        public List<IndexRequest> resolve(List<TransData> transDatas) {
            return transDatas.stream().map(transData -> {
                Map sourceData = transData.getData(Map.class);
                return new IndexRequest(indexName, "_doc").source(sourceData);
            }).collect(Collectors.toList());
        }
    }
}
