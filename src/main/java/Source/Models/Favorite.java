package Source.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "favorite")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int favoriteId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movieId")
    private Movie movie;
}
