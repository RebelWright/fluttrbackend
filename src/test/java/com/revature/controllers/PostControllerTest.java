package com.revature.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.services.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.revature.models.PostType.Top;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostService postService;
    //what we use in order to map our objects to JSon strings. For converting our objects to compare to expected
    //response from the servlet
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private Post mockedPostObject;
    @Mock
    private User mockedUserObject;

    @Test
    void getAllPostsTestSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser1);
        List<Post> expectedList = new ArrayList<>();

        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser1, Top, likesList);

        expectedList.add(expectedPost);

        given(postService.getAll()).willReturn(expectedList);

        this.mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(expectedList.size())));
    }
    @Test
    void getAllPostsTestFail() throws Exception {
        given(postService.getAll()).willReturn(null);

        this.mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }


    @Test
    void upsertPostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);


        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }
    //not passing. Still accepts bad request.
    @Test
    void upsertPostTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        Post unexpectedPost = new Post(2, "Unexpected post", "unexpected.com", new ArrayList<>(), testUser2, Top, likesList);

        given(postService.upsert(any(Post.class))).willReturn(unexpectedPost);

        mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTopPostsTestSuccess() {
    }
    @Test
    void getAllTopPostsTestFail() {
    }

    @Test
    void createPostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);


        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }
    /*@Test
    void createPostTestFail() throws Exception {}

    @Test
    void getPostByIdTestSuccess() {
    }
    @Test
    void getPostByIdTestFail() {
    }*/

    @Test
    void deletePostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);

        mockMvc.perform(delete("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1)))
                .andExpect(status().isOk())
                .andExpect(content().string("this post was deleted"));

        verify(postService, times(1)).deletePost(1);
    }

    @Test
    void deletePostTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);

        mockMvc.perform(delete("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(1)));
        //.andExpect(status().isBadRequest());

        verify(postService, never()).deletePost(1);
    }

    //mine
    @Test
    void addPostLikesSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser1, Top, new ArrayList<>());
        //Optional<Post> testPost = postService.findById(1);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        given(postService.findUserById(1)).willReturn(Optional.of(testUser1));
        this.mockMvc.perform(put("/posts/1/like/1")).andExpect(status().isOk());
    }

    @Test
    void addPostLikesTestSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        testUser1.setId(1);
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        String requestBody = objectMapper.writeValueAsString(expectedPost);
        when(postService.findById(expectedPost.getId())).thenReturn(Optional.of(expectedPost));
        when(postService.findUserById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        when(postService.addPostLike(expectedPost, testUser1)).thenReturn(expectedPost);

        mockMvc.perform(put("/posts/1/like/1"))
                .andExpect(status().isOk());
                //.andExpect(content().string("{\"id\":1,\"text\":\"text\",\"imageUrl\":\"imageUrl\",\"comments\":null,\"author\":null,\"postType\":null,\"likes\":[{\"id\":1,\"username\":\"username\",\"password\":\"password\",\"email\":\"email\"}]}"));

        verify(postService, times(1)).findById(expectedPost.getId());
        verify(postService, times(1)).findUserById(testUser1.getId());
        verify(postService, times(1)).addPostLike(expectedPost, testUser1);


    }

    void addPostLikesFail() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser1, Top, new ArrayList<>());
        //Optional<Post> testPost = postService.findById(1);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        //given(postService.findUserById(1)).willReturn(Optional.of(testUser1));
        this.mockMvc.perform(put("/posts/1/like/1")).andExpect(status().is(400));
    }

    //mine
    @Test
    void removePostLikesSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser1, Top, new ArrayList<>());
        //Optional<Post> testPost = postService.findById(1);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        given(postService.findUserById(1)).willReturn(Optional.of(testUser1));
        this.mockMvc.perform(put("/posts/1/unlike/1")).andExpect(status().isOk());
    }

    @Test
    void removePostLikesFail() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser1, Top, new ArrayList<>());
        //Optional<Post> testPost = postService.findById(1);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        //given(postService.findUserById(1)).willReturn(Optional.of(testUser1));
        this.mockMvc.perform(put("/posts/1/unlike/1")).andExpect(status().is(400));
    }

    @Test
    void editPostSuccess() throws Exception {
        User testUser2 = new User(1, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        List<Post> testList = new ArrayList<>();

        Post testpost = new Post(1,"test","www",testList ,testUser2,Top,likesList);


        String editString = "editTest";

        given(postService.findById(1)).willReturn(Optional.of(testpost));
        testpost.setText(editString);
        given(postService.upsert(testpost)).willReturn((testpost));
        this.mockMvc.perform(put("/posts/editPost/1")
                        .contentType(MediaType.APPLICATION_JSON).content(editString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("editTest"))
                .andExpect(jsonPath("$.imageUrl").value("www"))
                .andExpect(jsonPath("$.comments").value(testList))
                //.andExpect(jsonPath("$.author").value(testUser2))
                .andExpect(jsonPath("$.postType").value("Top"));
    }

    @Test
    void editPostFail() throws Exception {
        given(postService.findById(1)).willReturn(null);

        this.mockMvc.perform(put("/posts/editPost/1")
                        .contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
                .andExpect(status().is(400));

    }
    /*@Test
    void editPostTestFail() throws Exception {}*/

    @Test
    public void editPostByUrlSuccessTest() throws Exception {
        User testUser2 = new User(1, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);

        Post testpost = new Post(1,"test","www",testList ,testUser2,Top,likesList);


        String editString = "url/test.com";

        given(postService.findById(1)).willReturn(Optional.of(testpost));
        testpost.setImageUrl(editString);
        given(postService.upsert(testpost)).willReturn((testpost));
        this.mockMvc.perform(put("/posts/editPost/1/image")
                        .contentType(MediaType.APPLICATION_JSON).content(editString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("test"))
                .andExpect(jsonPath("$.imageUrl").value("url/test.com"))
                .andExpect(jsonPath("$.comments").value(testList))
                //.andExpect(jsonPath("$.author").value(testUser2))
                .andExpect(jsonPath("$.postType").value("Top"));

    }


    @Test
    public void editPostByUrlFailTest() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post testpost = new Post(2,"test","www",testList ,testUser2,Top,likesList);


        String editString = "url/test.com";

        given(postService.findById(1)).willReturn(Optional.empty());
        testpost.setImageUrl(editString);
        given(postService.upsert(testpost)).willReturn((testpost));
        this.mockMvc.perform(put("/posts/editPost/1/image")
                        .contentType(MediaType.APPLICATION_JSON).content(editString))
                .andExpect(status().is(400));
    }
    //need to add more .andExpect to verify content of response
    @Test
    public void createCommentTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        Post expectedComment = new Post(2,"This is a comment","image.com",new ArrayList<>(), testUser2, Top, likesList);
        String requestBody = objectMapper.writeValueAsString(expectedComment);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        given(postService.upsert(expectedComment)).willReturn(expectedComment);
        given(postService.addComment(expectedPost, expectedComment)).willReturn(expectedPost);
        mockMvc.perform(post("/posts/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    public void createCommentTestFailure() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        String requestBody = objectMapper.writeValueAsString(new Post(2,"This is a comment","image.com",new ArrayList<>(), testUser2, Top, likesList));

        given(postService.findById(1)).willReturn(Optional.empty());
        mockMvc.perform(post("/posts/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The parent post could not be found, please enter a valid parent post ID."));;
    }
    @Test
    public void deleteCommentTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        Post expectedComment = new Post(2,"This is a comment","image.com",new ArrayList<>(), testUser2, Top, likesList);

        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        given(postService.findById(2)).willReturn(Optional.of(expectedComment));
        mockMvc.perform(delete("/posts/1/comments/2"))
                .andExpect(status().isOk());

        verify(postService, times(1)).deleteComment(expectedPost, expectedComment);
    }
    @Test
    public void deleteCommentTestFailure() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, likesList);
        Post expectedComment = new Post(2,"This is a comment","image.com",new ArrayList<>(), testUser2, Top, likesList);
        given(postService.findById(1)).willReturn(Optional.empty());
        given(postService.findById(2)).willReturn(Optional.empty());
        mockMvc.perform(delete("/posts/1/comments/2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The post id provided does not link to an existing post."));

        verify(postService, never()).deleteComment(expectedPost, expectedComment);
    }


    @Test
    void createCommentsSuccess() throws Exception {
        User testUser = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());
        Post expectedPost2 = new Post(2, "testing", "image2.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());



        List<Post> testComment = new ArrayList<>();
        testComment.add(0,expectedPost2);
        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        expectedPost.setComments(testComment);
        given(postService.upsert(expectedPost)).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/posts/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void createCommentsFail() throws Exception {
        User testUser = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());
        Post expectedPost2 = new Post(2, "testing", "image2.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());


        List<Post> testComment = new ArrayList<>();
        testComment.add(0,expectedPost2);
        given(postService.findById(1)).willReturn(null);
        expectedPost.setComments(testComment);
        given(postService.upsert(null)).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/posts/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCreatedCommentsSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());
        Post expectedPost2 = new Post(1, "testing", "image2.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());

        given(postService.findById(1)).willReturn(Optional.of(expectedPost));
        given(postService.findById(1)).willReturn(Optional.of(expectedPost2));
        mockMvc.perform(delete("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        //verify(postService, times(1)).deletePost(1);
    }

    @Test
    void deleteCreateCommentsFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());
        Post expectedPost2 = new Post(1, "testing", "image2.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());

        given(postService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(delete("/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The post id provided does not link to an existing post."));


    }
}