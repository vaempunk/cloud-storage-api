package dev.vaem.cloudstorage.domain.track;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackController {

    @Autowired
    private TrackService trackService;

    @GetMapping(path = "/tracks/{trackId}")
    public Track getTrack(@PathVariable("trackId") String trackId) {
        return trackService.getFolderTrack(trackId);
    }

    @GetMapping(path = "/folders/{folderId}/tracks")
    public Page<Track> getAllByFolderId(@PathVariable("folderId") String folderId, @RequestParam(name = "page", defaultValue = "0") int page) {
        return trackService.getAllByFolderId(folderId, page);
        
    }

    @PostMapping(path = "/folders/{folderId}/track")
    public Track trackFolder(@PathVariable("folderId") String folderId) throws IOException {
        return trackService.trackFolder(folderId);
    }

    @PostMapping(path = "/tracks/{trackId}/restore")
    public void restoreTrack(@PathVariable("trackId") String trackId) throws IOException {
        trackService.restoreTrack(trackId);
    }

    @DeleteMapping(path = "/tracks/{trackId}")
    public void deleteTrack(@PathVariable("trackId") String trackId) throws IOException {
        trackService.deleteTrack(trackId);
    }

}
