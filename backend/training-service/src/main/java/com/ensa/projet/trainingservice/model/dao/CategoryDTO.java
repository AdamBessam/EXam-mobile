package com.ensa.projet.trainingservice.model.dao;

import jakarta.persistence.Transient;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    private String name;
    private String description;
    private String iconPath;
    private List<TrainingDTO> trainings;

    @Transient  // Cette annotation indique que ce champ ne doit pas être sérialisé
    private MultipartFile icon;
}