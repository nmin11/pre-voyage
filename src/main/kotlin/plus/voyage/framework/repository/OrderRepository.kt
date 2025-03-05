package plus.voyage.framework.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.entity.Order
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<Order, Long> {
    @Query("""
        SELECT o.coffee 
        FROM Order o 
        WHERE o.createdAt >= :targetDay 
        GROUP BY o.coffee 
        ORDER BY COUNT(o.coffee) DESC
    """)
    fun findPopularCoffeeSince(@Param("targetDay") targetDay: LocalDateTime, pageable: Pageable): List<Coffee>
}
