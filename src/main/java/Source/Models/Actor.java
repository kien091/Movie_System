package Source.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "actor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int actorId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;
}
