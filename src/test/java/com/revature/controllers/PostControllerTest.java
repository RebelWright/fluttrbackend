package com.revature.controllers;

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
import java.util.List;
import java.util.Optional;

import static com.revature.models.PostType.Top;
import static org.hamcrest.CoreMatchers.is;
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
        List<Post> expectedList = new ArrayList<>();
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser1, Top, 1);
        expectedList.add(expectedPost);

        given(postService.getAll()).willReturn(expectedList);

        this.mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(expectedList.size())));
    }


    @Test
    void upsertPostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, 1);

        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(put("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllTopPosts() {
    }

    @Test
    void createPostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, 1);

        given(postService.upsert(any(Post.class))).willAnswer((invocation) -> invocation.getArgument(0));

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedPost)))
                .andExpect(status().isOk());
    }

    @Test
    void getPostById() {
    }

    @Test
    void deletePostTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, 1);
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
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, Top, 1);
        mockMvc.perform(delete("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1)));
                //.andExpect(status().isBadRequest());

        verify(postService, never()).deletePost(1);
    }

    @Test
    void addPostLikes() {
    }

    @Test
    void removePostLikes() {
    }

    @Test
    void editPost() throws Exception {
        User testUser2 = new User(1, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();

        Post testpost = new Post(1,"test","www",testList ,testUser2,Top,0);

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
                .andExpect(jsonPath("$.postType").value("Top"))
                .andExpect(jsonPath("$.likes").value(0));
    }

    @Test
    public void editPostByUrlSuccessTest() throws Exception {
        User testUser2 = new User(1, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();

        Post testpost = new Post(1,"test","www",testList ,testUser2,Top,0);

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
                .andExpect(jsonPath("$.postType").value("Top"))
                .andExpect(jsonPath("$.likes").value(0));
    }


    @Test
    public void editPostByUrlFailTest() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> testList = new ArrayList<>();

        Post testpost = new Post(2,"test","www",testList ,testUser2,Top,0);

        String editString = "url/test.com";

        given(postService.findById(1)).willReturn(Optional.empty());
        testpost.setImageUrl(editString);
        given(postService.upsert(testpost)).willReturn((testpost));
        this.mockMvc.perform(put("/posts/editPost/1/image")
                        .contentType(MediaType.APPLICATION_JSON).content(editString))
                .andExpect(status().is(400));
    }
}