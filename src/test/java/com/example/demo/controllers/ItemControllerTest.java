package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

public class ItemControllerTest {
	private ItemController itemController;
	private ItemRepository itemRepository = mock(ItemRepository.class);
	
	@Before
	public void setup() {
		itemController = new ItemController();
		TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
	}
	
	@Test
	public void get_items_success() {
		List<Item> items = Arrays.asList(
				new Item(1L, "item 1", new BigDecimal(123), ""),
				new Item(2L, "item 2", new BigDecimal(1234), ""),
				new Item(3L, "item 3", new BigDecimal(12345), "")
		);
		when(itemRepository.findAll()).thenReturn(items);
		
		final ResponseEntity<List<Item>> response = itemController.getItems();
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		List<Item> lst = response.getBody();
		assertEquals(items, lst);
	}
	
	@Test
	public void get_items_by_name_success() {
		List<Item> items = Arrays.asList(
				new Item(1L, "item 1", new BigDecimal(123), ""),
				new Item(2L, "item 2", new BigDecimal(1234), ""),
				new Item(3L, "item 3", new BigDecimal(12345), "")
		);
		when(itemRepository.findByName("item")).thenReturn(items);
		
		final ResponseEntity<List<Item>> response = itemController.getItemsByName("item");
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		List<Item> lst = response.getBody();
		assertEquals(items, lst);
	}
	
	@Test
	public void get_items_by_name_error() {
		List<Item> items = Arrays.asList(
				new Item(1L, "item 1", new BigDecimal(123), ""),
				new Item(2L, "item 2", new BigDecimal(1234), ""),
				new Item(3L, "item 3", new BigDecimal(12345), "")
		);
		when(itemRepository.findByName("item")).thenReturn(items);
		
		final ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
	
	@Test
	public void get_item_by_id_success() {
		Item item = new Item(1L, "item 1", new BigDecimal(123), "");
		
		when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		
		final ResponseEntity<Item> response = itemController.getItemById(item.getId());
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
		
		Item ri = response.getBody();
		assertEquals(item.getName(), ri.getName());
	}
}
