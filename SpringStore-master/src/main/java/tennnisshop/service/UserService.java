package tennnisshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import tennnisshop.entity.User;
import tennnisshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        users.sort(Comparator.comparing(User::getUsername));
        return users;
    }


    public void updateUserStatus(String username, boolean enabled) {
        Optional<User> userOpt = userRepository.findById(username);
        if (userOpt.isPresent()) {
            userRepository.updateStatus(username, enabled);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
