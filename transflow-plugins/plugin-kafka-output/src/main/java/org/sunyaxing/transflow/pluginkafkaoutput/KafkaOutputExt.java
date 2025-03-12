package org.sunyaxing.transflow.pluginkafkaoutput;

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
    private String topic;

    public KafkaOutputExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public void output(List<TransData> data) {
        data.forEach(transData -> {
            this.producer.send(new ProducerRecord<>(topic, transData.getData(String.class)), (metadata, e) -> {
                if (e != null) {
                    log.error("kafka output 异常", e);
                }
            });
        });
    }

    @Override
    public void init(Properties config) {
        this.topic = config.getProperty("topic");
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("bootstrap-servers", "127.0.0.1:9093"));
        properties.put(ProducerConfig.ACKS_CONFIG, config.getProperty("acks", "0"));
        properties.put(ProducerConfig.RETRIES_CONFIG, config.getProperty("retries", "3"));
        properties.put(ProducerConfig.LINGER_MS_CONFIG, config.getProperty("linger", "1"));
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getProperty("buffer-memory", "524288"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        this.producer = new KafkaProducer<>(properties);
        log.info("初始化 KafkaProducer {}", properties);
    }

    @Override
    public void destroy() {

    }
}
