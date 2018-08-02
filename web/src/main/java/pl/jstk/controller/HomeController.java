package pl.jstk.controller;

import pl.jstk.constants.ModelConstants;
import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@Autowired
	BookService bookService;
    protected static final String WELCOME = "The newest book in the collection:";

    @GetMapping(value = "/")
    public String welcome(Model model) {
    	model.addAttribute("book", bookService.findLastBookInDatabase());
        model.addAttribute(ModelConstants.MESSAGE, WELCOME);
        return ViewNames.WELCOME;
    }

}
