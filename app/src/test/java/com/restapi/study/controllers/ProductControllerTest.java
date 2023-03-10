package com.restapi.study.controllers;

import com.restapi.study.application.AuthenticationService;
import com.restapi.study.application.ProductService;
import com.restapi.study.domain.Product;
import com.restapi.study.domain.Role;
import com.restapi.study.dto.ProductRequestData;
import com.restapi.study.exception.InvalidTokenException;
import com.restapi.study.exception.ProductNotFoundException;
import com.restapi.study.global.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    private static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9." +
            "eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

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

        given(authenticationService.parseToken(VALID_TOKEN)).willReturn(EXISTED_ID);

        given(authenticationService.parseToken(INVALID_TOKEN))
                        .willThrow(new InvalidTokenException(INVALID_TOKEN));

        given(authenticationService.parseToken(null))
                        .willThrow(new InvalidTokenException(null));

        given(authenticationService.roles(1L))
                .willReturn(Arrays.asList(new Role("USER")));


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
                get("/products/{id}", NOT_EXISTED_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void createWithValidAttributes() throws Exception {
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outer\",\"maker\":\"goose\"," +
                                "\"price\":10000,\"imageUrl\":\"goose.png\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
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
                        .content("{\"name\":\"\",\"maker\":\"\"," +
                                "\"price\":10000,\"imageUrl\":\"goose.png\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)

        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithoutAccessToken() throws Exception {
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outer\",\"maker\":\"goose\"," +
                                "\"price\":10000,\"imageUrl\":\"goose.png\"}")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithWrongAccessToken() throws Exception {
        mockMvc.perform(
                post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outer\",\"maker\":\"goose\"," +
                                "\"price\":10000,\"imageUrl\":\"goose.png\"}")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateExistedId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000," +
                                "\"imageUrl\":\"goosee.png\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("goosee.png")));

        verify(productService).updateProduct(eq(EXISTED_ID), any(ProductRequestData.class));
    }

    @Test
    void updateWithoutAccessToken() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000," +
                                "\"imageUrl\":\"goosee.png\"}")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateWithInvalidAccessToken() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000," +
                                "\"imageUrl\":\"goosee.png\"}")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateNotExistedId() throws Exception {
        mockMvc.perform(
                patch("/products/{id}", NOT_EXISTED_ID)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"outerr\",\"maker\":\"goosee\",\"price\":100000,\"imageUrl\":\"goosee.png\"}")
                        .header("Authorization", "Bearer " + VALID_TOKEN)
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
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isBadRequest());

    }

    @Test
    void destroyWithExistedProduct() throws Exception {
        mockMvc.perform(
          delete("/products/{id}", EXISTED_ID)
                  .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(eq(EXISTED_ID));
    }

    @Test
    void destroyWithNotExistedProduct() throws Exception {
        mockMvc.perform(
                delete("/products/{id}", NOT_EXISTED_ID)
                        .header("Authorization", "Bearer " + VALID_TOKEN)
        )
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(eq(NOT_EXISTED_ID));
    }

    @Test
    void destroyWithoutAccessToken() throws Exception {
        mockMvc.perform(
                patch("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                "\"price\":5000}")
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void destroyWithInvalidAccessToken() throws Exception {
        mockMvc.perform(
                patch("/products/1")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"쥐순이\",\"maker\":\"냥이월드\"," +
                                "\"price\":5000}")
                        .header("Authorization", "Bearer " + INVALID_TOKEN)
        )
                .andExpect(status().isUnauthorized());
    }


}