package fr.epsi.book;

import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class App {
	
	private static final Scanner sc = new Scanner( System.in );
	private static final Book book = new Book();
	
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
			System.out.println( subSet.size()+" contact(s) trouvé(s) : " );
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
			System.out.println( "* 7 - Quitter                        *" );
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
		} while ( 1 > response || 7 < response );
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
		}
	}
}
