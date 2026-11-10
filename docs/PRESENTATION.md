# 5-minute presentation outline

1. **Context (30 s)** – Orders & stock management for a small shop. Goal: never sell what we do not have, know what to reorder.
2. **Model (60 s)** – 7 entities. Show the ER diagram in the README. Point out: OneToMany (Category→Product, Customer→Order,
   Order→OrderItem with cascade), ManyToMany (Product↔Supplier, owner side = Product with `@JoinTable`), ManyToOne.
   `@JsonIgnore` on the inverse side avoids infinite JSON loops.
3. **Architecture (45 s)** – controller → service → repository. DTO records for requests, entities for responses.
4. **Business logic (60 s)** – `OrderService.create` is `@Transactional`: checks stock, decreases it, saves the order
   with its items (cascade) and logs OUT movements. If one line fails, everything rolls back. Cancel = stock restored.
5. **Custom queries (45 s)** – low stock (`@Query`), category+price range (derived), orders by status/date (derived),
   top customers and best sellers (JPQL `group by` returning DTO records), suppliers by product (ManyToMany).
6. **Validation & errors (30 s)** – annotations + `@RestControllerAdvice`: 400 / 404 / 409 with a JSON body.
7. **Live demo with Postman (45 s)** – run the collection: create order → stock 10→7 → cancel → stock 10; try an invalid product (400) and
   an order with too much quantity (409).

Questions to be ready for: why `@Transactional`? what does `mappedBy` mean? why LAZY/EAGER? difference between derived queries and `@Query`?
why DTOs? what happens on `orphanRemoval`?
