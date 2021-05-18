package com.kalamin.CloudStorage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
@JsonIgnoreProperties("shareds")
public class User {

    @Id
    @Column(name = "user_id")
    private String id;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "email")
    private String email;

    @ManyToMany()
    @JoinTable(name = "sharing",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "shared_id", referencedColumnName = "shared_id"))
    private List<Shared> shareds = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Drive drive;

    public User(String id) {
        this.id = id;
    }

    public User(String userId, String name, String email) {
        this.email = email;
        this.name = name;
        this.id = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
