package tennnisshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tennnisshop.entity.User;
import tennnisshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService() {
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByUsername(String username) {
        if (userRepository.findById(username).isPresent()) {
            return userRepository.findById(username).get();
        }
        return null;
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
}
