package com.revature.services;

import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mock.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PostService postService;
    @Mock
    private User testFollowedUser;
    @Mock
    private User testFollowerUser;
    @Mock
    private User mockedUserObject;
    @Mock
    private Post mockedPostObject;
    @InjectMocks
    private UserService userService;

    @Test
    void findByCredentialsSuccessTest() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByEmailAndPassword("test.com", "password")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByCredentials("test.com","password");
        assertEquals(resultUser.get().getEmail(),mockUser.getEmail());
        assertEquals(resultUser.get().getPassword(),mockUser.getPassword());

    }
    @Test
    void findByCredentialsSuccessFail() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByEmailAndPassword("test.com", "password")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByCredentials("test.com","password");
        assertNotEquals("com.test",mockUser.getEmail());
        assertNotEquals("drowssap",mockUser.getPassword());
        assertNotEquals(resultUser.get().getEmail(),"com.test");
        assertNotEquals(resultUser.get().getPassword(),"drowssap");

    }


    @Test
    void findByIdTestSuccess() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        mockUser.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findById(1);
        assertEquals(resultUser.get().getId(), mockUser.getId());
    }

    @Test
    void findByIdTestFail() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        mockUser.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findById(1);
        assertNotEquals(resultUser.get().getId(), 100);
        assertNotEquals(100, mockUser.getId());
    }



    @Test
    void saveUserTestSuccess() {
        when(userRepository.save(mockedUserObject)).thenReturn(mockedUserObject);
        User resultUser = userService.save(mockedUserObject);
        verify(userRepository, times(1)).save(mockedUserObject);
        assertEquals(mockedUserObject, resultUser);
    }
    @Test
    void saveUserTestFail() {
        //mockUser object intentionally has a null username field in order to fail test and trigger exception
        User mockUser = new User("test.com","password","John","Doe", null);
        //using Exceptions for failing methods that don't return an object when succeeding
        when(userRepository.save(mockUser)).thenThrow(new IllegalArgumentException());
        /*assertThrows takes in 2 arguments(exceptions) and compares the first exception to the second
        using a lambda function that contains the code that should throw the exception*/
        assertThrows(IllegalArgumentException.class, () -> userService.save(mockUser));
        /*verify method is used to check if the method was executed. Verify testing the save method of userRepository
        one time and takes in mockUser as the argument of save
        */
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void getAllUsersTestSuccess() {
        //creating empty ArrayList
        List<User> expectedList = new ArrayList<User>();
        //adding mockedUserObject to the list of users
        expectedList.add(mockedUserObject);
        //testing Repository method, when findAll is mocked it should return our expectedList
        when(userRepository.findAll()).thenReturn(expectedList);
        //using the user Service method and saving the result as an ArrayList
        List<User> resultList = userService.getAll();
        //using asserEquals to compare the expected size (1) to the actual result size
        assertEquals(1, resultList.size());
        //using assertEquals to compare the mockUser from our expectedList to the 1st user returned from
        //our actual result list
        assertEquals(expectedList.get(0), resultList.get(0));
    }
    @Test
    void getAllUsersTestFail() {
        //creating empty ArrayList
        List<User> expectedList = new ArrayList<User>();
        //adding mockedUserObject to the list of users
        expectedList.add(mockedUserObject);
        //testing Repository method, when findAll is mocked it should return our expectedList
        when(userRepository.findAll()).thenReturn(expectedList);
        //using the user Service method and saving the result as an ArrayList
        List<User> resultList = userService.getAll();
        //using assertEquals to compare the expected size (1000) to the actual result size
        assertNotEquals(1000, resultList.size());
        assertNotEquals(expectedList.size(), 1000);
        //using assertEquals to compare the mockUser from our expectedList to the 1st user returned from
        //our actual result list
        assertNotEquals(null, resultList.get(0));
        assertNotEquals(expectedList.get(0), null);
    }



    @Test
    void findByUsernameTestSuccess() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByUsername("JDoe")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByUsername("JDoe");
        assertEquals(resultUser.get().getUsername(), mockUser.getUsername());
    }
    @Test
    void findByUsernameTestFail() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByUsername("JDoe")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByUsername("JDoe");
        assertNotEquals(resultUser.get().getUsername(), "Eodj");
        assertNotEquals("Eodj", mockUser.getUsername());
    }

    @Test
    void findByEmailTestSuccess() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByEmail("test.com")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByEmail("test.com");
        assertEquals(resultUser.get().getEmail(), mockUser.getEmail());
    }
    @Test
    void findByEmailTestFail() {
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        when(userRepository.findByEmail("test.com")).thenReturn(Optional.of(mockUser));
        Optional<User> resultUser = userService.findByEmail("test.com");
        assertNotEquals(resultUser.get().getEmail(), "com.test");
        assertNotEquals("com.test", mockUser.getEmail());
    }

    @Test
    void addUserToFollowingListTestSuccess() {
        //creating arrayLists when get methods are mocked on our mock user objects
        when(testFollowedUser.getFollowers()).thenReturn(new ArrayList<>());
        when(testFollowerUser.getFollowing()).thenReturn(new ArrayList<>());
        //creating our followsuccessList when service method addFollower is called with our mock
        //user objects passed in as arguments
        List<User> mockFollowSuccessList = userService.addFollower(testFollowedUser, testFollowerUser);
        //testing that the mockFollowSuccessList is actually 2 (like it should be)
        assertEquals(mockFollowSuccessList.size(), 2);
        //testing that each of the mock user objects were added to our mockFollowSuccessList
        assertEquals(mockFollowSuccessList.get(0),testFollowedUser);
        assertEquals(mockFollowSuccessList.get(1),testFollowerUser);
        //creating an expected followers list and adding our testFollowerUser to that list
        List<User> expectedFollowers = new ArrayList<>();
        expectedFollowers.add(testFollowerUser);
        //verifying that our mockFollowedUser had it's followers list updated with the expectedFollowers list
        verify(testFollowedUser).setFollowers(expectedFollowers);
        //creating an expectedFollowing list and adding our testFollowedUser to that list
        List<User> expectedFollowing = new ArrayList<>();
        expectedFollowing.add(testFollowedUser);
        //verifying that our mockFollowerUser had it's following list updated with the expectedFollowing list
        verify(testFollowerUser).setFollowing(expectedFollowing);

    }
    @Test
    void addUserToFollowingListTestFail() {
        //when takes in testFollowedUser mock object, tries to get followers and is expected to throw a runtime
        // exception when trying to add followers.
        when(testFollowedUser.getFollowers()).thenThrow(new RuntimeException("Error adding followed user"));
        /*assertThrows takes in 2 arguments(exceptions) and compares the first exception to the second
        using a lambda function that contains the code that should throw the exception*/
        assertThrows(RuntimeException.class, () -> userService.addFollower(testFollowedUser,testFollowerUser));
        //finally the verify methods in this case are expected to never actually run if the exception was
        //correctly thrown.
        verify(testFollowedUser, never()).setFollowers(any());
        verify(testFollowerUser, never()).setFollowing(any());
    }

    @Test
    void removeFollowerTestSuccess() {
        List<User> testFollowers = new ArrayList<>();
        testFollowers.add(testFollowerUser);
        when(testFollowedUser.getFollowers()).thenReturn(testFollowers);

        List<User> testFollowing = new ArrayList<>();
        testFollowing.add(testFollowedUser);
        when(testFollowerUser.getFollowing()).thenReturn(testFollowing);

        List<User> unFollowSuccessList = userService.removeFollower(testFollowedUser, testFollowerUser);
        assertEquals(unFollowSuccessList.size(), 2);
        assertEquals(unFollowSuccessList.get(0),testFollowedUser);
        assertEquals(unFollowSuccessList.get(1),testFollowerUser);

        List<User> expectedFollowers = new ArrayList<>();
        verify(testFollowedUser).setFollowers(expectedFollowers);
        List<User> expectedFollowing = new ArrayList<>();
        verify(testFollowerUser).setFollowing(expectedFollowing);
    }
    //something is off about this one. setFollowers and setFollowing are both running.
    @Test
    void removeFollowerTestFailure() {
        List<User> testFollowers = new ArrayList<>();
        when(testFollowedUser.getFollowers()).thenReturn(testFollowers);

        List<User> testFollowing = new ArrayList<>();
        when(testFollowerUser.getFollowing()).thenReturn(testFollowing);

        List<User> unFollowFailureList = userService.removeFollower(testFollowedUser, testFollowerUser);
        assertEquals(unFollowFailureList.size(), 2);

        verify(testFollowedUser, never()).setFollowers(anyList());
        verify(testFollowerUser, never()).setFollowing(anyList());
    }

    @Test
    void getFeedForUserTestSuccess() {
        List<User> following = new ArrayList<>();
        following.add(testFollowedUser);
        when(mockedUserObject.getFollowing()).thenReturn(following);
        List<Post> expectedFeed = new ArrayList<>();
        expectedFeed.add(mockedPostObject);
        when(postService.getFeedForUser(following)).thenReturn(Optional.of(expectedFeed));
        List<Post> actualFeed = userService.getFeedForUser(mockedUserObject);
        assertEquals(expectedFeed, actualFeed);


    }
    @Test
    void getFeedForUserTestFail(){
        List<User> following = new ArrayList<>();
        following.add(testFollowedUser);
        when(mockedUserObject.getFollowing()).thenReturn(following);
        List<Post> expectedFeed = new ArrayList<>();
        expectedFeed.add(mockedPostObject);
        when(postService.getFeedForUser(following)).thenReturn(Optional.empty());
        List<Post> actualFeed = userService.getFeedForUser(mockedUserObject);
        assertNull(actualFeed);

    }
    @Test
    void getAllPostsByAUserTestSuccess() {
        User mockedUser = new User("test.com", "somepassword", "First", "Last", "username");
        mockedUser.setId(1);
        when(userRepository.findById(mockedUser.getId())).thenReturn(Optional.of(mockedUser));
        Optional<User> userOptional = userRepository.findById(mockedUser.getId());
        assertEquals(userOptional, Optional.of(mockedUser));
        List<Post> userPosts = new ArrayList<>();
        userPosts.add(mockedPostObject);
        when(postService.getAllPostsByUser(mockedUser)).thenReturn(Optional.of(userPosts));
        Optional<List<Post>> allUserPostsOptional = postService.getAllPostsByUser(mockedUser);
        assertEquals(allUserPostsOptional, Optional.of(userPosts));
    }
    @Test
    void getAllPostsByAUserTestFail() {
        User mockedUser = new User("test.com", "somepassword", "First", "Last", "username");
        mockedUser.setId(1);
        mockedPostObject.setAuthor(mockedUser);
        List<Post> userPosts = new ArrayList<>();
        userPosts.add(mockedPostObject);
        when(postService.getAllPostsByUser(mockedUser)).thenReturn(Optional.empty());
        Optional<List<Post>> allUserPostsOptional = postService.getAllPostsByUser(mockedUser);
        assertFalse(allUserPostsOptional.isPresent());
    }
}