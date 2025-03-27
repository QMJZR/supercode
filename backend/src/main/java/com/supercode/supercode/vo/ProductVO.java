package com.supercode.supercode.vo;

import cn.hutool.crypto.digest.DigestUtil;
import com.supercode.supercode.po.Product;
import com.supercode.supercode.po.Specification;
import com.supercode.supercode.po.User;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ProductVO {
    @Nullable
    private String id;
    private String title;
    private BigDecimal price;
    private Double rate;
    @Nullable
    private String description;
    @Nullable
    private String cover;
    @Nullable
    private String detail;
    @Nullable
    private Set<SpecificationVO> specifications;

    public Product toPO() {
        Product product=new Product();
        if(id!=null)
            product.setProductId(Integer.getInteger(id));
        product.setTitle(title);
        product.setPrice(price);
        product.setRate(rate);
        if(description!=null)
            product.setDescription(description);
        if(cover!=null)
            product.setCover(cover);
        if(detail!=null)
            product.setDetail(detail);
        return product;
    }
}
