package com.restapi.study.application;

import com.restapi.study.domain.Product;
import com.restapi.study.domain.ProductRepository;
import com.restapi.study.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService (ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product source) {
        Product product = findProduct(id);

        product.changeOf(source);

        return product;
    }

    public Product deleteProduct(long id) {
        Product product = findProduct(id);

        productRepository.delete(product);

        return product;
    }

    public Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
