package com.mwf9.projet.repository;

import com.mwf9.projet.dto.CustomerSpending;
import com.mwf9.projet.model.CustomerOrder;
import com.mwf9.projet.model.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByCustomerId(Long customerId);

    /** Custom query #3: orders with a given status placed between two dates. */
    List<CustomerOrder> findByStatusAndOrderDateBetween(OrderStatus status, LocalDateTime from, LocalDateTime to);

    /** Custom query #4: customers ranked by total amount spent (cancelled orders excluded). */
    @Query("select new com.mwf9.projet.dto.CustomerSpending(c.id, c.firstName, c.lastName, sum(o.totalAmount)) "
            + "from CustomerOrder o join o.customer c "
            + "where o.status <> :excluded "
            + "group by c.id, c.firstName, c.lastName "
            + "order by sum(o.totalAmount) desc")
    List<CustomerSpending> findTopCustomers(@Param("excluded") OrderStatus excluded, Pageable pageable);
}
