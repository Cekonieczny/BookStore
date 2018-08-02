package pl.jstk.service.impl;

import java.util.LinkedList;
import java.util.List;
import pl.jstk.entity.BookEntity;
import pl.jstk.mapper.BookMapper;
import pl.jstk.repository.BookRepository;
import pl.jstk.service.BookService;
import pl.jstk.to.BookTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

	private BookRepository bookRepository;

	@Autowired
	public BookServiceImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public List<BookTo> findAllBooks() {
		return BookMapper.map2To(bookRepository.findAll());
	}

	@Override
	public List<BookTo> findBooksByTitle(String title) {
		return BookMapper.map2To(bookRepository.findBookByTitle(title));
	}

	@Override
	public List<BookTo> findBooksByAuthor(String author) {
		return BookMapper.map2To(bookRepository.findBookByAuthor(author));
	}

	@Override
	@Transactional
	public BookTo saveBook(BookTo book) {
		BookEntity entity = BookMapper.map(book);
		entity = bookRepository.save(entity);
		return BookMapper.map(entity);
	}

	@Override
	@Transactional
	public void deleteBook(Long id) {
		bookRepository.deleteById(id);

	}

	@Override
	public BookTo findBookById(Long id) throws Exception {
		if (bookRepository.findById(id).isPresent()) {
			return BookMapper.map(bookRepository.findById(id).get());
		}
		throw new Exception("Book not found");
	}

	@Override
	public List<BookTo> findBookByAuthorAndTitle(String author, String title) {
		List<BookTo> listByTitle = BookMapper.map2To(bookRepository.findBookByTitle(title));
		List<BookTo> listByAuthor = BookMapper.map2To(bookRepository.findBookByAuthor(author));
		
		if (!title.isEmpty() && !author.isEmpty()) {
			List<BookTo> differingBooks = new LinkedList<>();
			for (BookTo bookByTitle : listByTitle) {
				for (BookTo bookByAuthor : listByAuthor) {
					if (!(bookByTitle.getId().equals(bookByAuthor.getId()))) {
						differingBooks.add(bookByAuthor);
					}
				}
			}
			listByTitle.addAll(differingBooks);
		}
		
		if(title.isEmpty()){
			return listByAuthor;
		}
		return listByTitle;
	}
	@Override
	public BookTo findLastBookInDatabase(){
		List<BookEntity> tempList= bookRepository.findAll();
		return BookMapper.map((tempList.get(tempList.size()-1)));	
	}
}
