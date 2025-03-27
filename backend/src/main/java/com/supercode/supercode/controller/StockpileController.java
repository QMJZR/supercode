package com.supercode.supercode.controller;

import com.supercode.supercode.service.ProductService;
import com.supercode.supercode.service.StockpileService;
import com.supercode.supercode.vo.*;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/products/stockpile")
public class StockpileController {

    @Autowired
    StockpileService stockpileService;

    @PatchMapping("/{productId}")
    public ResultVO<MessageVO> patchStockpile(@PathVariable String productId,@RequestBody StockpileVO stockpileVO) throws Exception {
        return ResultVO.buildSuccess(stockpileService.updateStockpile(productId,stockpileVO));
    }

    @GetMapping("/{productId}")
    public ResultVO<StockpileVO> getStockpile(@PathVariable String productId) throws Exception {
        return ResultVO.buildSuccess(stockpileService.getStockpileById(productId));
    }
}
