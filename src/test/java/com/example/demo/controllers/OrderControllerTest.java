package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

public class OrderControllerTest {
	private OrderController orderController;
	private OrderRepository orderRepository = mock(OrderRepository.class);
	private UserRepository userRepository = mock(UserRepository.class);
	
	@Before
	public void setup() {
		orderController = new OrderController();
		TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
		TestUtils.injectObjects(orderController, "userRepository", userRepository);
	}
	
	@Test
	public void submit_order_not_found_user() {
		User user = new User(1L, "test_user", null, "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		
		final ResponseEntity<UserOrder> response = orderController.submit("test_user_1");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void submit_order_success() {
		Cart cart = new Cart();
		cart.addItem(new Item(1L, "test_item", new BigDecimal(123), ""));
		User user = new User(1L, "test_user", cart, "testPassword");
		cart.setUser(user);
		UserOrder order = UserOrder.createFromCart(cart);
		
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		when(orderRepository.save(order)).thenReturn(order);
		
		final ResponseEntity<UserOrder> response = orderController.submit("test_user");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		UserOrder rorder = response.getBody();
		assertEquals(user, rorder.getUser());
	}
	
	@Test
	public void get_order_by_user_not_found() {
		User user = new User(1L, "test_user", null, "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		
		final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test_user_1");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void get_order_by_user_success() {
		User user = new User(1L, "test_user", null, "testPassword");
		when(userRepository.findByUsername("test_user")).thenReturn(user);
		
		UserOrder order_1 = new UserOrder();
		order_1.setUser(user);
		order_1.setId(1L);
		order_1.setTotal(new BigDecimal(123));
		order_1.setItems(Arrays.asList(new Item(1L, "test_item", new BigDecimal(123), "")));
		UserOrder order_2 = new UserOrder();
		order_2.setUser(user);
		order_2.setId(2L);
		order_2.setTotal(new BigDecimal(1234));
		order_2.setItems(Arrays.asList(new Item(2L, "test_item_2", new BigDecimal(1234), "")));
		
		List<UserOrder> orders = Arrays.asList(order_1, order_2);
		when(orderRepository.findByUser(user)).thenReturn(orders);
		
		final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test_user");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		List<UserOrder> rorders = response.getBody();
		assertEquals(2, rorders.size());
	}
}
