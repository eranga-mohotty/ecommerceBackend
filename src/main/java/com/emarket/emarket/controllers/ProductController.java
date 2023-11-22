package com.emarket.emarket.controllers;

import com.emarket.emarket.entities.Product;
import com.emarket.emarket.model.ServiceLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")

public class ProductController {

    @Autowired
    private ServiceLayer serviceLayer;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = serviceLayer.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }



    @GetMapping("/product/{id}")
    public ResponseEntity<Optional<Product>> getProduct(@PathVariable("id") Long id) {
        Optional<Product> product = serviceLayer.getProduct(id);
        if (product.isPresent()){
            return new ResponseEntity<>(product, HttpStatus.OK);

        }
        else{
            return new ResponseEntity<>(product, HttpStatus.NOT_FOUND);
        }
    }


}