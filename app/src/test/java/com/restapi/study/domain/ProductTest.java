// Product
// 0. 식별자(id) - identifier
// 1. 제품 이름(name) - 아우터
// 2. 제조사(maker) - nike
// 3. 가격(price) - 1,000,000
// 4. 이미지(image) - static, CDN => image URL

package com.restapi.study.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

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
}