package org.sunyaxing.transflow.pluginkafkainput;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.extensions.TransFlowInput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;


@Extension
public class KafkaInputExt extends TransFlowInput<String> {
    private static final Logger log = LogManager.getLogger(KafkaInputExt.class);
    private KafkaConsumer<String, String> kafkaConsumer;
    private AtomicLong atomicLong = new AtomicLong(0);

    public KafkaInputExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void commit(Long offset) {
        log.info("提交偏移量 {}", offset);
        this.kafkaConsumer.commitAsync();
    }

    @Override
    public void run() {
        this.kafkaConsumer.subscribe(List.of("topic1"));
        while (!Thread.currentThread().isInterrupted()) {
            // kafka 批量消费
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(record -> {
                    TransData<String> transData = new TransData<>(record.offset(), record.value());
                    put(transData);
                });
            }
        }
    }

    @Override
    public void init() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        // 手动提交 offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        this.kafkaConsumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void destroy() {
        Thread.currentThread().interrupt();
    }
}