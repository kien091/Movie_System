package Source.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "movie")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movieId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String genre;
    private int totalEpisode;
    private String director;
    private String poster;
    private String trailer;
    private String nation;

    private String releaseDate;

    @Column(columnDefinition = "double default 0")
    private double rating;

    @Column(columnDefinition = "bigint default 0")
    private int totalView;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchHistory> watchHistories;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes;

    @ManyToMany
    @JoinTable(name = "actionMovie",
            joinColumns = @JoinColumn(name = "movieId"),
            inverseJoinColumns = @JoinColumn(name = "actorId"))
    private List<Actor> actors;

    public Movie(String title, String description, String genre,
                 int totalEpisode, String director, String poster,
                 String trailer, String nation, String releaseDate,
                 double rating, int totalView) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.totalEpisode = totalEpisode;
        this.director = director;
        this.poster = poster;
        this.trailer = trailer;
        this.nation = nation;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.totalView = totalView;
    }
}
