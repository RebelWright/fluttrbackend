package com.revature.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;
    private String imageUrl;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Post> comments;
    @ManyToOne
    private User author;

    private PostType postType;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> likes;

    public Post() {
    }

    public Post(int id, String text, String imageUrl, List<Post> comments, User author, PostType postType, List<User> likes) {
        this.id = id;
        this.text = text;
        this.imageUrl = imageUrl;
        this.comments = comments;
        this.author = author;
        this.postType = postType;
        this.likes = likes;
    }

    public Post(String text, String imageUrl, List<Post> comments, User author, PostType postType) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.comments = comments;
        this.author = author;
        this.postType = postType;
        this.likes = new ArrayList<User>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Post> getComments() {
        return comments;
    }

    public void setComments(List<Post> comments) {
        this.comments = comments;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public List<User> getLikes() {
        return likes;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", comments=" + comments +
                ", author=" + author +
                ", postType=" + postType +
                ", likes=" + likes +
                '}';
    }
}
