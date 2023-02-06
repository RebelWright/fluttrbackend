package com.revature.controllers;

import java.util.List;
import java.util.Optional;

import com.revature.models.PostType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.revature.annotations.Authorized;
import com.revature.models.Post;
import com.revature.services.PostService;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://flutterdeployedbucket.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class PostController {

	private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }
    

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
    	return ResponseEntity.ok(this.postService.getAll());
    }
    
    @Authorized
    @PutMapping
    public ResponseEntity<Post> upsertPost(@RequestBody Post post) {
    	return ResponseEntity.ok(this.postService.upsert(post));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getAllTopPosts() {
        return ResponseEntity.ok(this.postService.getAllTop());
    }

    // Testing Method: Create/Add A Post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(this.postService.upsert(post));
    }

    // Testing Method: Get a Post By Id
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable int id) {
        Optional<Post> postOptional = postService.findById(id);

        if (!postOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(postOptional.get());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.ok("this post was deleted");
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<Post> addPostLikes(@PathVariable int id) {
        Optional<Post> postOptional = postService.findById(id);
        if(!postOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Post post = postOptional.get();
        post.setLikes(post.getLikes() + 1);
        return ResponseEntity.ok(postService.upsert(post));
    }

    @PutMapping("/{id}/unlike")
    public ResponseEntity<Post> removePostLikes(@PathVariable int id) {
        Optional<Post> postOptional = postService.findById(id);
        if(!postOptional.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Post post = postOptional.get();
        post.setLikes(post.getLikes() - 1);
        return ResponseEntity.ok(postService.upsert(post));
    }
    @PutMapping("editPost/{id}")
    public ResponseEntity<Post> editPost(@PathVariable int id, @RequestBody String editString) {
        Optional<Post> postOptional = postService.findById(id);
        if(!postOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Post newPost = postOptional.get();
        newPost.setText(editString);

        return ResponseEntity.ok(this.postService.upsert(newPost));
    }

    @PutMapping("editPost/{id}/image")
    public ResponseEntity<Post> editPostByUrl(@PathVariable int id, @RequestBody String editString) {
        Optional<Post> postOptional = postService.findById(id);
        if(!postOptional.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Post newPost = postOptional.get();
        newPost.setImageUrl(editString);

        return ResponseEntity.ok(this.postService.upsert(newPost));
    }

    @PostMapping("{id}/comment")
    public ResponseEntity createComment(@PathVariable int id, @RequestBody Post comment) {
        Optional<Post> parentPostOpt = postService.findById(id);
        if (!parentPostOpt.isPresent()) {
            return ResponseEntity.badRequest().body("The parent post could not be found, please enter a valid parent post ID.");
        }
        Post savedComment = postService.upsert(comment);
        return ResponseEntity.ok(postService.addComment(parentPostOpt.get(), savedComment));
    }

    @DeleteMapping("{postId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable(name="postId") int postId, @PathVariable(name="commentId") int commentId) {
        Optional<Post> postOptional = postService.findById(postId);
        Optional<Post> commentOptional = postService.findById(commentId);
        if (!postOptional.isPresent()) {
            return ResponseEntity.badRequest().body("The post id provided does not link to an existing post.");
        }
        if (!commentOptional.isPresent()) {
            return ResponseEntity.badRequest().body("The comment id provided does not link to an existing comment.");
        }
        return ResponseEntity.ok(postService.deleteComment(postOptional.get(), commentOptional.get()));
    }

}
