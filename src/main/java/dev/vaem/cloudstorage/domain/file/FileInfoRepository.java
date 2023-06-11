package dev.vaem.cloudstorage.domain.file;

import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.CrudRepository;

public interface FileInfoRepository extends CrudRepository<FileInfo, String> {

    Page<FileInfo> findAllByFolderId(String folderId, Pageable pageable);

    @Query("{ path: { $regex: '^?0' } }")
    Stream<FileInfo> streamByPathStartingWith(String path);

    @Query("{ path: {$regex: '^?0' } }")
    @Update("{ $addToSet: { tracks: ?1} }")
    void addTrackByPathStartingWith(String path, String track);

    @Query("""
            {
                $and: [
                    {path: {$regex: '^?0'}},
                    {tracks: {$in: [?1]}}
                ]
            }
            """)
    Stream<FileInfo> streamByPathStartingWithAndTracksContaining(String path, String track);

    @Query(value = """
            {
                $and: [
                    {path: {$regex: '^?0'}},
                    {tracks: {$size: 0}}
                ]
            }""", delete = true)
    void deleteByPathStartingWithAndTracksIsEmpty(String path);

}
