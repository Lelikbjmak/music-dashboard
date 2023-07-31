package com.innowise.soundfilemicroservice.domain;

import com.innowise.soundfilemicroservice.domain.domainenum.StorageTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "uploaded_audio_files")
public class UploadedAudioFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private StorageTypeEnum storage;

    @Column(nullable = false)
    private String path;

    @CreationTimestamp
    private LocalDateTime uploadedDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
