package dev.vaem.cloudstorage.domain.folder;

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
import net.lingala.zip4j.exception.ZipException;

@Service
class FSFolderService {

    @Autowired
    private UserService userService;

    private Path basePath;

    public FSFolderService(AppProperties applicationProperties) {
        this.basePath = Path.of(applicationProperties.getBasePath());
    }

    private Path makeUserBasePath() {
        return basePath.resolve(userService.userAccount().getId());
    }

    public void moveFolder(Path folderPath, Path newPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(folderPath);
        var absNewPath = userBasePath.resolve(newPath);
        Files.move(absPath, absNewPath);
    }

    public void copyFolder(Path folderPath, Path newPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(folderPath);
        var absNewPath = userBasePath.resolve(newPath);
        FileSystemUtils.copyRecursively(absPath, absNewPath);
    }

    public void deleteFolder(Path folderPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var absPath = userBasePath.resolve(folderPath);
        FileSystemUtils.deleteRecursively(absPath);
    }

    public void zipFolder(Path folderPath) throws IOException {
        var userBasePath = makeUserBasePath();
        var zipPath = Path.of(folderPath.toString() + ".zip");
        var zipAbsPath = userBasePath.resolve(zipPath);

        try (var zipFile = new ZipFile(zipAbsPath.toString())) {
            zipFile.addFolder(userBasePath.resolve(folderPath).toFile());
        } catch (ZipException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

}
