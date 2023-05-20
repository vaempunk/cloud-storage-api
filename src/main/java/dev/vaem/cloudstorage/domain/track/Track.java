package dev.vaem.cloudstorage.domain.track;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("tracks")
public class Track {
    
    @Id
    private String id;

    private String folderId;

    private String folderPath;

    private Instant date;

}
