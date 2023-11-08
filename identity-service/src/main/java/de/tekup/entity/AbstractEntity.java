package de.tekup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
@Data
public abstract class AbstractEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String createdAt;
    
    @Column(name = "updated_at")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String updatedAt;
    
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        createdAt = now.format(formatter);
        updatedAt = now.format(formatter);
    }
    
    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        updatedAt = now.format(formatter);
    }
}
