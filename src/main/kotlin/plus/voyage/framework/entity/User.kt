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

    @Column(nullable = false)
    var points: Int = 5000

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var boards: MutableList<Board> = mutableListOf()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var comments: MutableList<Comment> = mutableListOf()
}

enum class Role(val roleName: String) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN")
}
