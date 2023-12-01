package Source.Services;

import Source.Models.Actor;
import Source.Repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void delete(Actor actor){
        actorRepository.delete(actor);
    }
}
