package ru.yandex.practicum.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.service.FilesService;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FilesService filesService;

    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    // POST эндпоинт для загрузки файла
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return filesService.upload(file);
    }

    // GET эндпоинт для скачивания файла
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "filename") String filename) {
        Resource file = filesService.download(filename);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(file);
    }
}