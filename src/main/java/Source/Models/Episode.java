package Source.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "episode")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int episodeId;

    private String episodeName;
    private String episodeFilm;
    private int duration;

    @Column(columnDefinition = "bigint default 0")
    private int view;

    @ManyToOne
    @JoinColumn(name = "movieId")
    private Movie movie;
}
