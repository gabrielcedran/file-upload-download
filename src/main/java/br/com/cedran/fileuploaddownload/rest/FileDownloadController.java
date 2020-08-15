package br.com.cedran.fileuploaddownload.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

@RestController
@RequestMapping("/download")
@Slf4j
public class FileDownloadController {

    @GetMapping("/raw-in-chunks-1")
    public void rawInChunks1(HttpServletResponse response) throws IOException {

        URL url = new URL("https://speed.hetzner.de/100MB.bin");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"1GB.bin\"");
        response.setHeader("Content-Length", url.openConnection().getHeaderField("Content-Length"));

        try (OutputStream outputStream = response.getOutputStream()){
            try (InputStream inputStream = url.openStream()) {
                inputStream.transferTo(outputStream);
            }
        }
    }

    @GetMapping("/raw-in-chunks-2")
    public void rawInChunks2(HttpServletResponse response) throws IOException {

        URL url = new URL("https://speed.hetzner.de/100MB.bin");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"1GB.bin\"");
        response.setHeader("Content-Length", url.openConnection().getHeaderField("Content-Length"));


        try (OutputStream outputStream = response.getOutputStream()) {

            try (InputStream inputStream = url.openStream()) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, read);
                }
            }
        }
    }

    @GetMapping("/spring-in-chunks")
    public StreamingResponseBody springInChunks(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=\"1GB.bin\"");


            return outputStream -> {
                try (InputStream inputStream = new URL("https://speed.hetzner.de/100MB.bin").openStream()) {
                    int read;
                    byte[] bytes = new byte[1024];
                    while( (read = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, read);
                    }
                }
                outputStream.close();
            };
    }


    @GetMapping("/complete")
    public ResponseEntity<byte[]> complete() throws IOException {
        // https://speed.hetzner.de/100MB.bin
        // https://speed.hetzner.de/1GB.bin

        try (InputStream inputStream = new URL("https://speed.hetzner.de/100MB.bin").openStream()) {

            int read;
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while (( read = inputStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0, read);
            }
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    //.contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(byteArrayOutputStream.toByteArray());
        }
    }
}
