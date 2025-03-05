package plus.voyage.framework.entity

import jakarta.persistence.*

@Entity
class Coffee(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var imageUrl: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coffeeId")
    var id: Int? = null
}
