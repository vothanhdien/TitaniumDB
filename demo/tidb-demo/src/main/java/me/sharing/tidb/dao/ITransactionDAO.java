package me.sharing.tidb.dao;

import me.sharing.tidb.dto.CreateOrderDTO;

/**
 * @author dienvt
 */
public interface ITransactionDAO {


    public void insert(CreateOrderDTO dto);
}
