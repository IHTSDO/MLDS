/**
 * Package to generate email text, and send them.
 * 
 *  How to create a new email for Foo :
 *  - Copy one of the *EmailSender classes to FooEmailSender
 *  - Create new fooEmail.html template in src/main/resources/fooEmail.html
 *  - Create new messages entries in src/main/resources/mail/messages/messages_en.properties etc.
 *  - At a minimum, create foo.title property, and use it in fooEmail.html and FooEmailSender.
 */
package ca.intelliware.ihtsdo.mlds.service.mail;
