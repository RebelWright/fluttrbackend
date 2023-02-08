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
import org.springframework.test.web.servlet.ResultActions;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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


    @Test
    void getAllPostsTestSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser1);
        List<Post> expectedList = new ArrayList<>();

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser1, Top, likesList);

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
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);


        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void upsertPostTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);
        Post unexpectedPost = new Post(2, "Unexpected post", "unexpected.com", new ArrayList<>(), testUser2, Top, likesList);

        given(postService.upsert(expectedPost)).willReturn(null);

        ResultActions resultActions = mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unexpectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void createPostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);


        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void createPostTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);
        Post unExpectedPost = new Post(2, "test post2", "image.com2", new ArrayList<>(), testUser2, Top, likesList);

        given(postService.upsert(any(Post.class))).willReturn(null);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unExpectedPost)))
                .andExpect(status().isOk());
    }


    @Test
    void deletePostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);

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
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, likesList);

        mockMvc.perform(delete("/posts/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1)))
                .andExpect(status().isOk());

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

        Post testpost = new Post(1, "test", "www", testList, testUser2, Top, likesList);


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

    @Test
    public void editPostByUrlSuccessTest() throws Exception {
        User testUser2 = new User(1, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser2);

        Post testpost = new Post(1, "test", "www", testList, testUser2, Top, likesList);


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
        Post testpost = new Post(2, "test", "www", testList, testUser2, Top, likesList);


        String editString = "url/test.com";

        given(postService.findById(1)).willReturn(Optional.empty());
        testpost.setImageUrl(editString);
        given(postService.upsert(testpost)).willReturn((testpost));
        this.mockMvc.perform(put("/posts/editPost/1/image")
                        .contentType(MediaType.APPLICATION_JSON).content(editString))
                .andExpect(status().is(400));
    }

    @Test
    void createCommentsSuccess() throws Exception {
        User testUser = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());
        Post expectedPost2 = new Post(2, "testing", "image2.com", new ArrayList<>(), testUser2, Top, new ArrayList<>());


        List<Post> testComment = new ArrayList<>();
        testComment.add(0, expectedPost2);
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
        testComment.add(0, expectedPost2);
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

    @Test
    public void getAllTopPostTestSuccessful() throws Exception {
        User testUser = new User(1, "test@Email.com", "password", "firstN", "LastN", "Username", null, null, "image.com");
        List<Post> listTopPost = new ArrayList<>();
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser);
        Post testPost = new Post(1, "String Text", "String URL", new ArrayList<>(), testUser, PostType.Top, likesList);

        listTopPost.add(testPost);
        given(postService.getAllTop()).willReturn(listTopPost);
        this.mockMvc.perform(get("/posts/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(listTopPost.size())));

    }


    @Test
    public void getAllTopPostTestFail() throws Exception {
        User testUser = new User(1, "test@Email.com", "password", "firstN", "LastN", "Username", null, null, "image.com");
        List<Post> listTopPost = new ArrayList<>();
        List<User> likesList = new ArrayList<>();
        likesList.add(testUser);
        Post testPost = new Post(-1, "String Text", "String URL", new ArrayList<>(), testUser, Top, likesList);
        List<Post> badList = new ArrayList<>();

        listTopPost.add(testPost);
        listTopPost.add(new Post(3, "test post3", "image3.com", new ArrayList<>(), testUser, PostType.Reply, likesList));
        given(postService.getAllTop()).willReturn(listTopPost);
        this.mockMvc.perform(get("/posts/feed"))
                .andExpect(jsonPath("$.size()", is(listTopPost.size())))
                .andExpect(status().is(200)).andDo(print()).andReturn().getResponse().getContentAsString();
    }


    @Test
    public void getPostByIdTestSuccessful() throws Exception {
        User testUser3 = new User("testing@t.com", "password", "Test", "Dummy", "testdummy");
        Post testPost1 = new Post("testing text", " testing string URL", new ArrayList<>(), testUser3, PostType.Top);
        testUser3.setId(4);
        testPost1.setId(4);
        Optional<Post> optionalPost = Optional.of(testPost1);
        List<Post> ListFeed = new ArrayList<>();
        given(postService.findById(4)).willReturn(optionalPost);
        //given(postService.findById(testUser3.getId())).willReturn(Optional.of(testPost1));
        this.mockMvc.perform(get("/posts/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testPost1.getId())));
    }

    @Test
    public void getPostByIdTestFail() throws Exception {
        User testUser3 = new User("testing@t.com", "password", "Test", "Dummy", "testdummy");
        User testUser4 = new User("testing@t.com", "password", "Test", "Dummy", "testdummy");
        Post testPost1 = new Post("testing text", " testing string URL", new ArrayList<>(), testUser3, PostType.Top);
        Post testPost2 = new Post("testing text", " testing string URL", new ArrayList<>(), testUser3, PostType.Top);
        testUser3.setId(4);
        testUser4.setId(5);
        testPost1.setId(4);
        testPost2.setId(5);
        Optional<Post> optionalPost = Optional.of(testPost1);
        List<Post> ListFeed = new ArrayList<>();
        given(postService.findById(4)).willReturn(optionalPost);
        this.mockMvc.perform(get("/posts/5"))
                .andExpect(status().is(400)).andDo(print()).andReturn().getResponse().getContentAsString();
    }
}