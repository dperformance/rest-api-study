package com.restapi.study.controllers;

import com.restapi.study.application.ProductService;
import com.restapi.study.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        Product product= Product.builder()
                .id(1L)
                .name("outer")
                .maker("goose")
                .price(10000)
                .imageUrl("goose.png")
                .build();

        given(productService.getProducts()).willReturn(List.of(product));

        given(productService.getProduct(1L)).willReturn(product);

//        given(productService.getProduct(100L)).willThrow()
    }

    @Test
    @Disabled
    void list() throws Exception {
        mockMvc.perform(
                get("/products")
                        .accept(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("goose")));
    }

    @Test
    @Disabled
    void detailWithExsitedProduct() throws Exception {
        mockMvc.perform(
                get("/products/1")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("goose")));
    }

    @Test
    void detailWithNotExsitedProduct() throws Exception {
        mockMvc.perform(
                get("/products/100")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }


}