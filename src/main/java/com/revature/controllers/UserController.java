package com.revature.controllers;

import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.services.PostService;
import com.revature.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://flutterdeployedbucket.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Testing Method: Adds a User
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(this.userService.save(user));
    }

    // Testing Method: Get All Users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {return ResponseEntity.ok(this.userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userOptional.get());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        Optional<User> userOptional = userService.findByUsername(username);
        if(!userOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userOptional.get());
    }
//    @GetMapping("/user")
//    public ResponseEntity<User> findByUsername(@RequestBody String username) {
//        Optional<User> userOptional = userService.findByUsername(username);
//        if(!userOptional.isPresent()){
//            return ResponseEntity.badRequest().build();
//        }
//        return ResponseEntity.ok(userOptional.get());
//    }

    @PutMapping("/{id}/password")
    public ResponseEntity<User> editPassword(@PathVariable int id, @RequestBody String editString) {
        Optional<User> userOptional = userService.findById(id);
        if(!userOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        User newUser = userOptional.get();
        newUser.setPassword(editString);

        return ResponseEntity.ok(this.userService.save(newUser));
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<User> editEmail(@PathVariable int id, @RequestBody String editString) {
        Optional<User> userExist = userService.findByEmail(editString);

        if(userExist.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOptional = userService.findById(id);
        if(!userOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        User newUser = userOptional.get();
        newUser.setEmail(editString);

        return ResponseEntity.ok(this.userService.save(newUser));
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<User> editUsername(@PathVariable int id, @RequestBody String editString) {
        Optional<User> userExist = userService.findByUsername(editString);

        if(userExist.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        if(!(this.userService.findByUsername(editString)).isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOptional = userService.findById(id);
        if(!userOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        User newUser = userOptional.get();
        newUser.setUsername(editString);


        return ResponseEntity.ok(this.userService.save(newUser));
    }

    @PutMapping("/{id}/profileImage")
    @Transactional
    public ResponseEntity<User> updateImageUrl(@PathVariable int id, @RequestBody String imageUrl) {
        Optional<User> userOptional = userService.findById(id);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        User newUser = userOptional.get();
        newUser.setImageUrl(imageUrl);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping("/{id}/follow")
    public ResponseEntity<List<User>> addFollower(@PathVariable int id, @RequestBody int followerId) {
        Optional<User> followedUserOpt = userService.findById(id);
        if (!followedUserOpt.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Optional<User> followerOpt = userService.findById(followerId);
        if (!followerOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userService.addFollower(followedUserOpt.get(), followerOpt.get()));
    }

    @PutMapping("/{id}/unfollow")
    public ResponseEntity<List<User>> removeFollower(@PathVariable int id, @RequestBody int followerId) {
        Optional<User> followedUserOpt = userService.findById(id);
        if (!followedUserOpt.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Optional<User> followerOpt = userService.findById(followerId);
        if (!followerOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userService.removeFollower(followedUserOpt.get(), followerOpt.get()));
    }
    @GetMapping("/{id}/feed")
    public ResponseEntity<List<Post>> getFeedForUser(@PathVariable int id){
        Optional<User> optionalUser = userService.findById(id);
        if(!optionalUser.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        List<Post> feed = userService.getFeedForUser(optionalUser.get());
        if(feed == null) {
           return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<Post>> getAllPostsByAUser(@PathVariable int id) {
        Optional<User> optionalUser = userService.findById(id);
        if(!optionalUser.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Optional<List<Post>> postList = userService.getAllPostsByAUser(optionalUser.get());
        if (!postList.isPresent()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(postList.get());
    }
}
