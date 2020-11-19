package fact.it.users;

import fact.it.users.model.ImgBoardUser;
import fact.it.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ImgBoardUser user1 = new ImgBoardUser("r0703028@student.thomasmore.be", "test");
    private ImgBoardUser user2 = new ImgBoardUser("r0703029@student.thomasmore.be", "test2");

    @BeforeEach
    public void beforeAllTests() {
        userRepository.deleteAll();
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @AfterEach
    public void afterAllTests() {
        //Watch out with deleteAll() methods when you have other data in the test database!
        userRepository.deleteAll();
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenUser_whenGetUserByEmail_thenReturnJsonUser() throws Exception {

        mockMvc.perform(get("/user/{email}", "r0703028@student.thomasmore.be"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$.password", is("test")));
    }

    @Test
    public void givenUser_whenGetUsers_thenReturnJsonUsers() throws Exception {

        List<ImgBoardUser> userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);

        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$[0].password", is("test")))
                .andExpect(jsonPath("$[1].email", is("r0703029@student.thomasmore.be")))
                .andExpect(jsonPath("$[1].password", is("test2")));
    }


    @Test
    public void whenPostUser_thenReturnJsonUser() throws Exception {
        ImgBoardUser user3 = new ImgBoardUser("r0703030@student.thomasmore.be", "test3");

        mockMvc.perform(post("/user")
                .content(mapper.writeValueAsString(user3))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("r0703030@student.thomasmore.be")))
                .andExpect(jsonPath("$.password", is("test3")));
    }

    @Test
    public void givenUser_whenPutUser_thenReturnJsonUser() throws Exception {

        ImgBoardUser updateUser = new ImgBoardUser("r0703028@student.thomasmore.be", "test4");


        mockMvc.perform(put("/user")
                .content(mapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$.password", is("test4")));
    }

    @Test
    public void givenUser_whenDeleteUser_thenStatusOk() throws Exception {

        mockMvc.perform(delete("/user/{email}", "r0703028@student.thomasmore.be")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoReview_whenDeleteReview_thenStatusNotFound() throws Exception {

        mockMvc.perform(delete("/user/{email}", "test@testEmail.be")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
