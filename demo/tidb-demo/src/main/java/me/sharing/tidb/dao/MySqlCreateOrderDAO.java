package me.sharing.tidb.dao;

import me.sharing.tidb.dto.CreateOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author dienvt
 */
@Component
public class MySqlCreateOrderDAO implements ITransactionDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void insert(CreateOrderDTO dto) {
        jdbcTemplate.update("INSERT INTO `CreatedOrderLog` (`orderID`, `sourceID`, `targetID`, `amount`, `orderTime`) VALUES (?, ?, ?, ?, ?)",
                dto.getOrderID(),
                dto.getSourceID(),
                dto.getTargetID(),
                dto.getAmount(),
                dto.getOrderTime());
    }
}
