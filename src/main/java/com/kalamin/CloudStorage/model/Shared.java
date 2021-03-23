package com.kalamin.CloudStorage.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shared")
@NoArgsConstructor
@EqualsAndHashCode
public class Shared {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shared_id")
    private long id;

    @Basic
    @Column(name = "file_path")
    private String filePath;

    @Basic
    @Column(name = "shared_uri")
    private String sharedUri;

    @ManyToMany(mappedBy = "shareds")
    private List<User> users = new ArrayList<>();

    public Shared(String filePath) {
        this.filePath = filePath;
    }

    public Shared(long id) {
        this.id = id;
    }

    public Shared(String filePath, String sharedUri, List<User> users) {
        this.filePath = filePath;
        this.sharedUri = sharedUri;
        this.users = users;
    }
}
