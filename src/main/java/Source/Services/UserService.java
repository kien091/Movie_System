package Source.Services;

import Source.Models.User;
import Source.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(int id){
        return userRepository.findById(id).orElse(null);
    }

    public void update(User user){
        User u = userRepository.findById(user.getUserId()).orElse(null);
        if(u != null){
            u.setUsername(user.getUsername());
            u.setPassword(user.getPassword());
            u.setAvatar(user.getAvatar());
            u.setEmail(user.getEmail());
            u.setFirstName(user.getFirstName());
            u.setLastName(user.getLastName());
            u.setDateOfBirth(user.getDateOfBirth());
            u.setGender(user.getGender());
            u.setStatus(user.isStatus());

            u.setWatchHistories(user.getWatchHistories());
            u.setReviews(user.getReviews());
            u.setFavorites(user.getFavorites());

            userRepository.save(u);
        }
    }

    public void delete(User user){
        userRepository.delete(user);
    }
}
