package kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

// 不指定分区
public class CustomerProducerTransaction {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        // 配置连接
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "101.133.135.36:9092");

        // 序列化
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "my.test.transaction");

        // 创建生产者
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(properties);

        // 初始化事务
        kafkaProducer.initTransactions();
        // 开启事务
        kafkaProducer.beginTransaction();

        // 发送消息
        try {
            for (int i = 0; i < 50; i++) {
                kafkaProducer.send(new ProducerRecord<>("first", "hello" + i), new Callback() {
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
            kafkaProducer.commitTransaction();
        } catch (Exception e) {
            // 报错了
            System.out.println("报错了");
            kafkaProducer.abortTransaction();
        } finally {
            // 关闭连接
            kafkaProducer.close();
        }

    }

}

