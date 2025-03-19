package org.sunyaxing.transflow.pluginkafkainput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.TransFlowMultiInput;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;


@Extension
public class KafkaInputExt extends TransFlowMultiInput {
    private static final Logger log = LogManager.getLogger(KafkaInputExt.class);
    private String groupId;
    private KafkaConsumer<String, String> kafkaConsumer;
    private AdminClient adminClient;
    private final AtomicLong senNumb = new AtomicLong(0);

    public KafkaInputExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public Long getSendNumb() {
        return senNumb.get();
    }

    @Override
    public Long getRemainingDataSize() {
        AtomicLong count = new AtomicLong(0);
        try {
            ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition, OffsetAndMetadata> metaDataMap = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
            metaDataMap.forEach((topicPartition, offsetAndMetadata) -> {
                if (this.handleMap.values().contains(topicPartition.topic())) {
                    int lag = offsetAndMetadata.leaderEpoch().orElse(0);
                    count.addAndGet(lag);
                }
            });
        } catch (Exception e) {
            log.error("获取 kafka offset 异常", e);
        }
        return count.get();
    }

    @Override
    public void commit(HandleData offset) {
//        log.info("提交偏移量 {}", offset);
    }

    @Override
    public List<HandleData> handleDequeue() {
        List<HandleData> res = new ArrayList<>();
        // kafka 批量消费
        ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
        if (!consumerRecords.isEmpty()) {
            this.handleMap.forEach((handleId, handleV) -> {
                HandleData handleData = new HandleData(handleId, new ArrayList<>());
                consumerRecords.records(handleV).forEach(record -> {
                    handleData.getTransData().add(new TransData(record.offset(), record.value()));
                });
                if (!handleData.getTransData().isEmpty()) {
                    res.add(handleData);
                }
            });
            senNumb.addAndGet(consumerRecords.count());
            return res.isEmpty() ? null : res;
        }
        return null;
    }

    @Override
    protected void initSelf(JSONObject config, List<Handle> handles) {
        this.groupId = config.getString("group-id");
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString("bootstrap-servers"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getString("max-poll-records"));
        // 手动提交 offset false
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        this.kafkaConsumer = new KafkaConsumer<>(properties);
        List<String> topic = new ArrayList<>();
        handles.forEach(handle -> {
            topic.add(handle.getValue());
        });
        this.kafkaConsumer.subscribe(topic);
        this.adminClient = AdminClient.create(properties);
    }

    @Override
    public void destroy() {
        log.info("kafka 消费者 执行清理");
        this.adminClient.close();
        this.kafkaConsumer.close();
    }
}