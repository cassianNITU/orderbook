package com.example;

import java.util.*;

public class OrderBook {

    private final Map<Long, Order> ordersById;
    private final TreeMap<Double, List<Order>> bids;
    private final TreeMap<Double, List<Order>> offers;

    public OrderBook() {
        ordersById = new HashMap<>();
        bids = new TreeMap<>(Collections.reverseOrder());
        offers = new TreeMap<>();
    }

    private TreeMap<Double, List<Order>> getSideMap(char side) {
        return side == 'B' ? bids : offers;
    }

    public void addOrder(Order order) {
        ordersById.put(order.getId(), order);
        getSideMap(order.getSide())
                .computeIfAbsent(order.getPrice(), k -> new ArrayList<>())
                .add(order);
        sortOrdersByTime(getSideMap(order.getSide()), order.getPrice());
    }

    public void removeOrder(long orderId) {
        Order removedOrder = ordersById.remove(orderId);
        if (removedOrder != null) {
            List<Order> orders = getSideMap(removedOrder.getSide()).get(removedOrder.getPrice());
            if (orders != null) {
                orders.removeIf(o -> o.getId() == orderId);
                if (orders.isEmpty()) {
                    getSideMap(removedOrder.getSide()).remove(removedOrder.getPrice());
                }
            }
        }
    }

    public void modifyOrderSize(long orderId, long newSize) {
        Order order = ordersById.get(orderId);
        if (order != null) {
            List<Order> orders = getSideMap(order.getSide()).get(order.getPrice());
            if (orders != null) {
                orders.removeIf(o -> o.getId() == orderId);
                order.setSize(newSize);
                orders.add(order);
                sortOrdersByTime(getSideMap(order.getSide()), order.getPrice());
            }
        }
    }

    // ID = time priority and price = level
    private void sortOrdersByTime(TreeMap<Double, List<Order>> sideMap, double price) {
        List<Order> orders = sideMap.get(price);
        if (orders != null && orders.size() > 1) {
            orders.sort(Comparator.comparingLong(Order::getId));
        }
    }

    public Optional<Double> getPriceForLevelBySide(char side, int level) {
        TreeMap<Double, List<Order>> sideMap = getSideMap(side);
        return sideMap.values().stream()
                .skip(level - 1L)
                .limit(1)
                .flatMap(List::stream)
                .map(Order::getPrice)
                .findFirst();
    }

    public long getSizeAvailableForLevel(char side, int level) {
        TreeMap<Double, List<Order>> sideMap = getSideMap(side);
        return sideMap.values().stream()
                .skip(level - 1L)
                .limit(1)
                .flatMap(List::stream)
                .mapToLong(Order::getSize)
                .sum();
    }

    public List<Order> getOrdersForSide(char side) {
        TreeMap<Double, List<Order>> sideMap = getSideMap(side);
        return sideMap.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public List<Order> getAllOrders() {
        return ordersById.values().stream().toList();
    }
}


