package kitchenpos.eatin_orders.application;

import kitchenpos.eatin_orders.infrastructure.InMemoryEatInOrderRepository;
import kitchenpos.eatin_orders.infrastructure.InMemoryOrderTableRepository;
import kitchenpos.eatinorders.application.orderTables.OrderTableService;
import kitchenpos.eatinorders.domain.orders.EatInOrderRepository;
import kitchenpos.eatinorders.domain.orders.OrderStatus;
import kitchenpos.eatinorders.domain.ordertables.NumberOfGuests;
import kitchenpos.eatinorders.domain.ordertables.OrderTable;
import kitchenpos.eatinorders.domain.ordertables.OrderTableName;
import kitchenpos.eatinorders.domain.ordertables.OrderTableRepository;
import kitchenpos.products.application.InMemoryProductRepository;
import kitchenpos.products.tobe.domain.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static kitchenpos.eatin_orders.EatInOrderFixtures.order;
import static kitchenpos.eatin_orders.EatInOrderFixtures.orderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {
    private OrderTableRepository orderTableRepository;
    private ProductRepository productRepository;
    private EatInOrderRepository eatInOrderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        productRepository = new InMemoryProductRepository();
        eatInOrderRepository = new InMemoryEatInOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, eatInOrderRepository);
    }

    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        final OrderTableName expected = new OrderTableName("1번");
        final OrderTable actual = orderTableService.create(expected);
        assertThat(actual).isNotNull();
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(expected),
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(NumberOfGuests.ZERO),
                () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("빈 테이블을 해지할 수 있다.")
    @Test
    void sit() {
        final UUID orderTableId = orderTableRepository.save(orderTable(false, 0)).getId();
        final OrderTable actual = orderTableService.sit(orderTableId);
        assertThat(actual.isOccupied()).isTrue();
    }

    @DisplayName("빈 테이블로 설정할 수 있다.")
    @Test
    void clear() {
        final UUID orderTableId = orderTableRepository.save(orderTable(true, 4)).getId();
        final OrderTable actual = orderTableService.clear(orderTableId);
        assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isEqualTo(NumberOfGuests.ZERO),
                () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("완료되지 않은 주문이 있는 주문 테이블은 빈 테이블로 설정할 수 없다.")
    @Test
    void clearWithUncompletedOrders() {
        final OrderTable orderTable = orderTableRepository.save(orderTable(true, 4));
        final UUID orderTableId = orderTable.getId();
        eatInOrderRepository.save(order(OrderStatus.ACCEPTED, orderTable, productRepository));
        assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("방문한 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        final UUID orderTableId = orderTableRepository.save(orderTable(true, 0)).getId();
        final NumberOfGuests expected = new NumberOfGuests(4);
        final OrderTable actual = orderTableService.changeNumberOfGuests(orderTableId, expected);
        assertThat(actual.getNumberOfGuests()).isEqualTo(new NumberOfGuests(4));
    }

    @DisplayName("빈 테이블은 방문한 손님 수를 변경할 수 없다.")
    @Test
    void changeNumberOfGuestsInEmptyTable() {
        final UUID orderTableId = orderTableRepository.save(orderTable(false, 0)).getId();
        final NumberOfGuests expected = new NumberOfGuests(4);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, expected))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        orderTableRepository.save(orderTable());
        final List<OrderTable> actual = orderTableService.findAll();
        assertThat(actual).hasSize(1);
    }
}
