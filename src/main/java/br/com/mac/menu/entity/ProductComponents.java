package br.com.mac.menu.entity;

import br.com.mac.menu.entity.config.CallBackProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductComponents implements Serializable {
    @Id
    private Integer prdId;

    @ElementCollection(targetClass=Integer.class, fetch = FetchType.EAGER)
    private List<Integer> childProducts;
}
