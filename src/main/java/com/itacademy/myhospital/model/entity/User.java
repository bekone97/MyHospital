package com.itacademy.myhospital.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    @NotBlank(message = "username is required field")
    @Size(min=4,message = "name must be min 6 symbols")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "password is required field")
    @Size(min=4,message = "password must be min 6 symbols")
    private String password;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "verification_status")
    private Boolean verificationStatus;

    @Column(name="authentication_status")
    private Boolean authenticationStatus;

    @Column(name = "email")
    private String email;

    @Column(name="img")
    private String img;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @OneToOne(mappedBy = "user",
    cascade = CascadeType.ALL)
    private Person person;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", authenticationStatus=" + authenticationStatus +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password);
    }

    @Transient
    public String getImagePath(){
        if(img==null||id==null) return null;

        return "/users-images/"+id+"/"+img;
    }
}
