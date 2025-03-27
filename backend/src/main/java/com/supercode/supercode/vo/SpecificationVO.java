package com.supercode.supercode.vo;

import cn.hutool.crypto.digest.DigestUtil;
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
public class SpecificationVO {
    @Nullable
    private String id;
    private String item;
    private String value;
    @Nullable
    private String productId;

    public Specification toPO(Integer productId) {
        Specification specification=new Specification();
        specification.setItem(item);
        specification.setValue(value);
        specification.setProductId(productId);
        return specification;
    }
}
