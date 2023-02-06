package com.revature.services;

import com.revature.models.Post;
import com.revature.models.PostType;
import com.revature.models.User;
import com.revature.repositories.PostRepository;
import com.revature.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;
    @InjectMocks
    private UserService userService;
    @Mock
    private User mockedUserObject;
    @Mock
    private Post mockedPostObject;
    @Mock
    private Post mockedComment;

    @Mock
    private Post testUserFeed;

    @Mock
    private List<Post> testFeed;
    @Mock
    private User testFollowedUser;
    @Mock
    private User testFollowerUser;



    @Test
    void testGetAllPostsSuccess() {
        //creating empty ArrayList
        List<Post> expectedList = new ArrayList<Post>();
        //adding mockedPostObject to array
        expectedList.add(mockedPostObject);
        //testing Repository method, when findAll is mocked it should return our expectedList
        when(postRepository.findAll()).thenReturn(expectedList);
        //using the post Service method and saving the result as an ArrayList
        List<Post> resultList = postService.getAll();
        //using asserEquals to compare the expected size (1) to the actual result size
        assertEquals(1, resultList.size());
        //using assertEquals to compare the expectedPost from our expectedList to the 1st post returned from
        //our actual result list
        assertEquals(mockedPostObject, resultList.get(0));
    }


    @Test
    void upsertSuccessTest() {
        when(postRepository.save(mockedPostObject)).thenReturn(mockedPostObject);
        Post resultPost = postService.upsert(mockedPostObject);
        verify(postRepository, times(1)).save(mockedPostObject);
        assertEquals(mockedPostObject, resultPost);
    }

    @Test
    void upsertFailTest(){
        User mockUser = new User("test.com","password","John","Doe", "JDoe");
        // tried to change to null but what is unique in post to test a null value
        Post mockPost = new Post(1, "Test text for upsert", "Profile pic",new ArrayList<>(), mockedUserObject, PostType.Top, 21);
        lenient().when(postRepository.save(mockedPostObject)).thenReturn(mockedPostObject);
        //assertThrows(IllegalAccessError.class, () -> postService.upsert(mockPost));
        verify(postRepository,never()).save(mockPost);
    }


    @Test
    void getAllTopPostsSuccess() {
        //creating empty ArrayList
        List<Post> expectedList = new ArrayList<Post>();
        //creating test post for ArrayList
        Post expectedPost = new Post(1,"This is a test post","image.com",new ArrayList<>(), mockedUserObject, PostType.Top, 1);
        //adding post to array
        expectedList.add(expectedPost);
        when(postRepository.findAllByPostType(PostType.Top)).thenReturn(expectedList);
        //using the post Service method and saving the result as an ArrayList
        List<Post> resultList = postService.getAllTop();
        //using asserEquals to compare the expected size (1) to the actual result size
        assertEquals(1, resultList.size());
        //using assertEquals to compare the expectedPost from our expectedList to the 1st post returned from
        //our actual result list
        assertEquals(expectedPost, resultList.get(0));

    }
    @Test
    void getAllTopFail() {
        //creating empty ArrayList
        List<Post> expectedList = new ArrayList<Post>();
        //creating test post for ArrayList
        Post expectedPost = new Post(1, "This is a test post", "image.com", new ArrayList<>(), mockedUserObject, PostType.Top, 1);
        //adding post to array
        expectedList.add(expectedPost);
        when(postRepository.findAllByPostType(PostType.Top)).thenReturn(expectedList);
        //using the post Service method and saving the result as an ArrayList
        List<Post> resultList = postService.getAllTop();
        //using asserEquals to compare the expected size (1) to the actual result size
        assertNotEquals(0, resultList.size());
        //using assertEquals to compare the expectedPost from our expectedList to the 1st post returned from
        //our actual result list
        assertNotEquals(new Post(), resultList.get(0));
    }

    @Test
    void findByIdTestSuccess() {
        List<Post> expectedList = new ArrayList<Post>();
        Post mockPost = new Post(2,"First post", "FirstPic", new ArrayList<>(), mockedUserObject, PostType.Top,1);
        when(postRepository.findById(2)).thenReturn(Optional.of(mockPost));
        Optional<Post> resultPost = postService.findById(2);
        assertEquals(resultPost.get().getId(), mockPost.getId());
    }


    @Test
    void findByIdTestFail() {
        List<Post> expectedList = new ArrayList<Post>();
        Post mockPost = new Post(2,"First post", "FirstPic", new ArrayList<>(), mockedUserObject, PostType.Top,1);
        when(postRepository.findById(2)).thenReturn(Optional.of(mockPost));
        Optional<Post> resultPost = postService.findById(2);
        assertNotEquals(resultPost.get(), mockPost.getId());

    }
    @Test
    void findByIdTestFail2() {
        List<Post> expectedList = new ArrayList<Post>();
        Post mockPost = new Post(2,"First post", "FirstPic", new ArrayList<>(), mockedUserObject, null,1);
        when(postRepository.findById(2)).thenReturn(Optional.of(mockPost));
        Optional<Post> resultPost = postService.findById(2);
        assertNotEquals(resultPost.get(), mockPost.getId());

    }

