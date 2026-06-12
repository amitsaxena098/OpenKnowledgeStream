package com.as.OpenKnowledgeStream.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RecentChanges {
    @JsonProperty("recentchanges")
    List<Change> recentChanges;
}
