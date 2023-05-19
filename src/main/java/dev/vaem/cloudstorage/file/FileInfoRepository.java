package dev.vaem.cloudstorage.file;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface FileInfoRepository extends CrudRepository<FileInfo, UUID> {
    
}
