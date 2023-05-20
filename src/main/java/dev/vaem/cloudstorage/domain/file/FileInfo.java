package dev.vaem.cloudstorage.domain.file;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document("files")
public class FileInfo {
    
    @Id
    private String id;

    private String name;

    private String path;

    private String folderId;

    private boolean isActive;

    @Builder.Default
    Set<String> tracks = new HashSet<>();

    private Instant dateCreated;

    private Instant dateUpdated;

}
