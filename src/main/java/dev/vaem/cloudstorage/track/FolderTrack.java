package dev.vaem.cloudstorage.track;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("folder_tracks")
public class FolderTrack {
    
    @Id
    private UUID id;

    @Column("folder_id")
    private UUID folderId;

    private OffsetDateTime date;

}