/*    @Test
    void deletePostTestSuccess() {
        Post testPost = new Post(2, "First post", "FirstPic", new ArrayList<>(), mockedUserObject, PostType.Top, 1);
<<<<<<< Updated upstream
        postService.deletePost(testPost.getId());
        verify(postRepository).deleteById(testPost.getId());
    }
=======
        List<Post> removePostTest = new ArrayList<>();
        removePostTest.add(testPost);// gives us a list of mock list with a list of mock post objects
        doNothing().when(postService.deletePost(2));
        verify(userRepository).deleteById(testUser2.getId());
        //doNothing().when(postService.deletePost(2));
        //verify(postService, times(1)).deletePost(2);
    }*/
        //when(postRepository.deleteById(2)).thenReturn(testFeed);


//    @Test
//    void deletePostTestFail() {
//
//    }
//
    @Test
    void getFeedForUserTestSuccess()
    {
        // tried to change to null but what is unique in post to test a null value

        List<Post> testFeed = new ArrayList<>();
        //Post testPost = new Post(1, "Test text for upsert", "Profile pic",new ArrayList<>(), mockedUserObject, PostType.Top, 21);

        testFeed.add(mockedPostObject);
        List<User> userTestfollowing = new ArrayList<>();
        //User testUser = new User("test.com","password","John","Doe", "JDoe");

        userTestfollowing.add((testFollowedUser));
        //when(mockedUserObject.getFollowing()).thenReturn(userTestfollowing);// should return the users we are following
        when(postRepository.findByAuthorInAndPostType(userTestfollowing,PostType.Top)).thenReturn(Optional.of(testFeed));// we are finding all of the users we are following by top Post
        Optional<List<Post>> expectedFeed = postService.getFeedForUser(userTestfollowing);// testing our hard coded userTestFollowing against the expected value
        assertEquals(expectedFeed,Optional.of(testFeed));// if the expectedFeed and userTestFollowing equal it will return successful


    }
    @Test
    void getFeedForUserTestFail() {
        List<Post> testFeed = new ArrayList<>();
        testFeed.add(mockedPostObject);
        List<User> addUserFollowingTest = new ArrayList<>();
        addUserFollowingTest.add(testFollowedUser);
        lenient().when(postRepository.findByAuthorInAndPostType(addUserFollowingTest,PostType.Top)).thenReturn(Optional.of(testFeed));
        Optional<List<Post>> expectedFeed = postService.getAllPostsByUser(testFollowedUser);
        assertNotEquals(expectedFeed,addUserFollowingTest);


    }

    public void addCommentTestSuccess() {
        //create an empty array to beging with for our comments
        List<Post> commentList = new ArrayList<>();
        //using mockedPostObject since we don't need any actual fields, and we're set it's comments to the new array
        mockedPostObject.setComments(commentList);
        //when we get the comments from the mockedPost it will return our commmentList
        when(mockedPostObject.getComments()).thenReturn(commentList);
        //here we're creating an expected Post that we use our addComment method and pass mockedPost and mockedComment.
        Post actualPost = postService.addComment(mockedPostObject, mockedComment);
        //now we're going to compare our mockedPost with our expectedPost
        assertEquals(mockedPostObject, actualPost);
        //also compare the size, we're expecting 1 and actual is returning a size of 1 and we compare
        assertEquals(1, actualPost.getComments().size());
        //with this last one, we are comparing the mockedComment that got added with our postService to our comment that was added to our actual post
        assertEquals(mockedComment, actualPost.getComments().get(0));
    }
    @Test
    public void addCommentTestFail() {
        List<Post> commentList = new ArrayList<>();
        mockedPostObject.setComments(commentList);
        when(mockedPostObject.getComments()).thenReturn(commentList);
        Post expectedPost = postService.addComment(mockedPostObject, mockedComment);

        assertNull(expectedPost);
    }
    @Test
    public void deleteCommentTestSuccess() {
        List<Post> commentList = new ArrayList<>();
        commentList.add(mockedComment);
        mockedPostObject.setComments(commentList);
        when(mockedPostObject.getComments()).thenReturn(commentList);
        Post expectedPost = postService.deleteComment(mockedPostObject,mockedComment);
        assertEquals(mockedPostObject, expectedPost);
        assertEquals(0,expectedPost.getComments().size());
    }
}