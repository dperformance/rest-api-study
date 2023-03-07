package com.restapi.study.controllers;

import com.restapi.study.application.AuthenticationService;
import com.restapi.study.application.ProductService;
import com.restapi.study.domain.Product;
import com.restapi.study.dto.ProductRequestData;
import com.restapi.study.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final AuthenticationService authenticationService;

    public ProductController(
            ProductService productService,
            AuthenticationService authenticationService)
    {
        this.productService = productService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public Product detail(
            @PathVariable Long id) {

        return productService.getProduct(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid ProductRequestData productRequestData)
    {
        String accessToken = authorization.substring("Bearer ".length());
        authenticationService.parseToken(accessToken);

        return productService.createProduct(productRequestData);
    }

    @PatchMapping("/{id}")
    public Product update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody @Valid ProductRequestData productRequestData)
    {
        String accessToken = authorization.substring("Bearer ".length());
        authenticationService.parseToken(accessToken);

        return productService.updateProduct(id, productRequestData);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT
    )
    public Product delete(
            @PathVariable Long id) {
       return productService.deleteProduct(id);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleMissingRequestHeaderException() {

    }
}
