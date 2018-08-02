package pl.jstk.service;

import java.util.List;

import pl.jstk.to.BookTo;

public interface BookService {
	/**
	 * Returns whole collection of book in a list of transfer objects
	 * @return
	 */
    List<BookTo> findAllBooks();
    /**
     * Returns a book transfer object based on its title
     * @param title
     * - book's title
     * @return
     */
    List<BookTo> findBooksByTitle(String title);
    /**
     * Returns a book transfer object based on its author
     * @param author
     *  - book's author
     * @return
     */
    List<BookTo> findBooksByAuthor(String author);
    /**
     * Returns a book transfer object based on its id
     * @param id
     *  - book's id
     * @return
     * @throws Exception
     */
    BookTo findBookById(Long id) throws Exception;
    /**
     * Adds a new book to the database
     * @param book
     *  - book to be added
     * @return
     */
    BookTo saveBook(BookTo book);
    /**
     * Deletes a book from the collection in the database based on its id
     * @param id
     *  - id of book to be deleted
     */
    void deleteBook(Long id);
    /**
     * Returns a list of books based on their authors and/or titles
     * @param authors
     *  - authors of books that will be searched for
     * @param title
     *  - titles of books that will be searched for
     * @return
     */
	List<BookTo> findBookByAuthorAndTitle(String authors, String title);
	/**
	 * Finds the most recently added book to the collection in the database
	 * @return
	 */
	BookTo findLastBookInDatabase();
}
