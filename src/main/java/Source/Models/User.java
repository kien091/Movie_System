package Source.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(columnDefinition = "varchar(255) default 'user'")
    private String username;
    private String password;

    @Column(columnDefinition = "varchar(255) default '/img/user.png'")
    private String avatar;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;

    @Column(columnDefinition = "varchar(255) default 'user'")
    private String role;

    @Column(columnDefinition = "boolean default false")
    private boolean status;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public User(String username, String password, String avatar,
                String email, String role, boolean status){
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchHistory> watchHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;
}
