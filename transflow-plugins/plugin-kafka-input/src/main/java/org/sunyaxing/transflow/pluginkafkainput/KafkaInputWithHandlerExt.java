package org.sunyaxing.transflow.pluginkafkainput;

import com.alibaba.fastjson2.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;


@Extension
public class KafkaInputWithHandlerExt extends TransFlowInput<ConsumerRecords<String, String>, String> {
    private static final Logger log = LogManager.getLogger(KafkaInputWithHandlerExt.class);
    private String groupId;
    private KafkaConsumer<String, String> kafkaConsumer;
    private final AtomicLong senNumb = new AtomicLong(0);
    private final Thread kafkaConsumerThread;

    public KafkaInputWithHandlerExt(ExtensionContext extensionContext) {
        super(extensionContext);
        log.info("create");
        this.kafkaConsumerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // kafka 批量消费
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
                this.handlerMap.forEach((handlerId, handler) -> {

                });
                if (!consumerRecords.isEmpty()) {
                    this.putQueueLast(new HandleData<>(null, new TransData<>(null, consumerRecords)));
                    senNumb.addAndGet(consumerRecords.count());
                }
            }
        });
    }

    @Override
    protected HandleData<String> parseRToHandleData(HandleData<ConsumerRecords<String, String>> data) {
        return null;
    }

    @Override
    public void commit(HandleData offset) {
//        log.info("提交偏移量 {}", offset);
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
    }

    @Override
    public Function<TransData<ConsumerRecords<String, String>>, String> parseHandleToConsumer(String handleId, String topic) {
        return tarnsData -> {
            tarnsData.getData().records(topic).forEach(record -> {
                TransData<String> transData = new TransData<>(record.offset(), record.value());
                HandleData<String> handleData = new HandleData<>(handleId, transData);
            });
            return null;
        };
    }

    @Override
    public Function<ConsumerRecords<String, String>, String> s(String handleId, String topic) {
        return consumerRecords -> {
            consumerRecords.records(topic).forEach(record -> {
                TransData transData = new TransData(record.offset(), record.value());
                HandleData handleData = new HandleData(handleId, transData);
                this.putQueueLast(handleData);
            });
            return null;
        };
    }
    public static class KafkaInputFunction implements Function<TransData<ConsumerRecords<String, String>>, String> {
        private final String topic;
        public KafkaInputFunction(String topic) {
            this.topic = topic;
        }

        @Override
        public String apply(TransData<ConsumerRecords<String, String>> consumerRecordsTransData) {
            consumerRecordsTransData.getData().records(topic).forEach(record -> {

            });
            return consumerRecords.records();
        }
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