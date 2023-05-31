package cart.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import cart.dao.entity.CartItemEntity;
import cart.dao.entity.MemberEntity;
import cart.dao.entity.ProductEntity;
import cart.test.RepositoryTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@RepositoryTest
class ProductDaoTest {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private CartItemDao cartItemDao;

    @Test
    @DisplayName("getAllProducts 메서드는 모든 상품 데이터를 조회한다.")
    void getAllProducts() {
        Long firstProductId = productDao.createProduct(new ProductEntity("치킨", 10000, "http://image.com"));
        Long secondProductId = productDao.createProduct(new ProductEntity("샐러드", 20000, "http://image.com"));

        List<ProductEntity> result = productDao.getAllProducts();

        ProductEntity firstProductEntity = productDao.getProductById(firstProductId).get();
        ProductEntity secondProductEntity = productDao.getProductById(secondProductId).get();
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(firstProductEntity),
                () -> assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(secondProductEntity)
        );
    }

    @Test
    @DisplayName("updateProduct 메서드는 상품 데이터를 수정한다.")
    void updateProduct() {
        Long savedProductId = productDao.createProduct(new ProductEntity("치킨", 10000, "http://image.com"));
        ProductEntity findProductEntity = productDao.getProductById(savedProductId).get();
        ProductEntity updateProductEntity = new ProductEntity(findProductEntity.getId(), "피자", 13000, "http://photo.com");

        productDao.updateProduct(updateProductEntity);

        ProductEntity result = productDao.getProductById(savedProductId).get();
        assertAll(
                () -> assertThat(result).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(updateProductEntity),
                () -> assertThat(result.getCreatedAt()).isEqualTo(findProductEntity.getCreatedAt()),
                () -> assertThat(result.getUpdatedAt()).isNotNull()
        );
    }

    @Nested
    @DisplayName("deleteProduct 메서드는 ")
    class DeleteProduct {

        @Test
        @DisplayName("장바구니에 담겨 있는 상품이라면 예외를 던진다.")
        void existCartItemProduct() {
            MemberEntity member = new MemberEntity("a@a.com", "password1", 10);
            Long savedMemberId = memberDao.addMember(member);

            ProductEntity product = new ProductEntity("치킨", 10000, "http://image.com");
            Long savedProductId = productDao.createProduct(product);

            CartItemEntity cartItem = new CartItemEntity(member.assignId(savedMemberId), product.assignId(savedProductId), 1);
            cartItemDao.save(cartItem);

            assertThatThrownBy(() -> productDao.deleteProduct(savedProductId))
                    .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("상품 데이터를 삭제한다.")
        void deleteProduct() {
            Long savedProductId = productDao.createProduct(new ProductEntity("치킨", 10000, "http://image.com"));

            productDao.deleteProduct(savedProductId);

            Optional<ProductEntity> result = productDao.getProductById(savedProductId);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getProductById 메서드는 ")
    class GetProductById {

        @Test
        @DisplayName("조회 시 ID에 해당하는 상품이 존재하면 해당 상품 데이터를 반환한다.")
        void getProduct() {
            ProductEntity productEntity = new ProductEntity("치킨", 10000, "http://image.com");
            Long savedProductId = productDao.createProduct(productEntity);

            Optional<ProductEntity> result = productDao.getProductById(savedProductId);

            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result.get()).usingRecursiveComparison()
                            .ignoringExpectedNullFields()
                            .isEqualTo(productEntity.assignId(savedProductId)),
                    () -> assertThat(result.get().getCreatedAt()).isNotNull(),
                    () -> assertThat(result.get().getUpdatedAt()).isNotNull()
            );
        }

        @Test
        @DisplayName("조회 시 ID에 해당하는 상품이 존재하지 않으면 빈 값을 반환한다.")
        void getEmpty() {
            Optional<ProductEntity> result = productDao.getProductById(-1L);

            assertThat(result).isEmpty();
        }
    }
}
