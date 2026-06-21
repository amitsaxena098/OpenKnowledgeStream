package WikiIndexer.index;

import Wikicommon.models.Change;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpensearchIndexer {

    private final OpenSearchClient openSearchClient;

    public void index(Change change) throws Exception {
        openSearchClient.index(i -> i
                .index("wiki-changes")
                .id(change.getTitle())
                .document(change));
        log.info("Indexed Title: {}", change.getTitle());
    }

}
