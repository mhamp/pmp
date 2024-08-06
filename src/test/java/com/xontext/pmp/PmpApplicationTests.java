package com.xontext.pmp;

import com.xontext.pmp.model.User;
import com.xontext.pmp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PmpApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	@Transactional
	public void testCreateAndFindUser() {
		// Create a new user
		User user = new User();
		user.setUsername("testuser");
		user.setEmail("testuser@example.com");
		user.setPassword("password");

		userRepository.save(user);

		// Find the user by username
		List<User> users = userRepository.findAll();
		User foundUser = users.get(0);

		// Verify the user
		assertNotNull(foundUser);
		assertEquals("testuser", foundUser.getUsername());

	}

}
