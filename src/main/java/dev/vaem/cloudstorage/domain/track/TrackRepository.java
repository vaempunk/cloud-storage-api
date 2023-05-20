package dev.vaem.cloudstorage.domain.track;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<Track, String> {
    
    Page<Track> findAllByFolderId(String folderId, Pageable pageable);

}
