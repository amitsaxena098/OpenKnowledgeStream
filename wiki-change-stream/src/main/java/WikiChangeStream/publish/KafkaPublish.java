package WikiChangeStream.publish;

import Wikicommon.models.Change;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
public class KafkaPublish {
    private final String topic = "recent_change_stream";
    KafkaProducer<String, Change> producer = null;

    KafkaPublish() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", JsonSerializer.class.getName());
        producer = new KafkaProducer<>(properties);
    }

    public void publish(Change recentChange) {
        ProducerRecord<String, Change> record = new ProducerRecord<>(topic, recentChange);

        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if(exception == null) {
                    log.info("Change published with title: {}", recentChange.getTitle());
                } else {
                    log.error("Exception occurred while publish: {}", exception.getMessage());
                }
            }
        });
    }
}
