package dev.vaem.cloudstorage.domain.folder;

import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.CrudRepository;

// Репозиторий для взаимодействия с коллекцией "folders"
public interface FolderInfoRepository extends CrudRepository<FolderInfo, String> {

    // Автогенерация определения метода на основе его сигнатуры
    Page<FolderInfo> findAllByParentId(String parentId, Pageable pageable);

    // Запрос на чтение с фильтрацией
    @Query("{ path: { $regex: '^?0' } }")
    Stream<FolderInfo> streamByPathStartingWith(String path);

    // Запрос на обновление с фильтрацией
    @Query("{ path: { $regex: '^?0' } }")
    @Update("{ $addToSet: { tracks: ?1 } }")
    void addTrackByPathStartingWith(String path, String track);

    @Query("""
            {
                $and: [
                    {path: {$regex: '^?0'}},
                    {tracks: {$in: [?1]}}
                ]
            }
            """)
    Stream<FolderInfo> streamByPathStartingWithAndTracksContaining(String path, String track);

    // запрос на удаление с фильтрацией
    @Query(value = """
            {
                $and: [
                    {path: {$regex: '^?0'}},
                    {tracks: {$size: 0}}
                ]
            }""", delete = true)
    void deleteByPathStartingWithAndTracksIsEmpty(String path);

}
