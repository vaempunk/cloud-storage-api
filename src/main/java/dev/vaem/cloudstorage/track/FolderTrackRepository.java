package dev.vaem.cloudstorage.track;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface FolderTrackRepository extends CrudRepository<FolderTrack, UUID> {
    
}
