package dev.vaem.cloudstorage.domain.folder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("folders")
public class FolderInfo {
    
    @Id
    private String id;

    private String name;

    private String path;

    private String parentId;

    private boolean isActive;

    @Builder.Default
    private Set<String> tracks = new HashSet<>();

    private Instant dateCreated;

}
