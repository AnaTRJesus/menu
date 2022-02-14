package br.com.mac.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ProductDto {
    private String productName;
    private int productStatus;
    private Integer productType;
    private List<Integer> childProductList;
}
