package WikiChangeStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan({"com.as", "WikiIndexer", "WikiIndexer.models", "Wikicommon", "WikiChangeStream"})
public class OpenKnowledgeStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenKnowledgeStreamApplication.class, args);
	}

}
