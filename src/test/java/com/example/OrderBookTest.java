package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class OrderBookTest {

    private OrderBook orderBook;

    @BeforeEach
    public void setUp() {
        orderBook = new OrderBook();
    }

    @Test
    @DisplayName("Given an Order, add it to the OrderBook")
    public void testAddOrder() {
        Order order1 = new Order(1, 100.0, 'B', 1);
        Order order2 = new Order(2, 100.0, 'O', 1);

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);

        List<Order> bids = orderBook.getOrdersForSide('B');
        List<Order> offers = orderBook.getOrdersForSide('O');

        assertEquals(1, bids.size());
        assertEquals(1, offers.size());

        assertEquals(order1, bids.get(0));
        assertEquals(order2, offers.get(0));
    }

    @Test
    @DisplayName("Given an order id, remove an Order from the OrderBook")
    public void testRemoveOrder() {
        Order order1 = new Order(1, 100.0, 'B', 1);
        Order order2 = new Order(2, 200.0, 'O', 1);

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);

        orderBook.removeOrder(1);

        List<Order> bids = orderBook.getOrdersForSide('B');
        List<Order> offers = orderBook.getOrdersForSide('O');

        assertEquals(0, bids.size());
        assertEquals(1, offers.size());
        assertEquals(order2, offers.get(0));

        assertNull(orderBook.getPriceForLevelBySide('B', 1).orElse(null));
    }

    @Test
    @DisplayName("Given an order id and a new size, modify an existing order in the book to use the new size")
    public void testModifyOrderSize() {
        Order order1 = new Order(1, 100.0, 'B', 1);

        orderBook.addOrder(order1);

        assertEquals(1, orderBook.getSizeAvailableForLevel('B', 1));

        orderBook.modifyOrderSize(1, 1111);

        assertEquals(1111, orderBook.getSizeAvailableForLevel('B', 1));
    }

    // We use ID for time priority
    @Test
    @DisplayName("Size modifications do not affect time priority")
    public void testSizeModificationsDoNotAffectTimePriority() {
        Order order1 = new Order(1, 100.0, 'B', 10);
        Order order2 = new Order(2, 100.0, 'B', 20);

        orderBook.addOrder(order2);
        orderBook.addOrder(order1);

        List<Order> bids = orderBook.getOrdersForSide('B');
        assertEquals(2, bids.size());
        assertEquals(order1, bids.get(0));
        assertEquals(order2, bids.get(1));

        orderBook.modifyOrderSize(1, 5);

        bids = orderBook.getOrdersForSide('B');
        assertEquals(2, bids.size());
        assertEquals(order1, bids.get(0));
        assertEquals(order2, bids.get(1));

        orderBook.modifyOrderSize(1, 25);

        bids = orderBook.getOrdersForSide('B');
        assertEquals(2, bids.size());
        assertEquals(order1, bids.get(0));
        assertEquals(order2, bids.get(1));
    }

    @Test
    @DisplayName("Given a side and a level (an integer value >0) return the price for that level")
    public void testGetPriceForLevelBySide() {
        Order order1 = new Order(1, 100.0, 'B', 1);
        Order order2 = new Order(2, 200.0, 'B', 1);
        Order order3 = new Order(3, 100.0, 'O', 1);
        Order order4 = new Order(4, 200.0, 'O', 1);
        Order order5 = new Order(5, 100.0, 'O', 1);

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);
        orderBook.addOrder(order4);
        orderBook.addOrder(order5);

        Optional<Double> priceB = orderBook.getPriceForLevelBySide('B', 2);
        assertEquals(100.0, priceB.get());

        Optional<Double> priceO = orderBook.getPriceForLevelBySide('O', 1);
        assertEquals(100.0, priceO.get());
    }

    @Test
    @DisplayName("Given a side and a level return the total size available for that level")
    public void testGetSizeAvailableForLevel() {
        Order order1 = new Order(1, 100.0, 'B', 10);
        Order order2 = new Order(2, 200.0, 'B', 20);
        Order order3 = new Order(3, 200.0, 'B', 30);

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);

        assertEquals(50, orderBook.getSizeAvailableForLevel('B', 1));
        assertEquals(10, orderBook.getSizeAvailableForLevel('B', 2));
    }

    @Test
    @DisplayName("Given a side return all the orders from that side of the book, in level and time-order")
    public void testGetOrdersForSideInLevelAndTimeOrder() {
        Order order1 = new Order(1, 100.0, 'B', 5);
        Order order2 = new Order(2, 110.0, 'B', 3); // highest bid
        Order order3 = new Order(3, 90, 'B', 2);

        Order order4 = new Order(4, 100.0, 'O', 2); //lower offer
        Order order5 = new Order(5, 100.0, 'O', 4); //lower offer
        Order order6 = new Order(6, 120.0, 'O', 1);

        orderBook.addOrder(order1);
        orderBook.addOrder(order2);
        orderBook.addOrder(order3);

        orderBook.addOrder(order5);
        orderBook.addOrder(order4);
        orderBook.addOrder(order6);

        List<Order> bids = orderBook.getOrdersForSide('B');

        assertEquals(3, bids.size());
        assertEquals(order2, bids.get(0));
        assertEquals(order1, bids.get(1));
        assertEquals(order3, bids.get(2));

        List<Order> offers = orderBook.getOrdersForSide('O');

        assertEquals(3, offers.size());
        assertEquals(order4, offers.get(0));
        assertEquals(order5, offers.get(1));
        assertEquals(order6, offers.get(2));
    }

}
