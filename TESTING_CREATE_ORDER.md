# Testing the Create Order Workflow

## Quick Testing Guide

### Prerequisites
1. Application is running
2. Database is populated with:
   - At least one user
   - At least one product with inventory

### Test Scenario 1: Successful Order Creation

**Request:**
```json
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

**Expected Result:**
- HTTP 200/201 response
- Order created with status "pending"
- Order items created in database
- Inventory `quantity_in_stock` reduced by 2
- Product `stock_quantity` reduced by 2
- All changes committed together

**Verification Queries:**
```sql
-- Check order was created
SELECT * FROM orders WHERE id = <returned_order_id>;

-- Check order items
SELECT * FROM order_items WHERE order_id = <returned_order_id>;

-- Check inventory was reduced
SELECT quantity_in_stock FROM inventory WHERE product_id = 1;

-- Check product stock was reduced
SELECT stock_quantity FROM products WHERE id = 1;
```

### Test Scenario 2: Insufficient Stock (Should Fail)

**Setup:**
Assume product ID 1 has only 5 units in stock.

**Request:**
```json
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 10
    }
  ]
}
```

**Expected Result:**
- HTTP 400 Bad Request
- Error message: "Insufficient stock for product ID: 1. Available: 5, Requested: 10"
- NO order created
- NO order items created
- Inventory NOT changed
- Product stock NOT changed

**Verification Queries:**
```sql
-- Verify no new orders were created
SELECT COUNT(*) FROM orders WHERE created_at > (NOW() - INTERVAL '1 minute');

-- Verify inventory unchanged
SELECT quantity_in_stock FROM inventory WHERE product_id = 1;
-- Should be same as before request

-- Verify product stock unchanged  
SELECT stock_quantity FROM products WHERE id = 1;
-- Should be same as before request
```

### Test Scenario 3: Non-existent Product (Should Fail)

**Request:**
```json
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 99999,
      "quantity": 1
    }
  ]
}
```

**Expected Result:**
- HTTP 404 Not Found
- Error message: "Product not found with ID: 99999"
- NO order created
- NO changes to database

### Test Scenario 4: Multiple Items Order

**Request:**
```json
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 3
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**Expected Result:**
- HTTP 200/201 response
- One order created with 3 order items
- All products' inventory reduced correctly
- All products' stock reduced correctly
- Total amount calculated correctly

**Verification:**
```sql
-- Check order items count
SELECT COUNT(*) FROM order_items WHERE order_id = <returned_order_id>;
-- Should be 3

-- Check each product's inventory
SELECT p.id, p.name, p.stock_quantity, i.quantity_in_stock
FROM products p
JOIN inventory i ON p.id = i.product_id
WHERE p.id IN (1, 2, 3);
-- All should be reduced by ordered quantities
```

### Test Scenario 5: Concurrent Orders (Race Condition Test)

**Setup:**
Product ID 1 has 10 units in stock.

**Action:**
Send 2 simultaneous requests:

**Request 1 & 2:**
```json
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 6
    }
  ]
}
```

**Expected Result:**
- One request succeeds (first to acquire lock)
- Second request fails with "Insufficient stock" error
- Inventory shows 4 units remaining (10 - 6)
- NO overselling occurs

### Test Scenario 6: Transaction Rollback (Simulated)

To test rollback, you can temporarily modify the code to throw an exception after order creation but before order items creation.

**Expected Behavior:**
- Order should NOT exist in database
- Inventory should NOT be changed
- Product stock should NOT be changed
- Transaction rolled back completely

## Automated Testing

### Update the Unit Test

The existing `OrderServiceTest.createOrder_success_reducesInventory_and_savesOrder()` test expects direct inventory updates, but now the DAO handles this.

**Updated Test:**
```java
@Test
void createOrder_success_reducesInventory_and_savesOrder() throws Exception {
    // Arrange
    User user = new User(); 
    user.setId(1L);
    
    Product prod = new Product(); 
    prod.setId(10L); 
    prod.setPrice(5.0);
    
    Inventory inv = new Inventory(); 
    inv.setId(100L); 
    inv.setProduct(prod); 
    inv.setStockQuantity(10);

    OrderItemRequest itemReq = new OrderItemRequest(); 
    itemReq.setProductId(prod.getId()); 
    itemReq.setQuantity(2);
    
    OrderRequest req = new OrderRequest(); 
    req.setUserId(user.getId()); 
    req.setItems(List.of(itemReq));

    when(userDao.findById(user.getId())).thenReturn(Optional.of(user));
    when(productDao.findById(prod.getId())).thenReturn(Optional.of(prod));
    when(inventoryDao.findByProductId(prod.getId())).thenReturn(Optional.of(inv));
    when(ordersDao.save(any())).thenReturn(55L);
    when(ordersMapper.toResponse((Orders) any())).thenReturn(
        new OrderResponse(55L, user.getId(), "pending", 10.0, List.of(), LocalDateTime.now())
    );

    // Act
    OrderResponse resp = orderService.createOrder(req);

    // Assert
    assertNotNull(resp);
    assertEquals(55L, resp.id());
    
    // Verify ordersDao.save was called (which handles all updates internally)
    verify(ordersDao, times(1)).save(any(Orders.class));
    
    // Verify validation methods were called
    verify(userDao, times(1)).findById(user.getId());
    verify(productDao, times(1)).findById(prod.getId());
    verify(inventoryDao, times(1)).findByProductId(prod.getId());
    
    // NOTE: inventoryDao.update() is NOT called directly by service anymore
    // The DAO handles it internally within the transaction
}
```

