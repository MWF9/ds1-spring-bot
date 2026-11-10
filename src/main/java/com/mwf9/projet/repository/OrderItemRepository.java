package com.mwf9.projet.repository;

import com.mwf9.projet.dto.ProductSales;
import com.mwf9.projet.model.OrderItem;
import com.mwf9.projet.model.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /** Custom query #6: best-selling products by quantity sold (cancelled orders excluded). */
    @Query("select new com.mwf9.projet.dto.ProductSales(p.id, p.name, sum(i.quantity)) "
            + "from OrderItem i join i.product p join i.order o "
            + "where o.status <> :excluded "
            + "group by p.id, p.name "
            + "order by sum(i.quantity) desc")
    List<ProductSales> findBestSellers(@Param("excluded") OrderStatus excluded, Pageable pageable);
}
