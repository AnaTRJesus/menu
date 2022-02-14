package br.com.mac.menu.controller;

import br.com.mac.menu.dto.ProductDto;
import br.com.mac.menu.exception.BusinessException;
import br.com.mac.menu.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<?> save(@RequestBody ProductDto dto)  {
        try {
            return new ResponseEntity<>(productService.save(dto), HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PutMapping(value = "/update-status/{id}", produces = "application/json")
    public ResponseEntity<?> updateStatus(@PathVariable Integer id)  {
        try {
            return new ResponseEntity<>(productService.updateStatus(id), HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
