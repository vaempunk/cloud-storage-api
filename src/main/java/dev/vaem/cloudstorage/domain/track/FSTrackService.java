package dev.vaem.cloudstorage.domain.track;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.server.ResponseStatusException;

import dev.vaem.cloudstorage.config.AppProperties;
import dev.vaem.cloudstorage.domain.user.UserService;
import net.lingala.zip4j.ZipFile;

@Service
class FSTrackService {

    @Autowired
    private UserService userService;

    private Path basePath;
    private Path historyPath;

    public FSTrackService(AppProperties applicationProperties) {
        this.basePath = Path.of(applicationProperties.getBasePath());
        this.historyPath = Path.of(applicationProperties.getHistoryPath());
    }

    private Path makeUserBasePath() {
        return basePath.resolve(userService.userAccount().getId());
    }

    private Path makeUserHistoryPath() {
        return historyPath.resolve(userService.userAccount().getId());
    }

    public void createZipTrack(Path folderPath, String trackId) throws IOException {
        var userHistoryPath = makeUserHistoryPath();
        var userBasePath = makeUserBasePath();
        Files.createDirectories(userHistoryPath.resolve(folderPath));
        var zipPath = Path.of(folderPath.resolve(trackId.toString()) + ".zip");
        var zipAbsPath = userHistoryPath.resolve(zipPath);

        try (var zipFile = new ZipFile(zipAbsPath.toString())) {
            zipFile.addFolder(userBasePath.resolve(folderPath).toFile());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    public void restoreZipTrack(Path folderPath, String trackId) throws IOException {
        var userHistoryPath = makeUserHistoryPath();
        var userBasePath = makeUserBasePath();
        var zipPath = Path.of(folderPath.resolve(trackId.toString()) + ".zip");
        var zipAbsPath = userHistoryPath.resolve(zipPath);

        FileSystemUtils.deleteRecursively(userBasePath.resolve(folderPath));

        try (var zipFile = new ZipFile(zipAbsPath.toString())) {
            zipFile.extractAll(
                    userBasePath.resolve(folderPath.getParent() == null ? Path.of("") : folderPath).toString());
        }
    }

    public void deleteZipTrack(Path folderPath, String trackId) {
        var userHistoryPath = makeUserHistoryPath();
        var zipPath = Path.of(folderPath.resolve(trackId.toString()) + ".zip");
        var zipAbsPath = userHistoryPath.resolve(zipPath);

        try {
            Files.delete(zipAbsPath);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

}

// @Deprecated
// public long deleteChunk(Path filePath, long offset, Optional<Long> opLength)
// throws IOException {
// var absPath = basePath.resolve(filePath);
// var oldSize = Files.size(absPath);

// if (offset == 0 && opLength.isEmpty()) {
// Files.delete(absPath);
// return -oldSize;
// }
// var length = opLength.isPresent() ? opLength.get()
// : Files.size(absPath) -
// offset;

// try (var fileChan = Files.newByteChannel(absPath, StandardOpenOption.WRITE,
// StandardOpenOption.READ)) {
// var buffer = ByteBuffer.allocate(1024 * 1024);
// var remaining = fileChan.size() - offset - length;
// var donePosition = offset;
// while (remaining > 0) {
// buffer.clear();
// if (remaining < 1024 * 1024) {
// buffer.limit((int) remaining);
// }
// fileChan.position(donePosition + length);
// fileChan.read(buffer);
// fileChan.position(donePosition);
// buffer.position(0);
// fileChan.write(buffer);

// donePosition += 1024 * 1024;
// remaining -= 1024 * 1024;
// }
// fileChan.truncate(fileChan.size() - length);
// }

// var newSize = Files.size(absPath);
// return oldSize - newSize;
// }
