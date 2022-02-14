package br.com.mac.menu.entity;

import br.com.mac.menu.entity.config.CallBackProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(CallBackProduct.class)
public class ProductStatus implements Serializable {
    @Id
    private Integer prdId;

    private int status;
}
