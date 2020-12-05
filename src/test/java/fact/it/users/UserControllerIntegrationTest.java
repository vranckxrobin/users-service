package fact.it.users;

import com.jayway.jsonpath.JsonPath;
import fact.it.users.model.ImgBoardUser;
import fact.it.users.model.Login;
import fact.it.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.Assert.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private ImgBoardUser user1 = new ImgBoardUser("Robin","Vranckx","r0703028@student.thomasmore.be", "test");
    private ImgBoardUser user2 = new ImgBoardUser("Joske","Vermeulen","r0703029@student.thomasmore.be", "test2");

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
                .andExpect(jsonPath("$.firstname", is("Robin")))
                .andExpect(jsonPath("$.lastname", is("Vranckx")))
                .andExpect(jsonPath("$.email", is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$.password", is(user1.getPassword())));
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
                .andExpect(jsonPath("$[0].firstname", is("Robin")))
                .andExpect(jsonPath("$[0].lastname", is("Vranckx")))
                .andExpect(jsonPath("$[0].email", is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$[0].password", is(user1.getPassword())))
                .andExpect(jsonPath("$[1].firstname", is("Joske")))
                .andExpect(jsonPath("$[1].lastname", is("Vermeulen")))
                .andExpect(jsonPath("$[1].email", is("r0703029@student.thomasmore.be")))
                .andExpect(jsonPath("$[1].password", is(user2.getPassword())));
    }


    @Test
    public void whenPostUser_thenReturnJsonUser() throws Exception {
        ImgBoardUser user3 = new ImgBoardUser("John","Smith","r0703030@student.thomasmore.be", "test3");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MvcResult result = mockMvc.perform(post("/user")
                .content(mapper.writeValueAsString(user3))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("John")))
                .andExpect(jsonPath("$.lastname", is("Smith")))
                .andExpect(jsonPath("$.email", is("r0703030@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test3",encryptedPassword ));


        //.andExpect(encoder.matches("test3", MockMvcResultMatchers.jsonPath("$.password").value());
                //jsonPath("$.password", is(user3.getPassword()))
    }

    @Test
    public void givenUser_whenPutUser_thenReturnJsonUser() throws Exception {
        ImgBoardUser updateUser = new ImgBoardUser("Robin","Vranckx","r0703028@student.thomasmore.be", "test4");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


        MvcResult result = mockMvc.perform(put("/user")
                .content(mapper.writeValueAsString(updateUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Robin")))
                .andExpect(jsonPath("$.lastname", is("Vranckx")))
                .andExpect(jsonPath("$.email", is("r0703028@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test4",encryptedPassword ));    }

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

    @Test
    public void whenLoginUser_thenthenReturnJsonUser() throws Exception {
        ImgBoardUser user3 = new ImgBoardUser("John","Smith","r0703030@student.thomasmore.be", "test3");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MvcResult result = mockMvc.perform(post("/user")
                .content(mapper.writeValueAsString(user3))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("John")))
                .andExpect(jsonPath("$.lastname", is("Smith")))
                .andExpect(jsonPath("$.email", is("r0703030@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test3",encryptedPassword ));

        Login login = new Login(user3.getEmail(),"test3");

        mockMvc.perform(post("/login")
                .content(mapper.writeValueAsString(user3))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("John")))
                .andExpect(jsonPath("$.lastname", is("Smith")))
                .andExpect(jsonPath("$.email", is("r0703030@student.thomasmore.be")))
                .andExpect(jsonPath("$.password", is(encryptedPassword)));

    }
    @Test
    public void whenLoginUserWrong_thenthenReturnNull() throws Exception {
        ImgBoardUser user3 = new ImgBoardUser("John","Smith","r0703030@student.thomasmore.be", "test3");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MvcResult result = mockMvc.perform(post("/user")
                .content(mapper.writeValueAsString(user3))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("John")))
                .andExpect(jsonPath("$.lastname", is("Smith")))
                .andExpect(jsonPath("$.email", is("r0703030@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test3",encryptedPassword ));

        Login login = new Login(user3.getEmail(),"test4");

        mockMvc.perform(post("/login")
                .content(mapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").doesNotExist());


    }
}
