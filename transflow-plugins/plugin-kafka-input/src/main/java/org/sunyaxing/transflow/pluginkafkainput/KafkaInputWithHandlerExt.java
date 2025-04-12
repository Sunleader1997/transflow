package org.sunyaxing.transflow.pluginkafkainput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pf4j.Extension;
import org.sunyaxing.transflow.HandleData;
import org.sunyaxing.transflow.TransData;
import org.sunyaxing.transflow.common.Handle;
import org.sunyaxing.transflow.extensions.base.ExtensionContext;
import org.sunyaxing.transflow.extensions.base.types.TransFlowInput;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;


@Extension
public class KafkaInputWithHandlerExt extends TransFlowInput<ConsumerRecord<String, String>, String> {
    private static final Logger log = LogManager.getLogger(KafkaInputWithHandlerExt.class);
    private String groupId;
    private KafkaConsumer<String, String> kafkaConsumer;
    private Thread kafkaConsumerThread;

    public KafkaInputWithHandlerExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
    }

    @Override
    public void commit(HandleData<String> handleData) {

    }

    @Override
    protected void afterInitHandler(JSONObject config, List<Handle> handles) {
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

        this.kafkaConsumerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // kafka 批量消费
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                consumerRecords.forEach(consumerRecord -> {
                    // 通过 topic 获取 handleId
                    String recordTopic = consumerRecord.topic();
                    String handleId = this.findHandleIdByValue(recordTopic);
                    TransData<ConsumerRecord<String, String>> transData = new TransData<>(consumerRecord.offset(), consumerRecord);
                    this.handle(handleId, transData);
                });
            }
        });
        this.kafkaConsumerThread.start();
    }

    @Override
    public Function<TransData<ConsumerRecord<String, String>>, String> parseHandleToConsumer(String handleId, String topic) {
        return tarnsData -> {
            ConsumerRecord<String, String> record = tarnsData.getData();
            return record.value();
        };
    }

    @Override
    public void destroy() {
        log.info("kafka 消费者 执行清理");
        try {
            this.kafkaConsumerThread.interrupt();
            this.kafkaConsumer.unsubscribe();
            this.kafkaConsumer.close();
        } catch (Exception e) {
            log.error("kafka 销毁异常", e);
        }
    }
}