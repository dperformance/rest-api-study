package com.restapi.study.domain;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    Optional<Product> findById(Long existedId);

    Product save(Product product);

    void delete(Product product);
}
