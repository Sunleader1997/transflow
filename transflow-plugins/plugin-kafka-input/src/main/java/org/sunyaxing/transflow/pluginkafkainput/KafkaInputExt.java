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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;


@Extension
public class KafkaInputExt extends TransFlowInput {
    private static final Logger log = LogManager.getLogger(KafkaInputExt.class);
    private KafkaConsumer<String, String> kafkaConsumer;
    // 手动提交偏移量需要加锁
//    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public KafkaInputExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void commit(Long offset) {
//        log.info("提交偏移量 {}", offset);
    }

    @Override
    public List<TransData> dequeue() {
        // kafka 批量消费
        ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
        if (!consumerRecords.isEmpty()) {
            log.info("消费数据量：{}" , consumerRecords.count());
            List<TransData> res = new ArrayList<>();
            consumerRecords.forEach(record -> {
                TransData transData = new TransData(record.offset(), record.value());
                res.add(transData);
            });
            return res;
        }
        return null;
    }

    @Override
    public void run() {
    }

    @Override
    public void init(Properties config) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("bootstrap-servers", "127.0.0.1:9093"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, config.getProperty("group-id", "transflow"));
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getProperty("max-poll-records", "1000"));
        // 手动提交 offset false
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        log.info("初始化 KafkaConsumer {}", properties);
        this.kafkaConsumer.subscribe(List.of(config.getProperty("topics")));
    }

    @Override
    public void destroy() {
        this.kafkaConsumer.close();
        Thread.currentThread().interrupt();
    }
}