package fr.epsi.book.dal;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;
import fr.epsi.book.domain.Contact.Type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BookDAO implements IDAO<Book, Long> {
	
	private static final String INSERT_QUERY = "INSERT INTO book (id, code) values (?,?)";
	private static final String FIND_BY_ID_QUERY = "SELECT * FROM book WHERE id=?";
	private static final String FIND_ALL_QUERY = "SELECT * FROM book";
	private static final String UPDATE_QUERY = "UPDATE book SET code=? WHERE id=?";
	private static final String REMOVE_QUERY = "DELETE FROM book WHERE id = ?";
	private static final String REMOVE_ALL_QUERY = "DELETE FROM book WHERE 1";

	public void create( Book b ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( INSERT_QUERY);
		st.setString( 1, b.getId() );
		st.setString( 2, b.getCode() );
		try {
			st.executeUpdate();
		} catch (Exception e) {
			System.err.println("Le book avec l'id : " + b.getId() +" est déja présent dans la base de données");
		}
		st.close();
		connection.close();
		ContactDAO monContactDao = new ContactDAO();
		for (HashMap.Entry<String, Contact> entry : b.getContacts().entrySet())
		{
			try {
				monContactDao.create(entry.getValue(), b.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Book findById( String idBook ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_BY_ID_QUERY );
		st.setString(1, idBook);
		ResultSet rs = st.executeQuery();
		Book book = null;
		
		if ( rs.next() ) {
			book = new Book();
			book.setId(rs.getString("id"));
			book.setCode(rs.getString("code"));
		}
		rs.close();
		st.close();
		connection.close();
		
		ContactDAO monContactDao = new ContactDAO();
		List<Contact> contacts = monContactDao.findAllByCodeBook(book);
		for (Contact contact : contacts) {
			book.addContact(contact);
		}
		return book;
	}
	
	@Override
	public List<Book> findAll() throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( FIND_ALL_QUERY );
		ResultSet rs = st.executeQuery();
		List<Book> book = new ArrayList<>();
		Book unBook = new Book();
		ContactDAO monContactDao = new ContactDAO();
		
		while ( rs.next() ) {			
			unBook.setId(rs.getString("id"));
			unBook.setCode(rs.getString("code"));
			List<Contact> mesContacts = monContactDao.findAllByCodeBook(unBook);
			for(Contact unContact : mesContacts) {
				unBook.addContact(unContact);
			}
			book.add(unBook);
		}
		rs.close();
		st.close();
		connection.close();
		return book;
	}
	
	@Override
	public Book update( Book b ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( UPDATE_QUERY);
		st.setString( 1, b.getCode() );
		st.setString( 2, b.getId() );
		st.executeUpdate();
		st.close();
		connection.close();
		return findById( b.getId() );
	}
	
	@Override
	public void remove( Book b ) throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_QUERY);
		st.setString( 1, b.getId() );
		st.executeUpdate();
		st.close();
		connection.close();
	}
	
	public void removeAll() throws SQLException {
		Connection connection = PersistenceManager.getConnection();
		PreparedStatement st = connection.prepareStatement( REMOVE_ALL_QUERY);
		st.executeUpdate();
		st.close();
		connection.close();
	}
}
