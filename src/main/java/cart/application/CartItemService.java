package cart.application;

import cart.dao.CartItemDao;
import cart.dao.ProductDao;
import cart.domain.cartitem.CartItem;
import cart.domain.member.Member;
import cart.repository.ProductRepository;
import cart.ui.controller.dto.request.CartItemQuantityUpdateRequest;
import cart.ui.controller.dto.request.CartItemRequest;
import cart.ui.controller.dto.response.CartItemResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final ProductDao productDao;
    private final CartItemDao cartItemDao;
    private final ProductRepository productRepository;

    public CartItemService(ProductDao productDao, CartItemDao cartItemDao, ProductRepository productRepository) {
        this.productDao = productDao;
        this.cartItemDao = cartItemDao;
        this.productRepository = productRepository;
    }

    public List<CartItemResponse> findByMember(Member member) {
        List<CartItem> cartItems = cartItemDao.findByMemberId(member.getId());
        return cartItems.stream().map(CartItemResponse::of).collect(Collectors.toList());
    }

    public Long add(Member member, CartItemRequest cartItemRequest) {
        return cartItemDao.save(new CartItem(productRepository.getProductById(cartItemRequest.getProductId()), member));
    }

    public void updateQuantity(Member member, Long id, CartItemQuantityUpdateRequest request) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        if (request.getQuantity() == 0) {
            cartItemDao.deleteById(id);
            return;
        }

        cartItem.changeQuantity(request.getQuantity());
        cartItemDao.updateQuantity(cartItem);
    }

    public void remove(Member member, Long id) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        cartItemDao.deleteById(id);
    }
}
