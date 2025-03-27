package com.supercode.supercode.po;

import com.supercode.supercode.vo.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Product {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Basic
    @Column(name = "title", nullable = false)
    private String title;

    @Basic
    @Column(name = "price", nullable = false,columnDefinition="decimal(18,2)")
    private BigDecimal price;

    @Basic
    @Column(name = "rate", nullable = false,columnDefinition="decimal(18,2)")
    private double rate;

    @Basic
    @Column(name = "description")
    private String description;

    @Basic
    @Column(name = "cover")
    private String cover;

    @Basic
    @Column(name = "detail")
    private String detail;

    public ProductVO toVO(Set<SpecificationVO> specificationVOSet) {
        ProductVO productVO=new ProductVO();
        productVO.setId(productId.toString());
        productVO.setTitle(title);
        productVO.setPrice(price);
        productVO.setRate(rate);
        productVO.setDescription(description);
        productVO.setCover(cover);
        productVO.setDetail(detail);
        productVO.setSpecifications(specificationVOSet);
        return productVO;
    }

}
