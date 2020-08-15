package br.com.cedran.fileuploaddownload.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Slf4j
public class FileUploadController {

    @PostMapping("/raw-in-chunks")
    public ResponseEntity<Void> rawInChunks(
            HttpServletRequest request
    ) {
        try (FileOutputStream outputStream = new FileOutputStream("tmp/"+ UUID.randomUUID().toString())) {

            String boundary = request.getContentType().substring(request.getContentType().indexOf("boundary=") + "boundary=".length());
            MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(), 50, null);

            boolean nextPart = multipartStream.skipPreamble();
            while (nextPart) {
                String header = multipartStream.readHeaders();
                if (header.contains("filename")) {
                    multipartStream.readBodyData(outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
                nextPart = multipartStream.readBoundary();
            }
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("{}", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/spring-multipart-complete")
    public ResponseEntity<Void> springMultipart(
            @RequestParam MultipartFile file
    ) {
        try (FileOutputStream outputStream = new FileOutputStream("tmp/"+ UUID.randomUUID().toString())) {
            outputStream.write(file.getBytes());
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("{}", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/spring-multipart-in-chunks")
    public ResponseEntity<Void> springMultipartInChunks(
            @RequestParam MultipartFile file
    ) {
        try (FileOutputStream outputStream = new FileOutputStream("tmp/"+ UUID.randomUUID().toString())) {
            int read;
            byte[] bytes = new byte[50];
            InputStream inputStream = file.getInputStream();

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            log.error("{}", exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
