// Product
// 0. 식별자(id) - identifier
// 1. 제품 이름(name) - 아우터
// 2. 제조사(maker) - nike
// 3. 가격(price) - 1,000,000
// 4. 이미지(image) - static, CDN => image URL

package com.restapi.study.domain;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.restapi.study.dto.ProductRequestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    private Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    @Test
    void creationWithBuilder() {
        Product product = Product.builder()
                .id(1L)
                .name("아우터")
                .maker("nike")
                .price(5000)
                .build();

        assertThat(product.getId()).isEqualTo(1L);
        assertThat(product.getName()).isEqualTo("아우터");
        assertThat(product.getMaker()).isEqualTo("nike");
        assertThat(product.getPrice()).isEqualTo(5000);
        assertThat(product.getImageUrl()).isNull();
    }

    @Test
    void changeWith() {
        Product product = Product.builder()
                .id(1L)
                .name("outer")
                .maker("goose")
                .price(10000)
                .imageUrl("goose.png")
                .build();
        ProductRequestData source = ProductRequestData.builder()
                .name("아우터")
                .maker("구스")
                .price(20000)
                .imageUrl("구스.png")
                .build();

        product.changeWith(mapper.map(source, Product.class));

        assertThat(product.getName()).isEqualTo("아우터");
        assertThat(product.getMaker()).isEqualTo("구스");
        assertThat(product.getPrice()).isEqualTo(20000);
        assertThat(product.getImageUrl()).isEqualTo("구스.png");
    }
}