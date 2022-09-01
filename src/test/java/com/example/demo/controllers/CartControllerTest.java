package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

public class CartControllerTest {
	private CartController cartController;
	private UserRepository userRepository = mock(UserRepository.class);
	private CartRepository cartRepository = mock(CartRepository.class);
	private ItemRepository itemRepository = mock(ItemRepository.class);
	
	@Before
	public void setup() {
		cartController = new CartController();
		TestUtils.injectObjects(cartController, "userRepository", userRepository);
		TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
		TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
	}
	
	@Test
	public void add_to_cart_user_not_found() {
		User user = new User(1L, "test_user", new Cart(), "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		ModifyCartRequest request = new ModifyCartRequest();
		request.setUsername("test_user_1");
		
		final ResponseEntity<Cart> response = cartController.addTocart(request);
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void add_to_cart_item_not_found() {
		User user = new User(1L, "test_user", new Cart(), "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		Item item = new Item(1L, "item 1", new BigDecimal(123), "");
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		
		ModifyCartRequest request = new ModifyCartRequest();
		request.setUsername("test_user");
		request.setItemId(2L);
		
		final ResponseEntity<Cart> response = cartController.addTocart(request);
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void add_to_cart_success() {
		Cart cart = new Cart();
		cart.setId(1L);
		User user = new User(1L, "test_user", cart, "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		Item item = new Item(1L, "item 1", new BigDecimal(123), "");
		when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
		cart.setUser(user);
		List<Item> items = new ArrayList<>();
		items.add(item);
		cart.setItems(items);
		cart.setTotal(new BigDecimal(123));
		
		ModifyCartRequest request = new ModifyCartRequest();
		request.setUsername("test_user");
		request.setItemId(1L);
		request.setQuantity(1);
		
		final ResponseEntity<Cart> response = cartController.addTocart(request);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
	}
}
