package dev.vaem.cloudstorage.folder;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("folders")
public class FolderInfo {
    
    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("path")
    private String path;

    @Column("date_created")
    private OffsetDateTime createDate;

}
