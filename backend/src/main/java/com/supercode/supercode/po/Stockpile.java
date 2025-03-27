package com.supercode.supercode.po;

import com.supercode.supercode.vo.StockpileVO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Stockpile {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "stockpile_id")
    private Integer stockpileId;

    @Basic
    @Column(name = "product_id", nullable = false, unique = true)
    private Integer productId;

    @Basic
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Basic
    @Column(name = "frozen", nullable = false)
    private Integer frozen;

    public StockpileVO toVO() {
        StockpileVO stockpileVO=new StockpileVO();
        stockpileVO.setProductId(String.valueOf(productId));
        stockpileVO.setAmount(amount);
        stockpileVO.setFrozen(frozen);
        return stockpileVO;
    }
}
