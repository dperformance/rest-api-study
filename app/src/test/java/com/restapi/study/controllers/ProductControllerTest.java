package com.restapi.study.controllers;

import com.restapi.study.application.ProductService;
import com.restapi.study.domain.Product;
import com.restapi.study.dto.ProductRequestData;
import com.restapi.study.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final Long EXISTED_ID = 1L;

    private static final Long NOT_EXISTED_ID = 1000L;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        Product product= Product.builder()
                                .id(EXISTED_ID)
                                .name("outer")
                                .maker("goose")
                                .price(10000)
                                .imageUrl("goose.png")
                                .build();

        given(productService.getProducts())
                .willReturn(List.of(product));

        given(productService.getProduct(EXISTED_ID))
                .willReturn(product);

        given(productService.getProduct(NOT_EXISTED_ID))
                .willThrow(new ProductNotFoundException(NOT_EXISTED_ID));

        given(productService.createProduct(any(ProductRequestData.class)))
                .willReturn(product);

        given(productService.updateProduct(eq(EXISTED_ID), any(ProductRequestData.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                    ProductRequestData source = invocation.getArgument(1);
                    return new Product(
                                        id,
                                        source.getName(),
                                        source.getMaker(),
                                        source.getPrice(),
                                        source.getImageUrl());

                });

        given(productService.updateProduct(eq(NOT_EXISTED_ID), any(ProductRequestData.class)))
                .willThrow(new ProductNotFoundException(NOT_EXISTED_ID));

        given(productService.deleteProduct(NOT_EXISTED_ID))
                .willThrow(new ProductNotFoundException(NOT_EXISTED_ID));


    }

    @Test
    void list() throws Exception {
        mockMvc.perform(
                get("/products")
                        .accept(MediaType.APPLICATION_JSON)
                )
                        .andExpect(status().isOk())
                        .andExpect(content().string(containsString("goose")));
    }

    @Test
    void detailExistedProduct() throws Exception {
        mockMvc.perform(
                get("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("goose")));
    }

    @Test
    void detailWithNotExistedProduct() throws Exception {
        mockMvc.perform(
                get("/products/{id}", NOT_EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void create() throws Exception {
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outer\",\"maker\":\"goose\",\"price\":10000,\"imageUrl\":\"goose.png\"}")
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("goose.png")));

        verify(productService).createProduct(any(ProductRequestData.class));
    }
    @Test
    void createWithInvalidAttribute() throws Exception {
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"maker\":\"\",\"price\":10000,\"imageUrl\":\"goose.png\"}")

        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateExistedId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000,\"imageUrl\":\"goosee.png\"}")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("goosee.png")));

        verify(productService).updateProduct(eq(EXISTED_ID), any(ProductRequestData.class));
    }

    @Test
    void updateNotExistedId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", NOT_EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000,\"imageUrl\":\"goosee.png\"}")
        )
                .andExpect(status().isNotFound());

    }

    @Test
    void updateWithInvalidAttribute() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"maker\":\"\",\"price\":0,\"imageUrl\":\"goosee.png\"}")
        )
                .andExpect(status().isBadRequest());

    }

    @Test
    void deleteExistedId() throws Exception {
        mockMvc.perform(
          delete("/products/{id}", EXISTED_ID)
        )
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(eq(EXISTED_ID));
    }

    @Test
    void deleteNotExistedId() throws Exception {
        mockMvc.perform(
                delete("/products/{id}", NOT_EXISTED_ID)
        )
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(eq(NOT_EXISTED_ID));
    }


}