package dev.vaem.cloudstorage.folder;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface FolderInfoRepository extends CrudRepository<FolderInfo, UUID> {

}
