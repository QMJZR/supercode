package com.supercode.supercode.po;

import com.supercode.supercode.vo.SpecificationVO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Specification {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "specification_id")
    private Integer specificationId;

    @Basic
    @Column(name = "item", nullable = false)
    private String item;

    @Basic
    @Column(name = "value", nullable = false)
    private String value;

    @Basic
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    public SpecificationVO toVO() {
        SpecificationVO specificationVO=new SpecificationVO();
        specificationVO.setId(specificationId.toString());
        specificationVO.setItem(item);
        specificationVO.setValue(value);
        specificationVO.setProductId(String.valueOf(productId));
        return specificationVO;
    }
}
