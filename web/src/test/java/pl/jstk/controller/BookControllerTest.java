package pl.jstk.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.LinkedList;
import java.util.List;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.jstk.constants.ViewNames;
import pl.jstk.enumerations.BookStatus;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class BookControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	BookController bookController;

	@Mock
	BookService bookService;

	

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(bookService);
		Mockito.reset(bookService);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
		ReflectionTestUtils.setField(bookController, "bookService", bookService);
	}
	

	@Test
	public void testBooksPage() throws Exception {
		// given when
		List<BookTo> listOfBooks = generateListOfBook();
		Mockito.when(bookService.findAllBooks()).thenReturn(listOfBooks);
		ResultActions resultActions = mockMvc.perform(get("/books"));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name("books"))
					.andExpect(model().attribute("bookList", listOfBooks))
					.andExpect(content().string(containsString("")));
	}
	
	@Test
	public void testBookDetailPage() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();
		Mockito.when(bookService.findBookById(bookTo.getId())).thenReturn(bookTo);
		ResultActions resultActions = mockMvc.perform(get("/books/book?id=10"));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name(ViewNames.BOOK))
					.andExpect(model().attribute("book", bookTo))
					.andExpect(content().string(containsString("")));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testAddBooks() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();
		String author = bookTo.getAuthors();
		String title = bookTo.getTitle();
		BookStatus status = bookTo.getStatus();
		Mockito.when(bookService.saveBook(Mockito.any())).thenReturn(bookTo);
		ResultActions resultActions = mockMvc
				.perform(post("/greeting").with(csrf()).flashAttr("book", bookTo));
						
		// then
		resultActions.andExpect(status().isOk()).andExpect(view().name(ViewNames.BOOK))
				.andExpect(model().attribute("book", hasProperty("title", is(title))))
				.andExpect(model().attribute("book", hasProperty("authors", is(author))))
				.andExpect(model().attribute("book", hasProperty("status", is(status))))
				.andExpect(content().string(containsString("")));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testShowAddBooksPageWithAuthentication() throws Exception {
		// given when
		ResultActions resultActions = mockMvc.perform(get("/books/add"));
				
		// then
		resultActions.andExpect(status().isOk()).andExpect(view().name(ViewNames.ADD_BOOK))
				.andExpect(model().attribute("book", nullValue()))
				.andExpect(content().string(containsString("")));
	}

	@Test
	public void testShowAddBooksPageWithoutAuthentication() throws Exception {
		// given when
		ResultActions resultActions = mockMvc.perform(get("/books/add"));
				
		// then
		resultActions.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("http://localhost/login"))
					.andExpect(status().isFound());
	}

	@Test
	public void testShowFindBooksPage() throws Exception {
		// given when
		ResultActions resultActions = mockMvc.perform(get("/books/find"));
		// then
		resultActions.andExpect(status().isOk()).andExpect(view().name(ViewNames.FIND_BOOK))
				.andExpect(model().attribute("book", nullValue())).andExpect(content().string(containsString("")));
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	public void testDeleteBookWithAutorization() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();
		String title = bookTo.getTitle();
		String author = bookTo.getAuthors();
		BookStatus status = bookTo.getStatus();

		Mockito.when(bookService.findBookById(bookTo.getId())).thenReturn(bookTo);
		Mockito.doNothing().when(bookService).deleteBook(bookTo.getId());
		ResultActions resultActions = mockMvc.perform(get("/books/delete/book?id=10"));
				
		// then
		Mockito.verify(bookService,Mockito.times(1)).deleteBook(bookTo.getId());
		Mockito.verify(bookService,Mockito.times(1)).findBookById(bookTo.getId());
		Mockito.verifyNoMoreInteractions(bookService);
		resultActions.andExpect(status().isOk()).andExpect(view().name(ViewNames.DELETED_BOOK))
				.andExpect(model().attribute("book", hasProperty("title", is(title))))
				.andExpect(model().attribute("book", hasProperty("authors", is(author))))
				.andExpect(model().attribute("book", hasProperty("status", is(status))))
				.andExpect(content().string(containsString("")));
	}

	@Test
	@WithMockUser(username = "user", roles = { "USER" })
	public void testDeleteBookWithoutAuthorization() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();
		
		ResultActions resultActions = mockMvc.perform(get("/books/delete/book?id=10"));
		// then
		Mockito.verify(bookService,Mockito.never()).deleteBook(bookTo.getId());
		Mockito.verify(bookService,Mockito.never()).findBookById(bookTo.getId());
		Mockito.verifyNoMoreInteractions(bookService);
		resultActions.andExpect(view().name(ViewNames.ERROR_403));
		
	}
	
	@Test
	public void testDeleteBookWithoutAuthentication() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();

		Mockito.when(bookService.findBookById(bookTo.getId())).thenReturn(bookTo);
		Mockito.doNothing().when(bookService).deleteBook(bookTo.getId());
		ResultActions resultActions = mockMvc.perform(get("/books/delete/book?id=10"));
		// then
		
		resultActions.andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("http://localhost/login"))
					.andExpect(status().isFound());
	}
	
	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testSearchBooks() throws Exception {
		// given when
		BookTo bookTo = generateBookTo();
		List<BookTo> listOfBooks = generateListOfBook();
		
		Mockito.when(bookService.findBookByAuthorAndTitle(Mockito.any(), Mockito.any())).thenReturn(listOfBooks);
		ResultActions resultActions = mockMvc
				.perform(post("/books/find/search").with(csrf()).flashAttr("book", bookTo));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name(ViewNames.FOUND_BOOKS))
					.andExpect(model().attribute("bookList", listOfBooks))
					.andExpect(content().string(containsString("")));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testSearchBooksWithTitleOnly() throws Exception {
		// given when
		BookTo bookTo = new BookTo();
		bookTo.setTitle("title");
		List<BookTo> listOfBooks = generateListOfBook();
		
		Mockito.when(bookService.findBookByAuthorAndTitle(Mockito.any(), Mockito.any())).thenReturn(listOfBooks);
		ResultActions resultActions = mockMvc
				.perform(post("/books/find/search").with(csrf()).flashAttr("book", bookTo));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name(ViewNames.FOUND_BOOKS))
					.andExpect(model().attribute("bookList", listOfBooks))
					.andExpect(content().string(containsString("")));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testSearchBooksWithAuthorsOnly() throws Exception {
		// given when
		BookTo bookTo = new BookTo();
		bookTo.setAuthors("authors");
		List<BookTo> listOfBooks = generateListOfBook();
		
		Mockito.when(bookService.findBookByAuthorAndTitle(Mockito.any(), Mockito.any())).thenReturn(listOfBooks);
		ResultActions resultActions = mockMvc
				.perform(post("/books/find/search").with(csrf()).flashAttr("book", bookTo));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name(ViewNames.FOUND_BOOKS))
					.andExpect(model().attribute("bookList", listOfBooks))
					.andExpect(content().string(containsString("")));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = { "USER", "ADMIN" })
	public void testSearchBooksWithoutGivenParameters() throws Exception {
		// given when
		BookTo bookTo = new BookTo();
		List<BookTo> listOfBooks = generateListOfBook();
		
		Mockito.when(bookService.findBookByAuthorAndTitle(Mockito.any(), Mockito.any())).thenReturn(listOfBooks);
		ResultActions resultActions = mockMvc
				.perform(post("/books/find/search").with(csrf()).flashAttr("book", bookTo));
		// then
		resultActions.andExpect(status().isOk())
					.andExpect(view().name(ViewNames.FOUND_BOOKS))
					.andExpect(model().attribute("bookList", listOfBooks))
					.andExpect(content().string(containsString("")));
	}

	private BookTo generateBookTo() {
		return new BookTo(10L, "TEST_TITLE1", "TEST_AUTHORS1", BookStatus.FREE);
	}

	private List<BookTo> generateListOfBook() {
		List<BookTo> listOfBooks = new LinkedList<>();
		listOfBooks.add(new BookTo(10L, "TEST_TITLE1", "TEST_AUTHORS1", BookStatus.FREE));
		listOfBooks.add(new BookTo(120L, "TEST_TITLE2", "TEST_AUTHORS2", BookStatus.LOAN));
		listOfBooks.add(new BookTo(130L, "TEST_TITLE3", "TEST_AUTHORS3", BookStatus.MISSING));
		listOfBooks.add(new BookTo(104L, "TEST_TITLE4", "TEST_AUTHORS4", BookStatus.FREE));
		listOfBooks.add(new BookTo(105L, "TEST_TITLE5", "TEST_AUTHORS5", BookStatus.FREE));
		listOfBooks.add(new BookTo(106L, "TEST_TITLE6", "TEST_AUTHORS6", BookStatus.FREE));
		listOfBooks.add(new BookTo(1037L, "TEST_TITLE7", "TEST_AUTHORS7", BookStatus.FREE));
		listOfBooks.add(new BookTo(1033L, "TEST_TITLE8", "TEST_AUTHORS8", BookStatus.FREE));
		listOfBooks.add(new BookTo(1057L, "TEST_TITLE9", "TEST_AUTHORS9", BookStatus.FREE));
		listOfBooks.add(new BookTo(1023L, "TEST_TITLE10", "TEST_AUTHORS10", BookStatus.FREE));
		return listOfBooks;
	}

}
