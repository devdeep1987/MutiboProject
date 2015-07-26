package showcase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Devdeep on 12/21/2014.
 */
@Entity
public class User {
    @GeneratedValue
    @Id
    private Long id;

    private String username;
    private String password;
    private String role;
    private long score;

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public  void setRole(String role) {
        this.role = role;
    }

    public long getScore() {
        return score;
    }

    public  void setScore(long score) {
        this.score = score;
    }


}
