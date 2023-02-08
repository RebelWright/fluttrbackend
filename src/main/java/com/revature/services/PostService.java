package com.revature.services;

import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.repositories.PostRepository;
import com.revature.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final UserRepository userRepository;
    private PostRepository postRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAll() {
        return this.postRepository.findAll();
    }

    public Post upsert(Post post) {
        return this.postRepository.save(post);
    }

    public List<Post> getAllTop() {
        return postRepository.findAllByPostType(PostType.Top);
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void deletePost(int id) {
        this.postRepository.deleteById(id);
    }

    public Optional<List<Post>> getFeedForUser(List<User> following) {
        Optional<List<Post>> feed = postRepository.findByAuthorInAndPostType(following, PostType.Top);
        return feed;
    }

    public Optional<List<Post>> getAllPostsByUser(User user) {
        return postRepository.findAllByAuthorAndPostType(user, PostType.Top);
    }

    @Transactional
    public Post addComment(Post post, Post comment) {
        List<Post> commentList = post.getComments();
        commentList.add(comment);
        post.setComments(commentList);
        return post;
    }

    @Transactional
    public Post deleteComment(Post post, Post comment) {
        List<Post> commentList = post.getComments();
        commentList.remove(comment);
        post.setComments(commentList);
        deletePost(comment.getId());
        return post;
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Post addPostLike(Post post, User liker) {
        List<User> postLikesList = post.getLikes();
        postLikesList.add(liker);
        post.setLikes(postLikesList);
        return post;
    }

    @Transactional
    public Post removePostLike(Post post, User liker) {
        List<User> postLikesList = post.getLikes();
        postLikesList.remove(liker);
        post.setLikes(postLikesList);
        return post;
    }
}
