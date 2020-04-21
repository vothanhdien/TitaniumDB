package me.sharing.tidb.buz;

import lombok.extern.slf4j.Slf4j;
import me.sharing.tidb.dao.ITransactionDAO;
import me.sharing.tidb.dto.CreateOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dienvt
 */
@Component
@Slf4j
public class TransactionBusiness {

    @Autowired
    private ITransactionDAO dao;

    public void createOrder(long sourceId, long targetId, long amount) {

        long orderID = System.currentTimeMillis();
        log.info("create order {}", orderID);
        CreateOrderDTO dto = new CreateOrderDTO();
        dto.setOrderID(orderID);
        dto.setSourceID(sourceId);
        dto.setTargetID(targetId);
        dto.setAmount(amount);
        dto.setOrderTime(System.currentTimeMillis());

        dao.insert(dto);
    }
}
