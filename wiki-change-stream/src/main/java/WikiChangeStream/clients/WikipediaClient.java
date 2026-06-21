package WikiChangeStream.clients;

import WikiChangeStream.exception.TooManyRequests;
import Wikicommon.models.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WikipediaClient {

    private final WebClient webClient;

    public Query getRecentChanges() {
        return this.webClient
                .get()
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() == 429, clientResponse ->
                        Mono.error(new TooManyRequests("Too many requests...")))
                .bodyToMono(Query.class)
                .block();
    }
}
