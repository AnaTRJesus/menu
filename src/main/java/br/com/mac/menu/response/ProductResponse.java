package br.com.mac.menu.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class ProductResponse {
    private Integer prdId;
    private String productName;
    private int productStatus;
    private List<Integer> childProductList;
}
