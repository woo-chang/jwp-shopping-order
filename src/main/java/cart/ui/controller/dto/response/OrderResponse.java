package cart.ui.controller.dto.response;

import cart.domain.order.Order;
import cart.domain.order.OrderProduct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {

    private Long orderId;
    private List<OrderProductResponse> products;
    private int totalPrice;
    private int usedPoint;
    private int deliveryFee;
    private LocalDateTime orderedAt;

    private OrderResponse() {
    }

    public OrderResponse(
            Long orderId,
            List<OrderProductResponse> products,
            int totalPrice,
            int usedPoint,
            int deliveryFee,
            LocalDateTime orderedAt
    ) {
        this.orderId = orderId;
        this.products = products;
        this.totalPrice = totalPrice;
        this.usedPoint = usedPoint;
        this.deliveryFee = deliveryFee;
        this.orderedAt = orderedAt;
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                generateOrderProducts(order.getOrderProducts()),
                order.calculateTotalPrice(),
                order.getUsedPoint(),
                order.getDeliveryFee(),
                order.getOrderedAt()
        );
    }

    private static List<OrderProductResponse> generateOrderProducts(List<OrderProduct> products) {
        return products.stream()
                .map(OrderProductResponse::from)
                .collect(Collectors.toList());
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderProductResponse> getProducts() {
        return products;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getUsedPoint() {
        return usedPoint;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public String getOrderedAt() {
        return orderedAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
