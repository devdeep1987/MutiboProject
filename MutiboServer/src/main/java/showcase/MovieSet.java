package showcase;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Devdeep on 1/24/2015.
 */
@Entity
public class MovieSet {
    @GeneratedValue
    @Id
    private Long id;

    private String movie1, movie2, movie3, movie4;
    private String answer;

    public MovieSet(String m1, String m2, String m3, String m4, String ans) {
        movie1 = m1;
        movie2 = m2;
        movie3 = m3;
        movie4 = m4;
        answer = ans;
    }

}
