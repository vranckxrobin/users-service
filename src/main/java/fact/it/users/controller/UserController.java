package fact.it.users.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import fact.it.users.model.ImgBoardUser;
import fact.it.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void fillDB(){
        if(userRepository.count()==0){
            userRepository.save(new ImgBoardUser("r0703028@student.thomasmore.be",new BCryptPasswordEncoder().encode("test") ));
            userRepository.save(new ImgBoardUser("r0703029@student.thomasmore.be",new BCryptPasswordEncoder().encode("test") ));
        }
    }
    @GetMapping("/users")
    public List<ImgBoardUser> getAllUsers(){

        return userRepository.findAll();
    }
    @GetMapping("/user/{email}")
    public ImgBoardUser getUserByEmail(@PathVariable String email){

        return userRepository.findImgBoardUserByEmail(email);
    }

    @PostMapping(value = "/user")
    public ImgBoardUser createUser(@RequestBody ImgBoardUser user){
        user.setId(0);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }
    @PutMapping("/user")
    public ImgBoardUser updateUserPassword(@RequestBody ImgBoardUser updateUser){
        ImgBoardUser retrievedUser = userRepository.findImgBoardUserByEmail(updateUser.getEmail());
        String password = updateUser.getPassword();
        retrievedUser.setPassword(new BCryptPasswordEncoder().encode(password));

        userRepository.save(retrievedUser);
        return retrievedUser;
    }
    @DeleteMapping("/user/{email}")
    public ResponseEntity  deleteUser(@PathVariable String email){
        ImgBoardUser user = userRepository.findImgBoardUserByEmail(email);
        if(user!=null){
            userRepository.delete(user);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }



}
