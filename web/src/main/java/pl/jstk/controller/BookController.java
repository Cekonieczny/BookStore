package pl.jstk.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.jstk.constants.ViewNames;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

@Controller
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class BookController {

	@Autowired
	BookService bookService;

	@GetMapping(value = "/books")
	public String books(Model model) {
		model.addAttribute("bookList", bookService.findAllBooks());
		return ViewNames.BOOKS;
	}

	@GetMapping(value = "/books/book")
	public String book(@RequestParam Long id, Model model) throws Exception {
		model.addAttribute("book", bookService.findBookById(id));
		return ViewNames.BOOK;
	}
	
	@GetMapping(value = "/books/add")
	public String showAddBookView(Model model) throws Exception {
		BookTo newBook = new BookTo();
		model.addAttribute("newBook", newBook);
		return ViewNames.ADD_BOOK;
	}

	@GetMapping(value = "/books/find")
	public String showFindBookView(Model model) throws Exception {
		BookTo foundBook = new BookTo();
		model.addAttribute("foundBook", foundBook);
		return ViewNames.FIND_BOOK;
	}
	
	@Secured(value = "ROLE_ADMIN") 
	@GetMapping(value = "/books/delete/book")
	public String deleteBook(@RequestParam long id, Model model) throws Exception {
		model.addAttribute("book", bookService.findBookById(id));
		bookService.deleteBook(id);
		return ViewNames.DELETED_BOOK;
	}

	@PostMapping(value = "/greeting")
	public String addBook(@ModelAttribute("newBook") BookTo newBook, Model model) throws Exception {
		model.addAttribute("book", bookService.saveBook(newBook));
		return ViewNames.BOOK;
	}

	@PostMapping(value = "/books/find/search")
	public String findBook(@ModelAttribute("foundBook") BookTo foundBook, Model model) throws Exception {
		model.addAttribute("bookList",
				bookService.findBookByAuthorAndTitle(foundBook.getAuthors(), foundBook.getTitle()));
		return ViewNames.FOUND_BOOKS;
	}	
	

}
