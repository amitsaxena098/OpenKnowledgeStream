package Wikicommon.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Change {

    private String type;
    private String title;
    @JsonProperty("pageid")
    private Long pageId;
    private List<String> tags;
}
