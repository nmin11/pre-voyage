package plus.voyage.framework.entity

import jakarta.persistence.*

@Entity
@Table(name = "\"user\"")
class User(username: String, password: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    var id: Int? = null

    @Column(unique = true, nullable = false)
    var username: String = username

    @Column(nullable = false)
    var password: String = password

    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
}

enum class Role {
    USER,
    ADMIN
}
