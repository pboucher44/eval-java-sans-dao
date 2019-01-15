package fr.epsi.book;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {
	
	private static final String BOOK_BKP_DIR = "./resources/backup/";
	
	private static final Scanner sc = new Scanner( System.in );
	private static Book book = new Book();
	
	public static void main( String... args ) {
		dspMainMenu();
	}
	
	public static Contact.Type getTypeFromKeyboard() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "*******Choix type de contact *******" );
			System.out.println( "* 1 - Pero                         *" );
			System.out.println( "* 2 - Pro                          *" );
			System.out.println( "************************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt() - 1;
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 0 != response && 1 != response );
		return Contact.Type.values()[response];
	}
	
	public static void addContact() {
		System.out.println( "**************************************" );
		System.out.println( "**********Ajout d'un contact**********" );
		Contact contact = new Contact();
		System.out.print( "Entrer le nom :" );
		contact.setName( sc.nextLine() );
		System.out.print( "Entrer l'email :" );
		contact.setEmail( sc.nextLine() );
		System.out.print( "Entrer le téléphone :" );
		contact.setPhone( sc.nextLine() );
		contact.setType( getTypeFromKeyboard() );
		book.addContact( contact );
		System.out.println( "Nouveau contact ajouté ..." );
	}
	
	public static void editContact() {
		System.out.println( "*********************************************" );
		System.out.println( "**********Modification d'un contact**********" );
		dspContacts( false );
		System.out.print( "Entrer l'identifiant du contact : " );
		String id = sc.nextLine();
		Contact contact = book.getContacts().get( id );
		if ( null == contact ) {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		} else {
			System.out
					.print( "Entrer le nom ('" + contact.getName() + "'; laisser vide pour ne pas mettre à jour) : " );
			String name = sc.nextLine();
			if ( !name.isEmpty() ) {
				contact.setName( name );
			}
			System.out.print( "Entrer l'email ('" + contact
					.getEmail() + "'; laisser vide pour ne pas mettre à jour) : " );
			String email = sc.nextLine();
			if ( !email.isEmpty() ) {
				contact.setEmail( email );
			}
			System.out.print( "Entrer le téléphone ('" + contact
					.getPhone() + "'; laisser vide pour ne pas mettre à jour) : " );
			String phone = sc.nextLine();
			if ( !phone.isEmpty() ) {
				contact.setPhone( phone );
			}
			System.out.println( "Le contact a bien été modifié ..." );
		}
	}
	
	public static void deleteContact() {
		System.out.println( "*********************************************" );
		System.out.println( "***********Suppression d'un contact**********" );
		dspContacts( false );
		System.out.print( "Entrer l'identifiant du contact : " );
		String id = sc.nextLine();
		Contact contact = book.getContacts().remove( id );
		if ( null == contact ) {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		} else {
			System.out.println( "Le contact a bien été supprimé ..." );
		}
	}
	
	public static void sort() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "*******Choix du critère*******" );
			System.out.println( "* 1 - Nom     **              *" );
			System.out.println( "* 2 - Email **                *" );
			System.out.println( "*******************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt();
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 0 >= response || response > 2 );
		Map<String, Contact> contacts = book.getContacts();
		switch ( response ) {
			case 1:
				contacts.entrySet().stream()
						.sorted( ( e1, e2 ) -> e1.getValue().getName().compareToIgnoreCase( e2.getValue().getName() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
			case 2:
				
				contacts.entrySet().stream().sorted( ( e1, e2 ) -> e1.getValue().getEmail()
																	 .compareToIgnoreCase( e2.getValue().getEmail() ) )
						.forEach( e -> dspContact( e.getValue() ) );
				break;
		}
	}
	
	public static void searchContactsByName() {
		
		System.out.println( "*******************************************************************" );
		System.out.println( "************Recherche de contacts sur le nom ou l'email************" );
		System.out.println( "*******************************************************************" );
		System.out.print( "*Mot clé (1 seul) : " );
		String word = sc.nextLine();
		Map<String, Contact> subSet = book.getContacts().entrySet().stream()
										  .filter( entry -> entry.getValue().getName().contains( word ) || entry
												  .getValue().getEmail().contains( word ) )
										  .collect( HashMap::new, ( newMap, entry ) -> newMap
												  .put( entry.getKey(), entry.getValue() ), Map::putAll );
		
		if ( subSet.size() > 0 ) {
			System.out.println( subSet.size() + " contact(s) trouvé(s) : " );
			subSet.entrySet().forEach( entry -> dspContact( entry.getValue() ) );
		} else {
			System.out.println( "Aucun contact trouvé avec cet identifiant ..." );
		}
	}
	
	public static void dspContact( Contact contact ) {
		System.out.println( contact.getId() + "\t\t\t\t" + contact.getName() + "\t\t\t\t" + contact
				.getEmail() + "\t\t\t\t" + contact.getPhone() + "\t\t\t\t" + contact.getType() );
	}
	
	public static void dspContacts( boolean dspHeader ) {
		if ( dspHeader ) {
			System.out.println( "**************************************" );
			System.out.println( "********Liste de vos contacts*********" );
		}
		for ( Map.Entry<String, Contact> entry : book.getContacts().entrySet() ) {
			dspContact( entry.getValue() );
		}
		System.out.println( "**************************************" );
	}
	
	public static void dspMainMenu() {
		int response;
		boolean first = true;
		do {
			if ( !first ) {
				System.out.println( "***********************************************" );
				System.out.println( "* Mauvais choix, merci de recommencer !       *" );
				System.out.println( "***********************************************" );
			}
			System.out.println( "**************************************" );
			System.out.println( "*****************Menu*****************" );
			System.out.println( "* 1 - Ajouter un contact             *" );
			System.out.println( "* 2 - Modifier un contact            *" );
			System.out.println( "* 3 - Supprimer un contact           *" );
			System.out.println( "* 4 - Lister les contacts            *" );
			System.out.println( "* 5 - Rechercher un contact          *" );
			System.out.println( "* 6 - Trier les contacts             *" );
			System.out.println( "* 7 - Sauvegarder                    *" );
			System.out.println( "* 8 - Restaurer                      *" );
			System.out.println( "* 9 - Export des contacts            *" );
			System.out.println( "* 10 - Quitter                       *" );
			System.out.println( "**************************************" );
			System.out.print( "*Votre choix : " );
			try {
				response = sc.nextInt();
			} catch ( InputMismatchException e ) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while ( 1 > response || 10 < response );
		switch ( response ) {
			case 1:
				addContact();
				dspMainMenu();
				break;
			case 2:
				editContact();
				dspMainMenu();
				break;
			case 3:
				deleteContact();
				dspMainMenu();
				break;
			case 4:
				dspContacts( true );
				dspMainMenu();
				break;
			case 5:
				searchContactsByName();
				dspMainMenu();
				break;
			case 6:
				sort();
				dspMainMenu();
				break;
			case 7:
				storeContacts();
				dspMainMenu();
				break;
			case 8:
				restoreContacts();
				dspMainMenu();
				break;
			case 9:
				exportContacts();
				dspMainMenu();
				break;
		}
	}
	
	private static void storeContacts() {
		
		Path path = Paths.get( BOOK_BKP_DIR );
		if ( !Files.isDirectory( path ) ) {
			try {
				Files.createDirectory( path );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		String backupFileName = new SimpleDateFormat( "yyyy-MM-dd-hh-mm-ss" ).format( new Date() ) + ".ser";
		try ( ObjectOutputStream oos = new ObjectOutputStream( Files
				.newOutputStream( Paths.get( BOOK_BKP_DIR + backupFileName ) ) ) ) {
			oos.writeObject( book );
			System.out.println( "Sauvegarde terminée : fichier " + backupFileName );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	private static void restoreContacts() {
		int response;
		List<File> listOfSave = new ArrayList<>();
		
		try{
			File dir = new File(BOOK_BKP_DIR);
			File[] listOfFiles = dir.listFiles();
			System.out.println("**************************************");
			for (int i = 0; i < listOfFiles.length; i++) {
				  if (listOfFiles[i].isFile()) {
				    listOfSave.add(listOfFiles[i]);
				    System.out.println("* "+listOfSave.size()+" - "+ listOfFiles[i].getName());
				  }
				}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("* "+((int)listOfSave.size()+1)+" - retour");
		
		try {
			response = sc.nextInt();
		} catch ( InputMismatchException e ) {
			response = -1;
		} finally {
			sc.nextLine();
		}
		
		System.out.println(response);
		
		try ( DirectoryStream<Path> ds = Files.newDirectoryStream( Paths.get( BOOK_BKP_DIR ), "*.ser" ) ) {
			System.out.println(ds);
			for ( Path path : ds ) {
				System.out.println(path);
				System.out.println( "Restauration du fichier : " + path.getFileName() );
				try ( ObjectInputStream ois = new ObjectInputStream( Files.newInputStream( path ) ) ) {
					book = ( Book ) ois.readObject();
					System.out.println( "Restauration terminée : fichier " + path.getFileName() );
					break;
				} catch ( ClassNotFoundException e ) {
					e.printStackTrace();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
			//ds.forEach( path -> System.out.println( path.getFileName() ) );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
	
	private static void exportContacts() {
		
		try {
			JAXBContext context = JAXBContext.newInstance( Book.class );
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			marshaller.marshal( book, System.out );
			marshaller.marshal( book, System.out );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
	}
}
