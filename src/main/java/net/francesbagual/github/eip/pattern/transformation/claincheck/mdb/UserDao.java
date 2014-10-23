package net.francesbagual.github.eip.pattern.transformation.claincheck.mdb;

import java.util.Map;
import java.util.Random;

import org.apache.commons.collections4.map.HashedMap;

public class UserDao {
	
	private UserDao() {}
	private static UserDao instance = new UserDao();
	
	public static UserDao getInstance() {return instance;}
	
	private Map<Long, User> storage = new HashedMap<>();

	User getUser(Long id) {
		return storage.get(id);
	}

	public Long storeUser(User user) {
		Long id = new Random().nextLong();
		storage.put(id, user);
		return id;
	}
}
