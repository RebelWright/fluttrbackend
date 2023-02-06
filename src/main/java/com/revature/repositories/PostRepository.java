package com.revature.repositories;

import com.revature.models.PostType;
import com.revature.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer>{

    List<Post> findAllByPostType(PostType postType);
    Optional<List<Post>> findByAuthorInAndPostType(List<User> author, PostType postType);
    Optional<List<Post>> findAllByAuthorAndPostType(User user, PostType postType);

}
