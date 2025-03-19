package plus.voyage.framework.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import plus.voyage.framework.dto.CoffeeCreateRequest
import plus.voyage.framework.dto.OrderMockApiRequest
import plus.voyage.framework.entity.Coffee
import plus.voyage.framework.entity.Order
import plus.voyage.framework.entity.User
import plus.voyage.framework.exception.CoffeeNotFoundException
import plus.voyage.framework.exception.InsufficientPointsException
import plus.voyage.framework.repository.CoffeeRepository
import plus.voyage.framework.repository.OrderRepository
import java.util.*
import kotlin.test.Test

class CoffeeServiceTest {
    private val coffeeRepository: CoffeeRepository = mockk()
    private val orderRepository: OrderRepository = mockk()
    private val userService: UserService = mockk()
    private val objectMapper: ObjectMapper = mockk()
    private val restClient: RestClient = mockk()
    private var coffeeService = CoffeeService(
        coffeeRepository, orderRepository, userService, objectMapper, restClient
    )

    private lateinit var user: User
    private lateinit var coffee: Coffee
    private lateinit var order: Order
    private lateinit var top3Coffee: List<Coffee>

    @BeforeEach
    fun setup() {
        user = User(username = "testUser", password = "hashedPassword").apply { id = 1 }.apply { points = 10000 }
        coffee = Coffee(name = "아메리카노", price = 3000, imageUrl = "americano.jpg").apply { id = 1 }
        order = Order(coffee, user)
        top3Coffee = listOf(
            coffee,
            Coffee(name = "카페라떼", price = 5000, imageUrl = "latte.jpg").apply { id = 2 },
            Coffee(name = "에스프레소", price = 2500, imageUrl = "espresso.jpg").apply { id = 3 }
        )
    }

    @Test
    fun `커피 생성 성공`() {
        // given
        val request = CoffeeCreateRequest(name = "라떼", price = 4000, imageUrl = "latte.jpg")
        val newCoffee = Coffee(name = "라떼", price = 4000, imageUrl = "latte.jpg").apply { id = 2 }
        every { coffeeRepository.save(any()) } returns newCoffee

        // when
        val result = coffeeService.create(request)

        // then
        assertEquals("라떼", result.name)
        assertEquals(4000, result.price)
        assertEquals("latte.jpg", result.imageUrl)
        verify { coffeeRepository.save(any()) }
    }

    @Test
    fun `주간 인기 메뉴 조회 시 캐시 미스_DB 에서 주간 인기 메뉴 조회`() {
        // given
        every { orderRepository.findPopularCoffeeSince(any(), any()) } returns top3Coffee

        // when
        val result = coffeeService.getWeeklyPopularCoffee()

        // then
        assertEquals(3, result.totalCounts)
        verify { orderRepository.findPopularCoffeeSince(any(), any()) }
    }

    @Test
    fun `존재하지 않는 커피 메뉴_커피 주문 실패`() {
        // given
        every { coffeeRepository.findById(999) } returns Optional.empty()

        // when & then
        assertThrows<CoffeeNotFoundException> {
            coffeeService.orderCoffee(999)
        }
    }

    @Test
    fun `사용자의 보유 포인트 부족_커피 주문 실패`() {
        // given
        user.points = 2000 // 커피 가격보다 적은 포인트
        every { coffeeRepository.findById(coffee.id!!) } returns Optional.of(coffee)
        every { userService.getCurrentUser() } returns user

        // when & then
        assertThrows<InsufficientPointsException> {
            coffeeService.orderCoffee(coffee.id!!)
        }
    }

    @Test
    fun `Mock API 주문 내역 전송 성공`() {
        // given
        val order = Order(coffee, user).apply { id = 1 }
        val request = OrderMockApiRequest(order.id.toString(), user.id!!, coffee.id!!, coffee.price)
        val jsonPayload = """{"orderId":"1","userId":1,"coffeeId":1,"price":3000}"""

        every { userService.getCurrentUser() } returns user
        every { coffeeRepository.findById(any()) } returns Optional.of(coffee)
        every { orderRepository.save(any()) } returns order
        every { objectMapper.writeValueAsString(request) } returns jsonPayload
        every { restClient
            .post()
            .uri(any() as String)
            .contentType(any())
            .body(jsonPayload)
            .retrieve()
            .toEntity(String::class.java)
        } returns mockk {
            every { statusCode } returns HttpStatus.OK
            every { body } returns "Success"
        }

        // when
        coffeeService.orderCoffee(coffee.id!!)

        // then
        verify { restClient
            .post()
            .uri(any() as String)
            .contentType(any())
            .body(jsonPayload)
            .retrieve()
            .toEntity(String::class.java)
        }
    }

    @Test
    fun `Mock API 주문 내역 전송 실패 시 로그 출력`() {
        // given
        val order = Order(coffee, user).apply { id = 1 }
        val request = OrderMockApiRequest(order.id.toString(), user.id!!, coffee.id!!, coffee.price)
        val jsonPayload = """{"orderId":"1","userId":1,"coffeeId":1,"price":3000}"""

        every { userService.getCurrentUser() } returns user
        every { coffeeRepository.findById(any()) } returns Optional.of(coffee)
        every { orderRepository.save(any()) } returns order
        every { objectMapper.writeValueAsString(request) } returns jsonPayload
        every { restClient
            .post()
            .uri(any() as String)
            .contentType(any())
            .body(jsonPayload)
            .retrieve()
            .toEntity(String::class.java)
        } throws RestClientException("Network Error")

        // when
        assertDoesNotThrow { coffeeService.orderCoffee(coffee.id!!) }

        // then
        verify { restClient
            .post()
            .uri(any() as String)
            .contentType(any())
            .body(jsonPayload)
            .retrieve()
            .toEntity(String::class.java)
        }
    }
}
