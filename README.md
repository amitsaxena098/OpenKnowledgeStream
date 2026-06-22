> ## Documentation Index
> Fetch the complete documentation index at: [https://mintlify.com/amitsaxena098/OpenKnowledgeStream](https://mintlify.wiki/amitsaxena098/OpenKnowledgeStream)

# OpenKnowledgeStream: Real-Time Wikipedia Change Streaming

> Open-source Spring Boot 4 / Java 21 pipeline that polls Wikipedia for recent page edits, publishes them to Kafka, and indexes them in OpenSearch.

OpenKnowledgeStream is an open-source, multi-module Java pipeline that continuously polls the [Wikipedia Recent Changes API](https://en.wikipedia.org/w/api.php?action=query\&list=recentchanges\&format=json), publishes every detected page edit as a structured event to an Apache Kafka topic, and then consumes those events to index them in real time into OpenSearch. The result is a searchable, always-up-to-date record of every change happening across the English Wikipedia — with zero manual intervention required after startup.

## Architecture

The project is organized as a Maven multi-module build with three distinct modules that collaborate through shared Kafka topics and a common data model:

<CardGroup cols={3}>
  <Card title="wiki-change-stream" icon="broadcast-tower" href="#wiki-change-stream-producer">
    The **producer** module. Runs a scheduled Spring task every 5 seconds, calls the Wikipedia API via WebFlux `WebClient`, and publishes each `Change` event to the `recent_change_stream` Kafka topic as JSON.
  </Card>

  <Card title="opensearch-wiki-indexer" icon="database" href="#opensearch-wiki-indexer-consumer--indexer">
    The **consumer + indexer** module. A scheduled Kafka consumer polls the `recent_change_stream` topic every 5 seconds and upserts each document into the `wiki-changes` OpenSearch index, using the page title as the document ID.
  </Card>

  <Card title="wiki-common" icon="cube" href="#wiki-common-shared-models">
    The **shared models** module. Defines the `Change` POJO (type, title, pageId, tags) and the `Query` wrapper that is serialized/deserialized across both the producer and consumer.
  </Card>
</CardGroup>

### wiki-change-stream (Producer)

`AppConfig` builds a Spring WebFlux `WebClient` pre-configured with the Wikipedia Recent Changes endpoint, requesting the last 100 changes (`rclimit=100`) with the `title`, `tags`, and `ids` properties. `WikipediaClient.getRecentChanges()` calls that endpoint and maps the response body to a `Query` object. `OpenStream.stream()` is annotated with `@Scheduled(fixedRate = 5000)` and iterates over every `Change` in the query result, forwarding each one to `KafkaPublish.publish(Change)`. `KafkaPublish` uses a raw `KafkaProducer<String, Change>` with Spring's `JsonSerializer` to write records to the `recent_change_stream` topic on `localhost:9092`.

### opensearch-wiki-indexer (Consumer + Indexer)

`KafkaConsume.consume()` creates a `KafkaConsumer<String, Change>` subscribed to `recent_change_stream` (consumer group `wiki-indexer`, offset strategy `earliest`) and polls for records on a 5-second schedule via `@Scheduled(fixedRate = 5000)`. For each consumed record it delegates to `OpensearchIndexer.index(Change)`, which calls `openSearchClient.index(...)` targeting the `wiki-changes` index and using `change.getTitle()` as the document ID — so repeated edits to the same page are upserted rather than duplicated. `OpensearchConfig` wires the `OpenSearchClient` bean pointing at `localhost:9200`.

### wiki-common (Shared Models)

Provides the `Change` class (Lombok `@Data`):

```java theme={null}
public class Change {
    private String type;        // e.g. "edit", "new"
    private String title;       // Wikipedia page title (used as OpenSearch document ID)
    @JsonProperty("pageid")
    private Long pageId;
    private List<String> tags;  // editor tags attached to the change
}
```

## Key Features

* **Scheduled Wikipedia polling** — `@Scheduled(fixedRate = 5000)` fetches up to 100 recent changes every 5 seconds from the Wikipedia API.
* **Kafka event streaming** — each `Change` is serialized as JSON by Spring's `JsonSerializer` and produced to the `recent_change_stream` topic, decoupling ingestion from indexing.
* **OpenSearch indexing with upsert semantics** — documents are written to the `wiki-changes` index with the page title as the document ID, so re-edits update the existing document instead of creating duplicates.
* **Automatic HTTP 429 back-off** — when Wikipedia returns a rate-limit response, the producer logs a warning and sleeps for 5 seconds before the next scheduled tick resumes.
* **Spring Boot 4 / Java 21** — built on Spring Boot 4.1.0 with Java 21 as the minimum runtime target, using WebFlux for non-blocking HTTP and Lombok for boilerplate elimination.

## Prerequisites

Before running OpenKnowledgeStream, ensure the following are available on your machine:

| Requirement      | Version / Details                                                          |
| ---------------- | -------------------------------------------------------------------------- |
| **Java JDK**     | 21 or later                                                                |
| **Apache Kafka** | Broker reachable at `localhost:9092`, topic `recent_change_stream` created |
| **OpenSearch**   | Node reachable at `localhost:9200`                                         |
| **Apache Maven** | 3.9+ (or use the Maven Wrapper included in the repo)                       |

<Note>
  Both Kafka and OpenSearch must be running **before** you start the application. The producer and consumer connect immediately on startup and will fail fast if either service is unreachable.
</Note>

## Next Steps

<Card title="Quickstart" icon="rocket" href="/quickstart">
  Follow the step-by-step guide to clone the repository, build the project, start the pipeline, and verify that Wikipedia changes are flowing into OpenSearch.
</Card>
