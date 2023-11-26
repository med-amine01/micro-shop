### To start project locally you need:
1. **Docker installed**
2. **If you are NOT on linux distribution (windows, mac ...) RUN :**
   - `docker-compose -f docker-compose.yml up -d`
3. **If you are on linux distribution RUN :**
   - `make start`

### You can import [MICRO-SHOP.postman_collection.json](MICRO-SHOP.postman_collection.json) file to postman to check all bellow routes.

### Identity Service
1. **Create User**
   - Route: `POST http://localhost:8765/identity-service/api/v1/users`
   - Request Body:
     ```json
     {
         "username": "john_doe_01",
         "userPassword": "john",
         "email": "john.doe@example.com"
     }
     ```

2. **Get All Users**
   - Route: `GET http://localhost:8765/identity-service/api/v1/users`

3. **Get User**
   - Route: `GET http://localhost:8765/identity-service/api/v1/users/admin-01`

4. **Get Roles**
   - Route: `GET http://localhost:8765/identity-service/api/v1/roles`

5. **Create Role**
   - Route: `POST http://localhost:8765/identity-service/api/v1/roles`
   - Request Body:
     ```json
     {
         "roleName": "ROLE_NAME"
     }
     ```

6. **Authentication Token**
   - Route: `POST http://localhost:8765/identity-service/api/v1/auth/token`
   - Request Body:
     ```json
     {
         "username": "admin-01",
         "userPassword": "test"
     }
     ```

7. **Identity Health**
   - Route: `GET http://localhost:8765/identity-service/actuator/health`
   - Response Body:
     ```json
     {
         "status": "UP"
     }
     ```

### Order Service
1. **GET All Orders**
   - Route: `GET http://localhost:8765/order-service/api/v1/orders`

2. **GET Order by ID**
   - Route: `GET http://localhost:8765/order-service/api/v1/orders/1`

3. **Create Order**
   - Route: `POST http://localhost:8765/order-service/api/v1/orders`
   - Request Body:
     ```json
     {
         "orderLineItemsRequestList": [
             {
                 "skuCode": "{{last-skuCode}}",
                 "quantity": 1
             }
         ]
     }
     ```

4. **Update Order**
   - Route: `PATCH http://localhost:8765/order-service/api/v1/orders/{{order-number}}`
   - Request Body:
     ```json
     {
         "orderStatus": "placed"
     }
     ```

5. **Disable Order**
   - Route: `DELETE http://localhost:8765/order-service/api/v1/orders/1`

6. **Order Health**
   - Route: `GET http://localhost:8765/order-service/actuator/health`

### Product Service
1. **GET All Products**
   - Route: `GET http://localhost:8765/product-service/api/v1/products?enabled=false`

2. **GET Product by SkuCode**
   - Route: `GET http://localhost:8765/product-service/api/v1/products/{{last-skuCode}}`

3. **Create Product**
   - Route: `POST http://localhost:8765/product-service/api/v1/products`
   - Request Body:
     ```json
     {
         "name": "iphone 14 yellow",
         "couponCode": "{{last-code}}",
         "price": 1800
     }
     ```

4. **Update Product**
   - Route: `PUT http://localhost:8765/product-service/api/v1/products/{{last-skuCode}}`
   - Request Body:
     ```json
     {
         "couponCode": "{{last-code}}"
     }
     ```

5. **Delete Product**
   - Route: `DELETE http://localhost:8765/product-service/api/v1/products/{{last-skuCode}}`

6. **Product Health**
   - Route: `GET http://localhost:8765/product-service/actuator/health`

### Coupon Service
1. **GET All Coupons**
   - Route: `GET http://localhost:8765/coupon-service/api/v1/coupons`

2. **GET Coupon by Code**
   - Route: `GET http://localhost:8765/coupon-service/api/v1/coupons/{{last-code}}`

3. **Create Coupon**
   - Route: `POST http://localhost:8765/coupon-service/api/v1/coupons`
   - Request Body:
     ```json
     {
         "name": "supersale",
         "discount": 20,
         "expirationDate": "30-11-2023 18:40:00"
     }
     ```

4. **Update Coupon**
   - Route: `PUT http://localhost:8765/coupon-service/api/v1/coupons/{{last-code}}`
   - Request Body:
     ```json
     {
         "discount": 50,
         "expirationDate": "12-11-2023 19:55:00"
     }
     ```

5. **Disable Coupon**
   - Route: `DELETE http://localhost:8765/coupon-service/api/v1/coupons/{{last-code}}`

6. **Coupon Health**
   - Route: `GET http://localhost:8765/coupon-service/actuator/health`

### Inventory Service
1. **GET Inventories**
   - Route: `GET http://localhost:8765/inventory-service/api/v1/inventories`

2. **Check Product**
   - Route: `GET http://localhost:8765/inventory-service/api/v1/inventories/product/check?skuCode={{last-skuCode}}`

3. **Update Inventory Quantity**
   - Route: `PUT http://localhost:8765/inventory-service/api/v1/inventories/product/quantity/{{last-skuCode}}`
   - Request Body:
     ```json
     {
         "quantity": 5,
         "increase": true
     }
     ```

4. **Inventory Health**
   - Route: `GET http://localhost:8765/inventory-service/actuator/health`

