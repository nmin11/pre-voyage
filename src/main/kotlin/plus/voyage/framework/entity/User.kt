package plus.voyage.framework.entity

import jakarta.persistence.*

@Entity
@Table(name = "\"user\"")
class User(
    @Column(unique = true, nullable = false)
    var username: String,

    @Column(nullable = false)
    var password: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    var id: Int? = null

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
}

enum class Role {
    USER,
    ADMIN
}
