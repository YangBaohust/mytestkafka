package kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

// 不指定分区
public class CustomerProducerAdjParam {

    public static void main(String[] args) throws InterruptedException, ExecutionException, Exception {

        // 配置连接
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "101.133.135.36:9092");

        // 序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 调整参数
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 缓冲区默认32M
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 批次默认16k
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 1000);  // 等待时间ms，默认0即不等待
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");  // 压缩模式

        // 设置ack
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);

        // 创建生产者
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        // 发送消息
        for (int i = 0; i < 5; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "xxx" + i), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        // 发送无异常
                        System.out.println("主题：" + recordMetadata.topic() + ", 分区：" + recordMetadata.partition());
                    } else {
                        // 发送异常，打印异常消息
                        e.printStackTrace();
                    }
                }
            });
        }

        // 关闭连接
        kafkaProducer.close();

    }

}

