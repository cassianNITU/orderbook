
Part B
Please suggest (but do not implement) modifications or additions to the Order and/or OrderBook classes
to make them better suited to support real-life, latency-sensitive trading operations.

Order:

- order should be immutable or use record
- price should be big decimal for precision in financial calculations (also size depending on use case)
- should have a timestamp field as for my solution I use Ids based on the convention that they are given incremental in the right order
- filled field - where based on size can be partially filled, and the remaining size is still available in the order book
- a field for canceling the order with logic in order book to do that

OrderBook:

- implement an order execution logic that efficiently matches incoming orders with existing orders
  in the order book based on price, time priority and size consider using Priority Queues or other structures.

- methods that modify the order book data structures (addOrder, removeOrder, and modifyOrderSize) can use a locking mechanism
  if the application allows concurrent access to the OrderBook from multiple threads

- persist the order book state to a database to maintain order book state in case of system restarts or failures