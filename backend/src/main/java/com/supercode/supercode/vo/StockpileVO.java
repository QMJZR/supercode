package com.supercode.supercode.vo;

import com.supercode.supercode.po.Product;
import com.supercode.supercode.po.Stockpile;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class StockpileVO {
    @Nullable
    private String id;
    @Nullable
    private String productId;
    private Integer amount;
    @Nullable
    private Integer frozen;

    public Stockpile toPO() {
        Stockpile stockpile=new Stockpile();
        stockpile.setStockpileId(Integer.parseInt(id));
        stockpile.setProductId(Integer.valueOf(productId));
        stockpile.setAmount(amount);
        stockpile.setFrozen(frozen);
        return stockpile;
    }
}
