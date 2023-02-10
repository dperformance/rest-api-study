package com.restapi.study.application;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.restapi.study.domain.Product;
import com.restapi.study.domain.ProductRepository;
import com.restapi.study.dto.ProductRequestData;
import com.restapi.study.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ProductService {

    private final Mapper mapper;
    private final ProductRepository productRepository;

    public ProductService (
            Mapper dozerMapper,
            ProductRepository productRepository)
    {
        this.mapper = dozerMapper;
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return findProduct(id);
    }

    public Product createProduct(ProductRequestData productRequestData) {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        Product product = mapper.map(productRequestData, Product.class);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequestData source) {
        Product product = findProduct(id);

        product.changeWith(mapper.map(source, Product.class));

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
