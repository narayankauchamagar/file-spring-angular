package np.com.thapanarayan.fp.controller;

import np.com.thapanarayan.fp.dto.FileResponse;
import np.com.thapanarayan.fp.services.FileStorageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin(maxAge = 3600)
@RestController
public class ImageUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/file")
    public ResponseEntity<?> uploadImages(@RequestParam("file") MultipartFile file) {
        String upfile = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download/")
                .path(upfile)
                .toUriString();

        return ResponseEntity.status(HttpStatus.OK).body(new FileResponse(upfile, fileDownloadUri,"File uploaded with success!"));
    }


    @GetMapping("/{fileName:.+}")
    public void download(@PathVariable("fileName") String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException  {
        Resource resource = fileStorageService.loadFileAsResource(fileName);
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }
        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        response.setHeader("Content-Length", Long.toString(resource.contentLength()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + resource.getFilename() +"\"");

        IOUtils.copy(resource.getInputStream(), response.getOutputStream());
        IOUtils.closeQuietly(resource.getInputStream());
        IOUtils.closeQuietly(response.getOutputStream());
    }

    @GetMapping("/api/download/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            System.out.print("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename() + "")
                .body(resource);
    }
}
