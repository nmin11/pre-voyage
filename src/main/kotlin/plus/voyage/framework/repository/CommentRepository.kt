package plus.voyage.framework.repository

import org.springframework.data.jpa.repository.JpaRepository
import plus.voyage.framework.entity.Comment

interface CommentRepository : JpaRepository<Comment, Int>
