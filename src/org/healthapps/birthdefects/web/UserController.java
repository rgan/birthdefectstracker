package org.healthapps.birthdefects.web;

import org.apache.commons.lang.StringUtils;
import org.healthapps.birthdefects.dao.UserDao;
import org.healthapps.birthdefects.model.ErrorMessages;
import org.healthapps.birthdefects.model.User;
import org.healthapps.birthdefects.model.UserValidator;
import org.healthapps.birthdefects.utils.Encryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController extends BaseController {
    private UserDao dao;
    private MessageSource messageSource;
    private Emailer emailer;
    private static final String FROM_EMAIL_ADDRESS = "fromEmailAddress";

    @Autowired
    public UserController(UserDao userDao, MessageSource messageSource, Emailer emailer) {
        this.dao = userDao;
        this.messageSource = messageSource;
        this.emailer = emailer;
    }

    @RequestMapping(value = "/users.do", method = RequestMethod.GET)
    public String setupForm(Model model) {
        model.addAttribute("users", dao.allUsers());
        model.addAttribute("user", new User());
        return "users";
    }

    @RequestMapping(value = "/saveuser.do", method = RequestMethod.POST)
    public void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setupEncrytionKey(request);
        User user = userFromRequest(request);
        ErrorMessages errors = new UserValidator(dao).validate(user, isAdmin());
        if (errors.isEmpty()) {
            user.setPassword(encrypt(user.getPassword()));
            dao.save(user);
            writeToResponse(response, OK, "{}");
        } else {
            writeToResponse(response, BAD_REQUEST, errors.getMessages(messageSource));
        }
    }

    @RequestMapping(value = "/email.do", method = RequestMethod.POST)
    public void sendEmail(
            @RequestParam("id") Long recipientUserId,
            @RequestParam("body") String emailBody,
            @RequestParam("subject") String subject,
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        final WebApplicationContext webAppContext = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        final String fromEmailAddress = webAppContext.getServletContext().getInitParameter(FROM_EMAIL_ADDRESS);
        Emailer.FROM_EMAIL = fromEmailAddress;
        ErrorMessages errors = emailer.validate(emailBody, subject);
        if (errors.isEmpty()) {
            emailer.send(dao.findUserById(recipientUserId).getEmail(), subject, emailBody);
            writeToResponse(response, OK, "{}");
        } else {
            writeToResponse(response, BAD_REQUEST, errors.getMessages(messageSource));
        }
    }

    private User userFromRequest(HttpServletRequest request) {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        final String[] roles = request.getParameterValues("roles") != null ?
                request.getParameterValues("roles") : new String[]{};
        final boolean medicalProfessional = request.getParameter("medicalProfessional") != null ? true : false;
        return new User(username, password, StringUtils.join(roles, ","), medicalProfessional, email);
    }

    public String saveSpringTagsVersion(Model model, @ModelAttribute User user, BindingResult result, SessionStatus sessionStatus) {
        if (isAdmin()) {
            new UserValidator(dao).validate(user, result);
            if (!result.hasErrors()) {
                dao.save(user);
                model.addAttribute("users", dao.allUsers());
                sessionStatus.setComplete();
            }
        }
        return "users";
    }

    @RequestMapping(value = "/deleteuser.do", method = RequestMethod.POST)
    public String delete(Model model, @RequestParam("userIdToDelete") Long userIdToDelete) {
        if (isAdmin()) {
            dao.disableUser(userIdToDelete);
        }
        return setupForm(model);
    }
}
