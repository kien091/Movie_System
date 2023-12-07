package Source.Services;

import Source.Models.Actor;
import Source.Repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActorService {
    private final ActorRepository actorRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository){
        this.actorRepository = actorRepository;
    }

    public Actor save(Actor actor){
        return actorRepository.save(actor);
    }

    public List<Actor> findAll(){
        return actorRepository.findAll();
    }

    public Actor findById(int id){
        return actorRepository.findById(id).orElse(null);
    }

    public void update(Actor actor){
        Actor a = actorRepository.findById(actor.getActorId()).orElse(null);
        if(a != null){
            a.setName(actor.getName());
            a.setBiography(actor.getBiography());
            a.setMovies(actor.getMovies());

            actorRepository.save(a);
        }
    }

    @Transactional
    public void delete(Actor actor){
        actorRepository.deleteActionMovie(actor.getActorId());
        actorRepository.delete(actor);
    }

    public void insertActionMovie(int actorId, int movieId){
        actorRepository.insertActionMovie(actorId, movieId);
    }

    @Transactional
    public void deleteActionMovie(int actorId, int movieId){
        actorRepository.deleteActionMovie(actorId, movieId);
    }

    public Page<Actor> pageableActors(List<Actor> actors, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), actors.size());
        return new PageImpl<>(actors.subList(start, end), pageable, actors.size());
    }
}
