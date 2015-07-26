package showcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devdeep on 12/19/2014.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    @Autowired
    public CustomUserDetailsService(UserRepository repo) {
        this.repository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        logger.info("username:"+name);
        
        User u = repository.findByUsername(name);
        if (u == null)
            throw new UsernameNotFoundException("User details not found with this username: " + name);
        String username = u.getUsername();
        String password = u.getPassword();
        

        List<GrantedAuthority> authList = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        if (u.getRole().equals("admin"))
            authList = AuthorityUtils
                    .commaSeparatedStringToAuthorityList("ROLE_ADMIN");

        

        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(username, password, authList);

        return user;
    }

    public boolean createUser(String username, String password) {
        User existing = repository.findByUsername(username);
        if(existing!=null)
            return false;
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole("user");
        u.setScore(0);

        repository.save(u);
        return true;
    }

    private List getAuthorities(String role) {
        List authList = new ArrayList();
        authList.add(new SimpleGrantedAuthority("USER"));

        //you can also add different roles here
        //for example, the user is also an admin of the site, then you can add ROLE_ADMIN
        //so that he can view pages that are ROLE_ADMIN specific
        if (role != null && role.trim().length() > 0) {
            if (role.equals("admin")) {
                authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
        }

        return authList;
    }
}
