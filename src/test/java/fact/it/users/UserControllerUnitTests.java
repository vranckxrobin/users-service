package fact.it.users;

import com.jayway.jsonpath.JsonPath;
import fact.it.users.model.ImgBoardUser;
import fact.it.users.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenUser_whenGetUserByEmail_thenReturnJsonReview() throws Exception {
        ImgBoardUser user = new ImgBoardUser("r0703028@student.thomasmore.be","test");

        given(userRepository.findImgBoardUserByEmail("r0703028@student.thomasmore.be")).willReturn(user);

        mockMvc.perform(get("/user/{email}","r0703028@student.thomasmore.be"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email",is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$.password",is("test")));
    }
    @Test
    public void givenUser_whenGetAllUser_thenReturnJsonReview() throws Exception {
        ImgBoardUser user = new ImgBoardUser("r0703028@student.thomasmore.be","test");
        ImgBoardUser user2 = new ImgBoardUser("r0703029@student.thomasmore.be","test2");

        List<ImgBoardUser> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        given(userRepository.findAll()).willReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email",is("r0703028@student.thomasmore.be")))
                .andExpect(jsonPath("$[0].password",is("test")))
                .andExpect(jsonPath("$[1].email",is("r0703029@student.thomasmore.be")))
                .andExpect(jsonPath("$[1].password",is("test2")));
    }

    @Test
    public void whenPostUser_thenReturnJsonReview() throws Exception{
        ImgBoardUser user = new ImgBoardUser("r0703028@student.thomasmore.be","test");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MvcResult result = mockMvc.perform(post("/user")
                .content(mapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email",is("r0703028@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test",encryptedPassword ));
    }

    @Test
    public void givenUser_whenPutUser_thenReturnJsonReview() throws Exception{
        ImgBoardUser user = new ImgBoardUser("r0703028@student.thomasmore.be","test");

        given(userRepository.findImgBoardUserByEmail("r0703028@student.thomasmore.be")).willReturn(user);

        ImgBoardUser updatedUser = new ImgBoardUser("r0703028@student.thomasmore.be","test2");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MvcResult result = mockMvc.perform(put("/user")
                .content(mapper.writeValueAsString(updatedUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email",is("r0703028@student.thomasmore.be")))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        String encryptedPassword = JsonPath.parse(response).read("$.password").toString();
        assertTrue(encoder.matches("test2",encryptedPassword ));    }

    @Test
    public void givenUser_whenDeleteUser_thenStatusOk() throws Exception{
        ImgBoardUser user = new ImgBoardUser("r0703028@student.thomasmore.be","test");

        given(userRepository.findImgBoardUserByEmail("r0703028@student.thomasmore.be")).willReturn(user);

        mockMvc.perform(delete("/user/{email}","r0703028@student.thomasmore.be")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenNoReview_whenDeleteReview_thenStatusNotFound() throws Exception{
        given(userRepository.findImgBoardUserByEmail("r0703028@student.thomasmore.be")).willReturn(null);

        mockMvc.perform(delete("/user/{email}","r0703028@student.thomasmore.be")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
