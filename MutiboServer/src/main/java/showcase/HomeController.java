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
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Handles requests for the application home page.
 * 
 * @author Roy Clarkson
 */
@Controller
@RequestMapping("/*")
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    CustomUserDetailsService users;

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
        //Token token = session.getAttribute()
        return new Message(100, "Congratulations!", "You have logged in.");
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

        //return "msg:"+message;
    }

    @RequestMapping(value = "play", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Message play(HttpSession session) {
        logger.info("Accessing protected play resource sid:"+session.getId());
        return new Message(100, "Congratulations!", "Launching play.");
    }



}
