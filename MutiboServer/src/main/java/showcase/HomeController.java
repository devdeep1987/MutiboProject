/*
 * Copyright 2010-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package showcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.token.Token;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Handles requests for the application home page.
 * 
 * @author Roy Clarkson
 */

@RestController

public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    CustomUserDetailsService users;

    @Autowired
    private MovieSetRepository setRepository;

    @Autowired
    private UserRepository userRepository;

    private static String SEC_CONTEXT_ATTR = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		logger.info("Spring Android Showcase");
		return "home";
	}

    @RequestMapping(value = "login", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Message login(HttpSession session) {
        logger.info("Accessing protected resource sid:"+session.getId());
        ArrayList<Long> questionArray = new ArrayList<Long>();
        
        Iterator<MovieSet> iter = setRepository.findAll().iterator();
        while (iter.hasNext()) {
            MovieSet set = iter.next();
            questionArray.add(set.getId().longValue());
        }
        Collections.shuffle(questionArray);
        int questionIndex = 0;
        session.setAttribute("questionIndex",questionIndex);
        session.setAttribute("questionArray",questionArray);
        SecurityContext securityContext = (SecurityContext) session.getAttribute(SEC_CONTEXT_ATTR);
        logger.info("User:"+securityContext.getAuthentication().getName());
        User currentUser = userRepository.findByUsername(securityContext.getAuthentication().getName());
        long highscore = currentUser.getScore();
        String role = currentUser.getRole();
        return new Message(100, role, new Long(highscore).toString());
    }

    @RequestMapping(value = "signup", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Message getSignup() {
        logger.info("Accessing protected resource");
        users.createUser("devdeep","spring");
        return new Message(100, "Congratulations!", "GET signup.");
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody Message signUp(@RequestBody Message message, HttpSession session) {
        logger.info("Accessing protected resource sid:"+session.getId());
        boolean result = users.createUser(message.getSubject(),message.getText());
        if(!result)
            return new Message(0, "Duplicate","Username not available");
        return new Message(100, "Congratulations!", "You have signed up. msg:"+message.toString());

        
    }

    @RequestMapping(value = "play", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody MovieSet play(HttpSession session) {
        int questionIndex = (Integer)session.getAttribute("questionIndex");
        ArrayList<Long> questionArray = (ArrayList<Long>)session.getAttribute("questionArray");
        logger.info("Accessing protected play resource sid:"+session.getId()+" count:"+questionIndex);
        
        MovieSet set = setRepository.findById(questionArray.get(questionIndex));
        questionIndex = (questionIndex+1)%(questionArray.size());
        session.setAttribute("questionIndex",questionIndex);


        
        return set;
    }

    @RequestMapping(value = "updatehighscore", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody Message updateHighScore(@RequestBody Message message, HttpSession session) {
        logger.info("Accessing protected resource sid:"+session.getId());

        logger.info("accessed user:"+/*user.getUsername()*/message.getSubject());
        long highscore = Long.parseLong(message.getText());
        User user = userRepository.findByUsername(message.getSubject());
        user.setScore(highscore);
        userRepository.save(user);
        return new Message(100, "Congratulations!", "Highscore updated");

        
    }

    @RequestMapping(value = "addset", method = RequestMethod.POST)
    @Secured("ROLE_ADMIN")
    public @ResponseBody Message addSet(@RequestBody MovieSet movieSet) {
        logger.info("received set:"+movieSet.getMovie1()+" "+movieSet.getMovie2()+" "+movieSet.getMovie3()+" "+movieSet.getMovie4()+" "+movieSet.getAnswer());
        Iterator<MovieSet> iter = setRepository.findAll().iterator();
        while (iter.hasNext()) {
            MovieSet set = iter.next();
            if(set.getMovie1().equals(movieSet.getMovie1()) && set.getMovie2().equals(movieSet.getMovie2()) && set.getMovie3().equals(movieSet.getMovie3()) && set.getMovie4().equals(movieSet.getMovie4()) && set.getAnswer().equals(movieSet.getAnswer())) {
                logger.info("Duplicate set");
                return new Message(100, "Duplicate!", "Set not added");
            }
        }
        setRepository.save(movieSet);
        return new Message(100, "Congratulations!", "Set added");
    }



}
