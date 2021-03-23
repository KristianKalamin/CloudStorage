package com.kalamin.CloudStorage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "file")
@JsonIgnoreProperties("folder")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private long id;

    @Basic
    @Column(name = "size")
    private long size;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "path")
    private String path;

    @Basic
    @Column(name = "time_of_creation")
    private Timestamp timeOfCreation;

    @Basic
    @Column(name = "version")
    private int version;

    @Basic
    @Column(name = "time_of_deletion")
    private Timestamp timeOfDeletion;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    public File(Long id) {
        this.id = id;
    }

    public File(String path) {
        this.path = path;
    }

    public File(long size, String name, String path, Timestamp timeOfCreation, int version, @Nullable Timestamp timeOfDeletion, Folder folder) {
        this.size = size;
        this.folder = folder;
        this.name = name;
        this.path = path;
        this.timeOfCreation = timeOfCreation;
        this.version = version;
        this.timeOfDeletion = timeOfDeletion;
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                "size=" + size +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return size == file.size && version == file.version && name.equals(file.name) && path.equals(file.path) && timeOfCreation.equals(file.timeOfCreation) && Objects.equals(timeOfDeletion, file.timeOfDeletion) && folder.equals(file.folder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, name, path, timeOfCreation, version, timeOfDeletion, folder);
    }
}
