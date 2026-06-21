package WikiIndexer.consumer;

import WikiIndexer.index.OpensearchIndexer;
import Wikicommon.models.Change;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Component
@Slf4j

public class KafkaConsume {

    private final OpensearchIndexer opensearchIndexer;
    KafkaConsumer<String, Change> consumer;

    KafkaConsume(OpensearchIndexer opensearchIndexer) {
        this.opensearchIndexer = opensearchIndexer;
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", JsonDeserializer.class.getName());
        properties.put("group.id", "wiki-indexer");
        properties.put("auto.offset.reset", "earliest");
        properties.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "*"
        );
        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("recent_change_stream"));

    }

    @Scheduled(fixedRate = 5000)
    public void consume() throws Exception {
        log.info("Starting kafka consumer....");

        ConsumerRecords<String, Change> record = consumer.poll(Duration.ofMillis(1000));
        for(ConsumerRecord<String, Change> change : record) {
            opensearchIndexer.index(change.value());
        }
    }
}
