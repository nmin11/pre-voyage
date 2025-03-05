package plus.voyage.framework.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener::class)
class Order(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_id")
    var coffee: Coffee,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    var id: Long? = null

    @CreatedDate
    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
}
