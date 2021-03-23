package com.kalamin.CloudStorage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"subFolder", "drive"})
@Table(name = "folder")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private long id;

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
    @Column(name = "time_of_deletion")
    private Timestamp timeOfDeletion;

    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<File> files = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Folder parentFolder;

    @OneToMany(mappedBy = "parentFolder")
    private List<Folder> subFolders = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "drive_id")
    private Drive drive;

    public Folder(Folder f) {
        this.id = f.getId();
        this.name = f.getName();
        this.path = f.getPath();
        this.parentFolder = f.getParentFolder();
        this.drive = f.getDrive();
        this.subFolders = f.getSubFolders();
        this.files = f.getFiles();
        this.timeOfCreation = f.getTimeOfCreation();
        this.timeOfDeletion = f.getTimeOfDeletion();
    }

    public void addSubFolder(Folder subFolder) {
        this.subFolders.add(subFolder);
        subFolder.setParentFolder(this);
    }

    public Folder(String name, String path, Timestamp timeOfCreation, @Nullable Timestamp timeOfDeletion, Drive drive) {
        this.name = name;
        this.drive = drive;
        this.path = path;
        this.timeOfCreation = timeOfCreation;
        this.timeOfDeletion = timeOfDeletion;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Folder folder = (Folder) o;
        return name.equals(folder.name) && path.equals(folder.path) && timeOfCreation.equals(folder.timeOfCreation) && Objects.equals(timeOfDeletion, folder.timeOfDeletion) && Objects.equals(parentFolder, folder.parentFolder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, timeOfCreation, timeOfDeletion, parentFolder);
    }
}
