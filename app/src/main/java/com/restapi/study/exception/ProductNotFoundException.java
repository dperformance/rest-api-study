package com.restapi.study.exception;


import com.restapi.study.global.error.exception.EntityNotFoundException;

public class ProductNotFoundException extends EntityNotFoundException {

    public ProductNotFoundException(Long id) {
        super(id + " Product not found");
    }
}
