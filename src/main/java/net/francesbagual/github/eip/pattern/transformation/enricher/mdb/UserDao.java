package net.francesbagual.github.eip.pattern.transformation.enricher.mdb;

public class UserDao {

 User getUser(Long id){
	 return new User(id, "John", 25);
 }
}
