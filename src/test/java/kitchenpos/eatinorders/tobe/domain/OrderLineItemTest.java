package kitchenpos.eatinorders.tobe.domain;

import kitchenpos.menus.domain.Menu;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static kitchenpos.Fixtures.menu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderLineItemTest {

    private final Menu menu = menu();
    private final BigDecimal price = BigDecimal.valueOf(15000);


    @Test
    @DisplayName("주문 항목을 생성한다.")
    void createOrderLineItem() {
        // given
        long quantity = 2;

        // when
        OrderLineItem orderLineItem = new OrderLineItem(menu, quantity);

        // then
        assertThat(orderLineItem).isEqualTo(new OrderLineItem(menu, quantity));
    }

    @ParameterizedTest
    @DisplayName("매장 주문항목의 수량은 0보다 작을 수 있다.")
    @ValueSource(ints = {-1, 0, 1})
    void createMenuPrice(int quantity) {
        // when
        OrderLineItem orderLineItem = new OrderLineItem(menu, quantity);

        // then
        assertThat(orderLineItem).isEqualTo(new OrderLineItem(menu, quantity));
    }
}
