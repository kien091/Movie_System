package Source.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "watchHistory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int watchId;

    @Column(columnDefinition = "DATETIME default CURRENT_TIMESTAMP")
    private Date timeWatch;

    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "movieId")
    private Movie movie;
}
