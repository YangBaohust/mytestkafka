package kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;



// 自定义分区器
public class CustomerProducerCustomPartition {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // 配置连接
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "101.133.135.36:9092");

        // 序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // 添加自定义分区器
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "kafka.producer.MyPartitioner");

        // 创建生产者
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        // 发送消息
        for (int i = 0; i < 50; i++) {
            // 1. 指定分区 kafkaProducer.send(new ProducerRecord<>("myfirst", 1, "", "hello" + i)
            // 2. 指定key，通过key的hash自动分区 kafkaProducer.send(new ProducerRecord<>("first", "c", "hello" + i)
            kafkaProducer.send(new ProducerRecord<>("first", "c", "helo" + i), new Callback() {
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
            }).get();  // 不加get()为异步，加了变成同步

        }

        // 关闭连接
        kafkaProducer.close();

    }

}

