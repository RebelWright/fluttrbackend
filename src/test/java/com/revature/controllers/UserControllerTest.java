package com.revature.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    //what we use in order to map our objects to JSon strings. For converting our objects to compare to expected
    //response from the servlet
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUser_Successful() throws Exception {
        //when method userService.save is given any User object, it will answer with an invocation(calling a method)
        //the invocation then gets the 1st indexed argument (0) of the userService.save() method
        //and then returns that argument
        given(userService.save(any(User.class))).willAnswer((invocation) -> invocation.getArgument(0));
        /*instantiating a new test user to use with the method. In this case, ALL fields must be written out
        even if they are going to have null values that we aren't checking in this particularly method.
        Followers and Following are both null when a user is created, but must be passed along as null
        in the constructor*/
        User testUser2 = new User(2,"test2.com","password2","Bob","Smith","BSmi",null,null,"image2.com");
        //Using this mockMvc perform a post request to the "/users" URI endpoint in controller
        this.mockMvc.perform(post("/users")
                        //we want our request to be Application_JSON mediatype for our server to read it
                        //used because we are performing a post request
                        .contentType(MediaType.APPLICATION_JSON)
                        //We use the object mapper we called earlier and it's method writeValueAsString to actually
                        //convert our user object to a JSon string
                        .content(objectMapper.writeValueAsString(testUser2)))
                //we are now defining what HTTP response is expected to return from our Http request to the servlet
                //in this case a status of 200 when request is ok
                .andExpect(status().is(200))
                //The $ symbol represents the root element of the JSON document, and the property name following
                // it is used to select the value of that property.
                // In this case, $.id is used to select the value of the id property in the JSON response.
                //We now compare each of our fields in JSon string that is returned with the same field in the test
                //user object. In this case, only comparing the fields that are not null.
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.email", is(testUser2.getEmail())))
                .andExpect(jsonPath("$.password", is(testUser2.getPassword())))
                .andExpect(jsonPath("$.firstName", is(testUser2.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(testUser2.getLastName())))
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())))
                .andExpect(jsonPath("$.imageUrl", is(testUser2.getImageUrl())));
    }
    //currently failing
    @Test
    void createNewUserWithoutEmailFail() throws Exception {
        User testUser2 = new User(2, null, "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String requestBody = objectMapper.writeValueAsString(testUser2);
        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getAllUsersTestSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        List<User> userList = new ArrayList<>();
        userList.add(testUser1);

        given(userService.getAll()).willReturn(userList);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(userList.size())));
    }
    //passes but isn't checking correctly
    @Test
    void getAllUsersTestFail() throws Exception {
        List<User> emptyList = new ArrayList<>();

        given(userService.getAll()).willReturn(emptyList);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(emptyList.size())));
    }

    @Test
    void getUserByIdTestSuccess() throws Exception {
        User testUser2 = new User(2,"test2.com","password2","Bob","Smith","BSmi",null,null,"image2.com");

        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        this.mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser2.getId())));
    }
    @Test
    void getUserByIdTestFail() throws Exception {
        User testUser2 = new User(2,"test2.com","password2","Bob","Smith","BSmi",null,null,"image2.com");

        given(userService.findById(3)).willReturn(Optional.empty());
        this.mockMvc.perform(get("/users/3"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void findByUsernameTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        given(userService.findByUsername("BSmi")).willReturn(Optional.of(testUser2));
        this.mockMvc.perform(get("/users/user/BSmi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())));

    }
    @Test
    void findByUsernameTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        given(userService.findByUsername("JDoe")).willReturn(Optional.empty());
        this.mockMvc.perform(get("/users/user/JDoe"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void editPasswordTestSuccess() throws Exception {
        //creating test user
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        //creating edited Password string
        String editPassword = "password3";
        //Using object mapper to write editPassword as a JSON string
        String requestBody = objectMapper.writeValueAsString(editPassword);
        //running the userService find by Id (2 in our test user) and returning Optional of test user
        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        //setting the test user's password as the new edited password
        testUser2.setPassword(editPassword);
        //when running save method in user service, will return our test user
        given(userService.save(testUser2)).willReturn((testUser2));
        this.mockMvc.perform(put("/users/2/password")
                        //we're sending JSon data
                        .contentType(MediaType.APPLICATION_JSON)
                        //our content we are sending is our JSON string of the edited password
                        .content(requestBody))
                .andExpect(status().isOk())
                //we are expecting Json data as a response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //we are expecting our response to contain an id of 2 and our new edited password
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.password", is(testUser2.getPassword())));

    }
    @Test
    void editPasswordTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String editPassword = "password3";
        String requestBody = objectMapper.writeValueAsString(editPassword);
        given(userService.findById(2)).willReturn(Optional.empty());

        this.mockMvc.perform(put("/users/3/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


    @Test
    void editEmailTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String editEmail = "com.test2";
        String requestBody = objectMapper.writeValueAsString(editEmail);
        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        testUser2.setEmail(editEmail);
        given(userService.save(testUser2)).willReturn((testUser2));
        this.mockMvc.perform(put("/users/2/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.email", is(testUser2.getEmail())));
    }
    @Test
    void editEmailTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String editEmail = "com.test2";
        String requestBody = objectMapper.writeValueAsString(editEmail);
        given(userService.findById(3)).willReturn(Optional.empty());

        this.mockMvc.perform(put("/users/3/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void editUsernameTestSuccess() throws Exception {
        String editUsername = "IMsb";
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String requestBody = objectMapper.writeValueAsString(editUsername);
        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        testUser2.setUsername(editUsername);
        given(userService.save(testUser2)).willReturn((testUser2));
        this.mockMvc.perform(put("/users/2/username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.username", is(testUser2.getUsername())));
    }
    @Test
    void editUsernameTestFail() throws Exception {
        String editUsername = "IMsb";
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String requestBody = objectMapper.writeValueAsString(editUsername);
        given(userService.findById(3)).willReturn(Optional.empty());
        this.mockMvc.perform(put("/users/3/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateImageUrlTestSuccess() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String editImageUrl = "com.image";
        String requestBody = objectMapper.writeValueAsString(editImageUrl);
        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        testUser2.setImageUrl(editImageUrl);
        given(userService.save(testUser2)).willReturn((testUser2));
        this.mockMvc.perform(put("/users/2/profileImage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testUser2.getId())))
                .andExpect(jsonPath("$.imageUrl", is(testUser2.getImageUrl())));
    }
    @Test
    void updateImageUrlTestFail() throws Exception {
        String editImageUrl = "com.image";
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        String requestBody = objectMapper.writeValueAsString(editImageUrl);
        given(userService.findById(3)).willReturn(Optional.empty());
        this.mockMvc.perform(put("/users/3/profileImage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

        //still needs work....
    @Test
    void addFollowerTestSuccess() throws Exception{
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        testUser1.setId(1);
        testUser1.setFollowers(new ArrayList<>());
        testUser1.setFollowing(new ArrayList<>());

        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        testUser2.setFollowers(new ArrayList<>());
        testUser2.setFollowing(new ArrayList<>());
        List<User> followSuccess = new ArrayList<>();
        followSuccess.add(testUser1);
        followSuccess.add(testUser2);
        given(userService.addFollower(testUser1, testUser2)).willReturn(followSuccess);

        given(userService.findById(1)).willReturn(Optional.of(testUser1));
        given(userService.findById(2)).willReturn(Optional.of(testUser2));
        mockMvc.perform(put("/users/1/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id", hasItems(testUser1.getId(), testUser2.getId())))
                .andExpect(jsonPath("$.length()", is(2)));
    }
    @Test
    void addFollowerTestFail() throws Exception{
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        testUser1.setId(1);

        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        given(userService.findById(1)).willReturn(Optional.of(testUser1));
        given(userService.findById(2)).willReturn(Optional.empty());
        mockMvc.perform(put("/users/1/follow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeFollowerTestSuccess() throws Exception {
        User followedUser = new User("test.com", "password", "John", "Doe", "JDoe");
        User follower = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");

        List<User> removeSuccess = new ArrayList<>();
        removeSuccess.add(followedUser);

        given(userService.findById(1)).willReturn(Optional.of(followedUser));
        given(userService.findById(2)).willReturn(Optional.of(follower));
        given(userService.removeFollower(followedUser, follower)).willReturn(removeSuccess);

        mockMvc.perform(put("/users/1/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(followedUser.getId())));

    }
    @Test
    void removeFollowerTestFail() throws Exception {
        User follower = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        given(userService.findById(1)).willReturn(Optional.empty());
        given(userService.findById(2)).willReturn(Optional.of(follower));

        mockMvc.perform(put("/users/1/unfollow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFeedForUserTestSuccess() throws Exception {
        User testUser1 = new User("test.com", "password", "John", "Doe", "JDoe");
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        testUser1.setId(1);
        Optional<User> optionalUser = Optional.of(testUser1);
        when(userService.findById(1)).thenReturn(optionalUser);
        List<Post> feed = new ArrayList<>();
        feed.add(new Post(1,"This is a test post","image.com",new ArrayList<>(), testUser2, PostType.Top, 1));
        feed.add(new Post(2,"Test post 2","image2.com",new ArrayList<>(), testUser2, PostType.Top, 1));
        when(userService.getFeedForUser(optionalUser.get())).thenReturn(feed);

        mockMvc.perform(get("/users/1/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(feed.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
    @Test
    void getFeedForUserTestFail() throws Exception {
        given(userService.findById(1)).willReturn(Optional.empty());
        mockMvc.perform(get("/users/1/feed"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAllPostsByAUserTestSuccess() throws Exception {
        User testUser = new User(1, "test.com", "password", "John", "Doe", "JDoe", null, null, "image.com");
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1, "Test post 1", "image1.com", new ArrayList<>(), testUser, PostType.Top, 1));
        posts.add(new Post(2, "Test post 2", "image2.com", new ArrayList<>(), testUser, PostType.Top, 1));

        given(userService.findById(1)).willReturn(Optional.of(testUser));
        given(userService.getAllPostsByAUser(testUser)).willReturn(Optional.of(posts));

        mockMvc.perform(get("/users/1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(posts.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }
    @Test
    void getAllPostsByAUserTestFail() throws Exception {
        User testUser2 = new User(2, "test2.com", "password2", "Bob", "Smith", "BSmi", null, null, "image2.com");
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(1, "Test post 1", "image1.com", new ArrayList<>(), testUser2, PostType.Top, 1));
        posts.add(new Post(2, "Test post 2", "image2.com", new ArrayList<>(), testUser2, PostType.Top, 1));

        given(userService.findById(1)).willReturn(Optional.empty());

        mockMvc.perform(get("/users/1/posts"))
                .andExpect(status().isBadRequest());
    }

}