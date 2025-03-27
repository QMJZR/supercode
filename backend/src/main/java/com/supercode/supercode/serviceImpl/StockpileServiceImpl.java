package com.supercode.supercode.serviceImpl;

import com.supercode.supercode.exception.SupercodeException;
import com.supercode.supercode.po.Stockpile;
import com.supercode.supercode.repository.StockpileRepository;
import com.supercode.supercode.service.StockpileService;
import com.supercode.supercode.vo.MessageVO;
import com.supercode.supercode.vo.StockpileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockpileServiceImpl implements StockpileService {
    @Autowired
    StockpileRepository stockpileRepository;

    @Override
    public StockpileVO getStockpileById(String productId) throws Exception {
        try {
            return stockpileRepository.findByProductId(Integer.valueOf(productId)).toVO();
        } catch (Exception e) {
            throw SupercodeException.productNotExisted();
        }
    }

    @Override
    public MessageVO updateStockpile(String productId, StockpileVO stockpileVO) throws Exception {
        try {
            if(stockpileRepository.findByProductId(Integer.valueOf(productId))==null)
                throw SupercodeException.productNotExisted();
            Stockpile stockpile=stockpileRepository.findByProductId(Integer.valueOf(productId));
            stockpile.setAmount(stockpileVO.getAmount());
            stockpileRepository.save(stockpile);
            return new MessageVO("调整库存成功");
        } catch (Exception e) {
            throw SupercodeException.productNotExisted();
        }
    }
}
