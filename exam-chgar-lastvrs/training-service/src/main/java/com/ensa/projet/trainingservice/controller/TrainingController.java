package com.ensa.projet.trainingservice.controller;

import com.ensa.projet.trainingservice.model.dao.ModuleDTO;
import com.ensa.projet.trainingservice.model.dao.TrainingDTO;
import com.ensa.projet.trainingservice.service.interfaces.TrainingService;
import com.ensa.projet.trainingservice.service.FileStorageService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private final FileStorageService fileStorageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public TrainingController(TrainingService trainingService, FileStorageService fileStorageService) {
        this.trainingService = trainingService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainingDTO> createTraining(
            @RequestPart("training") TrainingDTO trainingDTO,
            @RequestPart(value = "icon", required = false) MultipartFile icon) {

        if (icon != null && !icon.isEmpty()) {
            try {
                // Appel du service pour stocker le fichier
                String iconPath = fileStorageService.storeFile(icon);

                // Assigner le chemin du fichier stocké dans le DTO
                trainingDTO.setIconPath(iconPath);
            } catch (RuntimeException ex) {
                // Gestion des erreurs de stockage
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            // Création de la formation avec le DTO
            TrainingDTO created = trainingService.createTraining(trainingDTO);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception ex) {
            // Gestion des erreurs lors de la création de la formation
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainingDTO> updateTraining(
            @PathVariable Integer id,
            @RequestPart("training") TrainingDTO trainingDTO,
            @RequestPart(value = "icon", required = false) MultipartFile icon) {

        trainingDTO.setId(id);

        if (icon != null && !icon.isEmpty()) {
            String iconPath = fileStorageService.storeFile(icon);
            trainingDTO.setIconPath(iconPath);
        }

        TrainingDTO updated = trainingService.updateTraining(id, trainingDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Integer id) {
        trainingService.deleteTraining(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingDTO> getTraining(@PathVariable Integer id) {
        TrainingDTO training = trainingService.getTrainingById(id);
        return ResponseEntity.ok(training);
    }

    @GetMapping
    public ResponseEntity<List<TrainingDTO>> getAllTrainings() {
        List<TrainingDTO> trainings = trainingService.getAllTrainings();
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TrainingDTO>> getTrainingsByCategory(@PathVariable Integer categoryId) {
        List<TrainingDTO> trainings = trainingService.getTrainingsByCategory(categoryId);
        return ResponseEntity.ok(trainings);
    }

    @GetMapping("/{id}/icon")
    public ResponseEntity<Resource> getTrainingIcon(@PathVariable Integer id) {
        try {
            TrainingDTO training = trainingService.getTrainingById(id);
            if (training.getIconPath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path iconPath = Paths.get(uploadDir, training.getIconPath());
            Resource resource = new UrlResource(iconPath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType;
            if (training.getIconPath().toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (training.getIconPath().toLowerCase().endsWith(".jpg")
                    || training.getIconPath().toLowerCase().endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/modules")
    public ResponseEntity<ModuleDTO> addModule(@PathVariable Integer id, @RequestBody ModuleDTO moduleDTO) {
        ModuleDTO added = trainingService.addModule(id, moduleDTO);
        return new ResponseEntity<>(added, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishTraining(@PathVariable Integer id) {
        trainingService.publishTraining(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<Void> unpublishTraining(@PathVariable Integer id) {
        trainingService.unpublishTraining(id);
        return ResponseEntity.ok().build();
    }
}