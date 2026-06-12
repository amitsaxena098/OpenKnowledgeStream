package com.as.OpenKnowledgeStream.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Change {

    private String type;
    private String title;
    @JsonProperty("pageid")
    private Long pageId;
}
