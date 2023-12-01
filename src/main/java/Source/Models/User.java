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

    private String username;
    private String password;
    private String avatar;
    private String email;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String role;

    @Column(columnDefinition = "boolean default false")
    private boolean status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchHistory> watchHistories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;
}
