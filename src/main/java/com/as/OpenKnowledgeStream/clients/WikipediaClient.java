package com.as.OpenKnowledgeStream.clients;

import com.as.OpenKnowledgeStream.models.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class WikipediaClient {

    private final WebClient webClient;

    public Query getRecentChanges() {
        return this.webClient
                .get()
                .retrieve()
                .bodyToMono(Query.class)
                .block();
    }
}
