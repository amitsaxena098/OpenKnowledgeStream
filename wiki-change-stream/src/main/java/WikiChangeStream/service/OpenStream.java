package WikiChangeStream.service;

import WikiChangeStream.clients.WikipediaClient;
import WikiChangeStream.exception.TooManyRequests;
import WikiChangeStream.publish.KafkaPublish;
import Wikicommon.models.Change;
import Wikicommon.models.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OpenStream {
    private final WikipediaClient wikipediaClient;
    private final KafkaPublish kafkaPublish;

    OpenStream(WikipediaClient wikipediaClient, KafkaPublish kafkaPublish) {
        this.wikipediaClient = wikipediaClient;
        this.kafkaPublish = kafkaPublish;
    }

    @Scheduled(fixedRate = 5000)
    private void stream() {
        try {
            Query query = wikipediaClient.getRecentChanges();
            for(Change change : query.getQuery().getRecentChanges()) {
                kafkaPublish.publish(change);
            }
        } catch (TooManyRequests ex) {
            log.warn("Request limit hit...sleeping for 5 seconds...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        catch (Exception ex) {
            log.error("Exception occurred: {}", ex.getMessage());
        }
    }
}
