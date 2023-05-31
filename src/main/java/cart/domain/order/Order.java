package cart.domain.order;

import static cart.exception.OrderException.IllegalMember;

import cart.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {

    private Long id;
    private Member member;
    private OrderProducts orderProducts;
    private Point usedPoint;
    private Fee deliveryFee;
    private LocalDateTime orderedAt;

    private Order() {
    }

    public Order(Member member, List<OrderProduct> orderProducts, int usedPoint) {
        this(null, member, orderProducts, usedPoint, null);
    }

    public Order(Long id, Member member, List<OrderProduct> orderProducts, int usedPoint, LocalDateTime orderedAt) {
        this.id = id;
        this.member = member;
        this.orderProducts = new OrderProducts(orderProducts);
        this.usedPoint = new Point(usedPoint);
        this.deliveryFee = Fee.from(this.orderProducts.calculateTotalPrice());
        this.orderedAt = orderedAt;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void checkOwner(Member member) {
        if (!Objects.equals(this.member.getId(), member.getId())) {
            throw new IllegalMember();
        }
    }

    public int calculateTotalPrice() {
        return orderProducts.calculateTotalPrice();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts.getValue();
    }

    public int getUsedPoint() {
        return usedPoint.getValue();
    }

    public int getDeliveryFee() {
        return deliveryFee.getValue();
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
}