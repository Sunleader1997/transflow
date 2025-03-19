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
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowOutput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

@Extension
public class KafkaOutputExt extends TransFlowOutput {
    private static final Logger log = LogManager.getLogger(KafkaOutputExt.class);

    private KafkaProducer<String, String> producer;
    private AtomicLong rec = new AtomicLong(0L);

    public KafkaOutputExt(ExtensionContext extensionContext) {
        super(extensionContext);
    }

    @Override
    public Long getRecNumb() {
        return rec.get();
    }

    @Override
    public List<TransData> execDatas(String handleValue, List<TransData> data) {
        rec.addAndGet(data.size());
        data.forEach(transData -> {
            this.producer.send(new ProducerRecord<>(handleValue, transData.getData(String.class)), (metadata, e) -> {
                if (e != null) {
                    log.error("kafka output 异常", e);
                }
            });
        });
        return data;
    }

    @Override
    public void initSelf(JSONObject config, List<Handle> handles) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("bootstrap-servers"));
        properties.put(ProducerConfig.ACKS_CONFIG, config.getOrDefault("acks","0"));
        properties.put(ProducerConfig.RETRIES_CONFIG, config.getOrDefault("retries","3"));
        properties.put(ProducerConfig.LINGER_MS_CONFIG, config.getOrDefault("linger","1"));
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getOrDefault("buffer-memory","524288"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        this.producer = new KafkaProducer<>(properties);
        log.info("初始化 KafkaProducer {}", properties);
    }

    @Override
    public void destroy() {
        log.info("kafka 生产者 资源清理");
        this.producer.close();
    }
}
