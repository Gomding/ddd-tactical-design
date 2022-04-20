package kitchenpos.products.tobe.domain;

import kitchenpos.common.domain.Price;
import kitchenpos.common.domain.ProfanityFilteredName;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Table(name = "product")
@Entity
public class Product {
    @Column(name = "id", columnDefinition = "varbinary(16)")
    @Id
    private UUID id;

    @Embedded
    private Price price;

    @Embedded
    private ProfanityFilteredName profanityFilteredName;

    protected Product() {
    }

    public Product(UUID id, ProfanityFilteredName profanityFilteredName, Price price) {
        this.id = id;
        this.price = price;
        this.profanityFilteredName = profanityFilteredName;
    }

    public UUID getId() {
        return id;
    }

    public Price getPrice() {
        return price;
    }

    public String getName() {
        return profanityFilteredName.getValue();
    }

    public Product changePrice(final Price price) {
        return new Product(id, profanityFilteredName, price);
    }
}
