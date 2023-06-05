package cart.application;

import cart.domain.cartitem.CartItem;
import cart.domain.member.Member;
import cart.domain.order.Order;
import cart.domain.order.OrderProduct;
import cart.exception.badrequest.order.OrderPointException;
import cart.repository.CartItemRepository;
import cart.repository.MemberRepository;
import cart.repository.OrderRepository;
import cart.ui.controller.dto.request.OrderRequest;
import cart.ui.controller.dto.response.OrderResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    public OrderService(
            OrderRepository orderRepository,
            MemberRepository memberRepository,
            CartItemRepository cartItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public List<OrderResponse> getOrders(Member member) {
        List<Order> orders = orderRepository.findAllByMemberId(member.getId());
        return OrderResponse.listOf(orders);
    }

    public OrderResponse getOrderDetail(Long id, Member member) {
        Order order = orderRepository.findById(id);
        order.checkOwner(member);
        return OrderResponse.from(order);
    }

    @Transactional
    public Long processOrder(Member member, OrderRequest orderRequest) {
        List<CartItem> cartItems = cartItemRepository.findAllInIds(orderRequest.getCartItemIds());
        for (CartItem cartItem : cartItems) {
            cartItem.checkOwner(member);
        }
        Order order = new Order(member, orderInCart(cartItems), orderRequest.getPoint());
        if (order.getTotalPrice() < orderRequest.getPoint()) {
            throw new OrderPointException(
                    "사용하려는 포인트가 총 결제 금액보다 많습니다. 총 결제 금액: " + order.getTotalPrice() + ", 사용 포인트: " + orderRequest.getPoint());
        }
        member.usePoint(orderRequest.getPoint());
        member.addPoint(order.getSavedPoint());

        memberRepository.update(member);
        cartItemRepository.deleteAll(cartItems);
        return orderRepository.save(order);
    }

    private List<OrderProduct> orderInCart(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(OrderProduct::from)
                .collect(Collectors.toList());
    }
}
