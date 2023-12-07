package Source.Services;

import Source.Models.User;
import Source.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String bcryptPassword = bcrypt.encode(user.getPassword());
        user.setPassword(bcryptPassword);
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

            userRepository.save(u);
        }
    }

    public void delete(User user){
        userRepository.delete(user);
    }

    public boolean authenticateUser(User user){
        User u = userRepository.findByEmail(user.getEmail());
        if(u != null){
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            return bcrypt.matches(user.getPassword(), u.getPassword());
        }
        return false;
    }

    public Page<User> pageableUsers(List<User> users, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        return new PageImpl<>(users.subList(start, end), pageable, users.size());
    }
}
