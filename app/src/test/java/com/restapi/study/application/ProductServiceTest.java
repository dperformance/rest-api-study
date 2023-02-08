package com.restapi.study.application;

import com.restapi.study.domain.Product;
import com.restapi.study.domain.ProductRepository;
import com.restapi.study.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository
            = mock(ProductRepository.class);
    private static final Long EXISTED_ID = 1L;
    private static final Long NOT_EXISTED_ID = 1000L;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);

        Product product = Product.builder()
                .id(1L)
                .name("outer")
                .maker("goose")
                .price(10000)
                .imageUrl("goose.png")
                .build();

        given(productRepository.findAll()).willReturn(List.of(product));

        given(productRepository.findById(EXISTED_ID)).willReturn(Optional.of(product));

        given(productRepository.save(any(Product.class))).will(invocation -> {
            Product source = invocation.getArgument(0);
            return Product.builder()
                    .id(2L)
                    .name(source.getName())
                    .maker(source.getMaker())
                    .price(source.getPrice())
                    .imageUrl(source.getImageUrl())
                    .build();
        });



    }

    @Test
    void getProductsWithNoProducts() {
        given(productRepository.findAll()).willReturn(List.of());
        assertThat(productService.getProducts()).isEmpty();
    }

    @Test
    void getProducts() {
        List<Product> products = productService.getProducts();

        assertThat(products).isNotEmpty();

        Product product = products.get(0);

        assertThat(product.getMaker()).isEqualTo("goose");
    }

    @Test
    void getProduct() {
        Product product = productService.getProduct(EXISTED_ID);

        assertThat(product).isNotNull();

        assertThat(product.getMaker()).isEqualTo("goose");
    }

    @Test
    void getProductWithNotExistedId() {
        assertThatThrownBy(() -> productService.getProduct(NOT_EXISTED_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void createProduct() {
        Product productData = Product.builder()
                                .id(1L)
                                .name("outer")
                                .maker("goose")
                                .price(10000)
                                .imageUrl("goose.png")
                                .build();
        Product product = productService.createProduct(productData);

        verify(productRepository).save(any(Product.class));

        assertThat(product.getId()).isEqualTo(2L);
        assertThat(product.getName()).isEqualTo("outer");
        assertThat(product.getMaker()).isEqualTo("goose");
    }

    @Test
    void updateProduct() {
        Product productData = Product.builder()
                                .id(1L)
                                .name("아우터")
                                .maker("구스")
                                .price(10000)
                                .imageUrl("goose.png")
                                .build();

        Product product = productService.updateProduct(EXISTED_ID, productData);

        assertThat(product.getName()).isEqualTo("아우터");
    }

    @Test
    void updateProductWithNotExistedId() {
        Product source = Product.builder()
                                .name("아우터")
                                .maker("구스")
                                .price(20000)
                                .imageUrl("구스.png")
                                .build();

        assertThatThrownBy(() -> productService.updateProduct(NOT_EXISTED_ID, source))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deleteProduct() {
        productService.deleteProduct(EXISTED_ID);

        verify(productRepository).delete(any(Product.class));
    }

    @Test
    void deleteProductWithNotExistedID() {
        assertThatThrownBy(() -> productService.deleteProduct(NOT_EXISTED_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

}