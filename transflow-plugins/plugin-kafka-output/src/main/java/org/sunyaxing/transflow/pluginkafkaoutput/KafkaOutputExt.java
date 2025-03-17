package org.sunyaxing.transflow.pluginkafkaoutput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Assert;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.Properties;

@Extension
public class KafkaOutputExt extends TransFlowOutput {
    private static final Logger log = LogManager.getLogger(KafkaOutputExt.class);

    private KafkaProducer<String, String> producer;

    public KafkaOutputExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public List<TransData> execDatas(String handle, List<TransData> data) {
        data.forEach(transData -> {
            this.producer.send(new ProducerRecord<>(handle, transData.getData(String.class)), (metadata, e) -> {
                if (e != null) {
                    log.error("kafka output 异常", e);
                }
            });
        });
        return data;
    }

    @Override
    public void init(JSONObject config) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("bootstrap-servers"));
        properties.put(ProducerConfig.ACKS_CONFIG, config.getString("acks"));
        properties.put(ProducerConfig.RETRIES_CONFIG, config.getString("retries"));
        properties.put(ProducerConfig.LINGER_MS_CONFIG, config.getString("linger"));
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getString("buffer-memory"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        this.producer = new KafkaProducer<>(properties);
        log.info("初始化 KafkaProducer {}", properties);
    }

    @Override
    public void destroy() {

    }
}
