package Source.Services;

import Source.Models.Favorite;
import Source.Repositories.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public FavoriteService(FavoriteRepository favoriteRepository){
        this.favoriteRepository = favoriteRepository;
    }

    public Favorite save(Favorite favorite){
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> findAll(){
        return favoriteRepository.findAll();
    }

    public Favorite findById(int id){
        return favoriteRepository.findById(id).orElse(null);
    }

    public void update(Favorite favorite){
        Favorite f = favoriteRepository.findById(favorite.getFavoriteId())
                .orElse(null);
        if(f != null){
            f.setUser(favorite.getUser());
            f.setMovie(favorite.getMovie());

            favoriteRepository.save(f);
        }
    }

    public void delete(Favorite favorite){
        favoriteRepository.delete(favorite);
    }
}
