package Source.Services;

import Source.Models.Episode;
import Source.Repositories.EpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EpisodeService {
    private final EpisodeRepository episodeRepository;

    @Autowired
    public EpisodeService(EpisodeRepository episodeRepository){
        this.episodeRepository = episodeRepository;
    }

    public Episode save(Episode episode){
        return episodeRepository.save(episode);
    }

    public List<Episode> findAll(){
        return episodeRepository.findAll();
    }

    public Episode findById(int id){
        return episodeRepository.findById(id).orElse(null);
    }

    public void update(Episode episode){
        Episode e = episodeRepository.findById(episode.getEpisodeId())
                .orElse(null);
        if(e != null){
            e.setEpisodeName(episode.getEpisodeName());
            e.setEpisodeFilm(episode.getEpisodeFilm());
            e.setDuration(episode.getDuration());
            e.setView(episode.getView());
            e.setMovie(episode.getMovie());

            episodeRepository.save(e);
        }
    }

    public void delete(Episode episode){
        episodeRepository.delete(episode);
    }

    public Page<Episode> pageableEpisodes(List<Episode> episodes, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), episodes.size());
        return new PageImpl<>(episodes.subList(start, end), pageable, episodes.size());
    }
}
