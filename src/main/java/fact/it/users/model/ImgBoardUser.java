package fact.it.users.model;


import javax.persistence.*;

@Entity
public class ImgBoardUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String email;

    private String password;

    public ImgBoardUser(){

    }

    public ImgBoardUser(String email, String password) {
        setEmail(email);
        setPassword(password);
    }


    public void setEmail(String email) {
        this.email=email;
    }
    public void setPassword(String password) {
        this.password=password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
