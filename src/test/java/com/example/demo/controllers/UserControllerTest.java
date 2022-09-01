package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;


public class UserControllerTest {
	private UserController userController;
	private UserRepository userRepository = mock(UserRepository.class);
	private CartRepository cartRepository = mock(CartRepository.class);
	private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
	
	@Before
	public void setup() {
		userController = new UserController();
		TestUtils.injectObjects(userController, "userRepository", userRepository);
		TestUtils.injectObjects(userController, "cartRepository", cartRepository);
		TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
	}
	
	
	
	@Test
	public void create_user_success() throws Exception {
		when(bCryptPasswordEncoder.encode("admin@123")).thenReturn("hashedPassword");
		
		CreateUserRequest r = new CreateUserRequest();
		r.setUsername("test");
		r.setPassword("admin@123");
		r.setConfirmPassword("admin@123");
		
		final ResponseEntity<User> response = userController.createUser(r);
		
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		User u = response.getBody();
		assertNotNull(u);
		assertEquals(0, u.getId());
		assertEquals("test", u.getUsername());
		assertEquals("hashedPassword", u.getPassword());
	}
	
	@Test
	public void create_user_error() throws Exception {
		CreateUserRequest r = new CreateUserRequest();
		r.setUsername("test");
		r.setPassword("admin@123");
		r.setConfirmPassword("admin@1234");
		
		final ResponseEntity<User> response = userController.createUser(r);
		
		assertNotNull(response);
		assertEquals(400, response.getStatusCodeValue());
	}
	
	@Test
	public void get_user_by_username_success() throws Exception {
		User u = new User();
		u.setUsername("test_user");
		String hashed = bCryptPasswordEncoder.encode("testPassword");
		u.setPassword(hashed);
		when(userRepository.findByUsername(any(String.class))).thenReturn(u);
		
		final ResponseEntity<User> response = userController.findByUserName(u.getUsername());
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		User ru = response.getBody();
		assertNotNull(ru);
		assertEquals(u.getUsername(), ru.getUsername());
	}
	
	@Test
	public void get_user_by_username_error() throws Exception {
		User u = new User();
		u.setUsername("test_user");
		String hashed = bCryptPasswordEncoder.encode("testPassword");
		u.setPassword(hashed);
		when(userRepository.findByUsername(u.getUsername())).thenReturn(u);
		
		final ResponseEntity<User> response = userController.findByUserName("test1");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void get_user_by_id_success() throws Exception {
		User u = new User();
		u.setId(2);
		u.setUsername("test_user_2");
		String hashed = bCryptPasswordEncoder.encode("testPassword");
		u.setPassword(hashed);
		when(userRepository.findById(u.getId())).thenReturn(Optional.of(u));
		
		final ResponseEntity<User> response = userController.findById(u.getId());
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		User ru = response.getBody();
		assertNotNull(ru);
		assertEquals(u.getUsername(), ru.getUsername());
	}
	
	@Test
	public void get_user_by_id_error() throws Exception {
		User u = new User();
		u.setId(2);
		u.setUsername("test_user_2");
		String hashed = bCryptPasswordEncoder.encode("testPassword");
		u.setPassword(hashed);
		when(userRepository.findById(5L)).thenReturn(Optional.of(u));
		
		final ResponseEntity<User> response = userController.findById(u.getId());
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
}
