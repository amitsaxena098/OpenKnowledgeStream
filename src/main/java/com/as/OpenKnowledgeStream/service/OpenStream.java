package com.as.OpenKnowledgeStream.service;

import com.as.OpenKnowledgeStream.clients.WikipediaClient;
import com.as.OpenKnowledgeStream.exception.TooManyRequests;
import com.as.OpenKnowledgeStream.models.Change;
import com.as.OpenKnowledgeStream.models.Query;
import com.as.OpenKnowledgeStream.publish.KafkaPublish;
import lombok.extern.slf4j.Slf4j;
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
