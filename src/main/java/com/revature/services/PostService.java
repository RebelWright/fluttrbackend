package com.revature.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.springframework.stereotype.Service;

import com.revature.models.Post;
import com.revature.repositories.PostRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

	private PostRepository postRepository;
	private UserRepository userRepository;
	
	public PostService(PostRepository postRepository) {
		this.postRepository = postRepository;
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
	public void deletePost(int id) { this.postRepository.deleteById(id);}

	public Optional<List<Post>> getFeedForUser(List<User> following){
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
}