### Integration Test (Spring Boot Test)

Create a full integration test with actual database:

```java
@SpringBootTest
@Transactional
class OrderIntegrationTest {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductDao productDao;
    
    @Autowired
    private InventoryDao inventoryDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private OrdersDao ordersDao;
    
    @Test
    void createOrder_shouldUpdateAllTablesAtomically() {
        // Setup test data
        User user = createTestUser();
        Product product = createTestProduct();
        Inventory inventory = createTestInventory(product, 100);
        
        int initialStock = inventory.getStockQuantity();
        int orderQuantity = 5;
        
        // Create order request
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(product.getId());
        itemReq.setQuantity(orderQuantity);
        
        OrderRequest req = new OrderRequest();
        req.setUserId(user.getId());
        req.setItems(List.of(itemReq));
        
        // Execute
        OrderResponse response = orderService.createOrder(req);
        
        // Verify order created
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("pending", response.status());
        
        // Verify inventory updated
        Inventory updatedInv = inventoryDao.findByProductId(product.getId()).orElseThrow();
        assertEquals(initialStock - orderQuantity, updatedInv.getStockQuantity());
        
        // Verify product stock updated
        Product updatedProd = productDao.findById(product.getId()).orElseThrow();
        assertEquals(initialStock - orderQuantity, updatedProd.getStockQuantity());
        
        // Verify order items created
        Orders order = ordersDao.findById(response.id()).orElseThrow();
        assertEquals(1, order.getItems().size());
        assertEquals(orderQuantity, order.getItems().get(0).getQuantity());
    }
    
    @Test
    void createOrder_insufficientStock_shouldRollbackEverything() {
        // Setup
        User user = createTestUser();
        Product product = createTestProduct();
        Inventory inventory = createTestInventory(product, 5);
        
        int initialStock = inventory.getStockQuantity();
        
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(product.getId());
        itemReq.setQuantity(10); // More than available
        
        OrderRequest req = new OrderRequest();
        req.setUserId(user.getId());
        req.setItems(List.of(itemReq));
        
        // Execute and verify exception
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(req);
        });
        
        // Verify nothing changed
        Inventory unchangedInv = inventoryDao.findByProductId(product.getId()).orElseThrow();
        assertEquals(initialStock, unchangedInv.getStockQuantity());
        
        Product unchangedProd = productDao.findById(product.getId()).orElseThrow();
        assertEquals(initialStock, unchangedProd.getStockQuantity());
    }
}
```

## Performance Testing

### Load Test Setup

Use JMeter or similar tool to send concurrent requests:

**Test Configuration:**
- Number of threads: 50
- Ramp-up time: 10 seconds
- Loop count: 10
- Total requests: 500

**Metrics to Monitor:**
- Response time (average, 95th percentile, max)
- Throughput (requests/second)
- Error rate
- Database connection pool usage
- Transaction deadlocks (if any)

### Expected Performance
- Average response time: < 200ms
- 95th percentile: < 500ms
- Error rate: 0% (for valid requests)
- No database deadlocks

## Common Issues and Solutions

### Issue 1: Foreign Key Constraint Violation
**Symptom:** "insert or update on table order_items violates foreign key constraint"

**Cause:** Order not committed before order items are inserted

**Solution:** Ensure DataSourceUtils is used and connection is not manually closed

### Issue 2: Inventory Not Updated
**Symptom:** Order created but inventory unchanged

**Cause:** Transaction not propagating properly

**Solution:** Verify @Transactional annotation on service method

### Issue 3: Transaction Rollback Not Working
**Symptom:** Partial data remains after error

**Cause:** Checked exceptions not rolling back by default

**Solution:** Use `@Transactional(rollbackFor = Exception.class)` or throw RuntimeException

## Logging

Enable transaction logging to debug issues:

```properties
# application.properties
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.jdbc=DEBUG
```

This will show transaction boundaries and SQL statements.

## Summary Checklist

After deploying, verify:
- ✅ Successful order creates all records
- ✅ Inventory and product stock are updated
- ✅ Insufficient stock is caught before DB changes
- ✅ Invalid data throws appropriate errors
- ✅ Transaction rollback works on errors
- ✅ No orphaned records after failures
- ✅ Concurrent orders handled correctly
- ✅ Response times are acceptable
- ✅ No database deadlocks under load

The create order workflow is now production-ready with full transactional integrity!
