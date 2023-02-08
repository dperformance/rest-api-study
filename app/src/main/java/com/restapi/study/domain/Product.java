// Product Model
// User Model
// Order Model
// ... Model
// Application (UseCase)
// Product ->  관리자 등록/수정/삭제 -> List/detail/
// 사용자 주문 -> 확인 -> 배송 등 처리

// Product
// 0. 식별자(id) - identifier
// 1. 제품 이름(name) - 아우터
// 2. 제조사(maker) - 캐나다구스
// 3. 가격(price) - 1,000,000
// 4. 이미지(image) - static, CDN => image URL
package com.restapi.study.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String maker;

    private Integer price;

    private String imageUrl;

    @Builder
    public Product(Long id, String name, String maker, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.maker = maker;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void changeOf(Product source) {
        this.name = source.getName();
        this.maker = source.getMaker();
        this.price = source.getPrice();
        this.imageUrl = source.getImageUrl();
    }
}
