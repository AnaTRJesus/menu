package br.com.mac.menu.service;

import br.com.mac.menu.dto.ProductDto;
import br.com.mac.menu.entity.Product;
import br.com.mac.menu.entity.ProductComponents;
import br.com.mac.menu.entity.ProductStatus;
import br.com.mac.menu.exception.BusinessException;
import br.com.mac.menu.repository.ProductComponentsRepository;
import br.com.mac.menu.repository.ProductRepository;
import br.com.mac.menu.repository.ProductStatusRepository;
import br.com.mac.menu.response.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;
    private final ProductComponentsRepository productComponentsRepository;
    private final static int PRODUCT = 1;
    private final static int CHOICE = 2;
    private final static int VALUE_MEAL = 3;
    private final static int ACTIVE = 1;
    private final static int INACTIVE = 0;

    public ProductService(ProductRepository productRepository,
                          ProductStatusRepository productStatusRepository,
                          ProductComponentsRepository productComponentsRepository){

        this.productRepository = productRepository;
        this.productStatusRepository = productStatusRepository;
        this.productComponentsRepository = productComponentsRepository;
    }

    public ProductResponse save(ProductDto dto) throws BusinessException {
        isChildProductListValid(dto);
        areThereProductsInTable(dto);
        isStatusValid(dto.getProductStatus());

        Product product = Product.builder().prdName(dto.getProductName()).type(1).build();
        Product productSaved = productRepository.save(product);

        ProductStatus productStatus = ProductStatus.builder().prdId(productSaved.getPrdId()).status(dto.getProductStatus()).build();
        ProductStatus productStatusSaved = productStatusRepository.save(productStatus);

        if (dto.getChildProductList().isEmpty()) {
            return ProductResponse.builder().prdId(productSaved.getPrdId()).productName(productSaved.getPrdName())
                    .productStatus(productStatusSaved.getStatus()).childProductList(dto.getChildProductList()).build();
        }

        ProductComponents productComponents = ProductComponents.builder().prdId(product.getPrdId()).childProducts(dto.getChildProductList()).build();
        ProductComponents productComponentsSaved = productComponentsRepository.save(productComponents);

        return ProductResponse.builder().prdId(productSaved.getPrdId()).productName(productSaved.getPrdName())
                .productStatus(productStatusSaved.getStatus()).childProductList(productComponentsSaved.getChildProducts()).build();
    }

    public ProductStatus updateStatus(Integer prdId) throws BusinessException {
        ProductStatus productStatus = productStatusRepository.findById(prdId).orElseThrow(() -> new BusinessException("Product not found"));
        if (productStatus.getStatus() == ACTIVE) {
            productStatus.setStatus(INACTIVE);
            return productStatusRepository.save(productStatus);
        }
        productStatus.setStatus(ACTIVE);
        return productStatusRepository.save(productStatus);
    }

    private void isStatusValid(int productStatus) throws BusinessException {
        if (productStatus != 1 && productStatus != 0){
            throw new BusinessException("INVALID PRODUCT STATUS, CHOOSE 1 TO ACTIVE OR 0 TO INACTIVE");
        }
    }

    private void isChildProductListValid(ProductDto dto) throws BusinessException {
        if (dto.getProductType() == PRODUCT && !dto.getChildProductList().isEmpty()){
            throw new BusinessException("PRODUCTS WITH PRODUCT TYPE MUST HAVE NO CHILD");
        }
        if (dto.getProductType() == CHOICE && dto.getChildProductList().size() != 2){
            throw new BusinessException("PRODUCTS WITH CHOICE TYPE MUST HAVE TWO CHILD");
        }
        if (dto.getProductType() == VALUE_MEAL && dto.getChildProductList().size() != 3){
            throw new BusinessException("PRODUCTS WITH CHOICE TYPE MUST HAVE THREE CHILD");
        }
    }

    private void areThereProductsInTable(ProductDto dto) throws BusinessException {
        if (!dto.getChildProductList().isEmpty()){
            List<Integer> listIds = productRepository.findAll().stream().map(product -> product.getPrdId()).collect(Collectors.toList());
            boolean isThereChild = listIds.containsAll(dto.getChildProductList());

            if (!isThereChild){
                throw new BusinessException("There is not this product child");
            }
        }
    }

    public void processUpdateEvent(ProductStatus productStatus) throws BusinessException {
        Product product = productRepository.findById(productStatus.getPrdId()).orElseThrow(() -> new BusinessException("Product not found"));
        if (product.getType() == VALUE_MEAL) {
            return;
        }

        if (productStatus.getStatus() == INACTIVE){
            tryInactiveProducts(productStatus);
            return;
        }
        tryActiveProducts(productStatus);
    }

    private void tryActiveProducts(ProductStatus productStatus) {
        List<ProductComponents> listChildProducts = productComponentsRepository.findByChildProducts(productStatus.getPrdId());

        listChildProducts.forEach(childProduct ->
        {
            Product product = productRepository.findById(childProduct.getPrdId()).get();
            ProductStatus productFinal = productStatusRepository.findById(childProduct.getPrdId()).get();

            if (product.getType() == CHOICE) {
                List<ProductStatus> listProductStatus = productStatusRepository.findByProductStatusIds(childProduct.getChildProducts());

                boolean areThereInactiveProduct = listProductStatus.stream().anyMatch(singleProductStatus -> singleProductStatus.getStatus() == ACTIVE);
                {
                    if (areThereInactiveProduct) {
                        productFinal.setStatus(ACTIVE);
                        productStatusRepository.save(productFinal);
                    }
                }
            }

            if (product.getType() == VALUE_MEAL) {
                List<ProductStatus> listProductStatus = productStatusRepository.findByProductStatusIds(childProduct.getChildProducts());

                int quantityActivateProducts = (int) listProductStatus
                        .stream()
                        .filter(c -> c.getStatus() == ACTIVE)
                        .count();

                if (quantityActivateProducts == VALUE_MEAL) {
                    productFinal.setStatus(ACTIVE);
                    productStatusRepository.save(productFinal);
                }
            }
        });
    }

    private void tryInactiveProducts(ProductStatus productStatus) {
        List<ProductComponents> listChildProducts = productComponentsRepository.findByChildProducts(productStatus.getPrdId());

        listChildProducts.forEach(childProduct ->
        {
            Product product = productRepository.findById(childProduct.getPrdId()).get();
            ProductStatus productFinal = productStatusRepository.findById(childProduct.getPrdId()).get();
            if (product.getType() == CHOICE) {

                List<ProductStatus> listProductsStatus = productStatusRepository.findByProductStatusIds(childProduct.getChildProducts());

                boolean areThereInactiveProduct = listProductsStatus.stream().anyMatch(singleProductStatus -> singleProductStatus.getStatus() == INACTIVE);
                {
                    if (areThereInactiveProduct) {
                        productFinal.setStatus(INACTIVE);
                        productStatusRepository.save(productFinal);
                    }
                }
            }

            if (productFinal.getStatus() != INACTIVE) {
                productFinal.setStatus(INACTIVE);
                productStatusRepository.save(productFinal);
            }
        });
    }
}
