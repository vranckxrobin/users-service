package fact.it.users.repository;

import fact.it.users.model.ImgBoardUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ImgBoardUser, Integer> {
    @Query("select u from ImgBoardUser u WHERE u.email = ?1")
    ImgBoardUser findImgBoardUserByEmail(String email);
}
