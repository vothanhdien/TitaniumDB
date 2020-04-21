package me.sharing.tidb.rep;

import me.sharing.tidb.dto.CreateOrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author dienvt
 */
@Component
public class TransactionRepository {

    @Autowired
    private DataSource dataSource;

    public boolean insert(CreateOrderDTO dto){

        return false;
    }
}
