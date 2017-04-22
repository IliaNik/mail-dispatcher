package com.mail.dispatcher.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author IliaNik on 22.04.2017.
 */
@Controller
public class MailController {

    @RequestMapping("/")
    public String mail(Model model) {
        return "mail";
    }
}
