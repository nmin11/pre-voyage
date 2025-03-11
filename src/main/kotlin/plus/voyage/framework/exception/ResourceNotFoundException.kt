package plus.voyage.framework.exception

open class ResourceNotFoundException(message: String) : RuntimeException(message)

class UserNotFoundException(message: String) : ResourceNotFoundException(message)

class BoardNotFoundException(message: String) : ResourceNotFoundException(message)

class CommentNotFoundException(message: String) : ResourceNotFoundException(message)
