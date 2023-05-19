package dev.vaem.cloudstorage.file;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Table("files")
public class FileInfo {
    
    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("path")
    private String path;

    @Column("size")
    private long size;

    @Column("date_created")
    private OffsetDateTime dateCreated;

    @Column("date_updated")
    private OffsetDateTime dateUpdated;

}
