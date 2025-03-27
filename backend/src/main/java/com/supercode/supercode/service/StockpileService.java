package com.supercode.supercode.service;

import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.StockpileVO;
import org.springframework.stereotype.Service;

@Service
public interface StockpileService {

    StockpileVO getStockpileById(String productId) throws Exception;

    MessageVO updateStockpile(String productId, StockpileVO stockpileVO) throws Exception;

}
