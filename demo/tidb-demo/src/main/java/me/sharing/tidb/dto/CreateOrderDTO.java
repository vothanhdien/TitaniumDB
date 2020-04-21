package me.sharing.tidb.dto;

import lombok.Data;

/**
 * @author dienvt
 */
@Data
public class CreateOrderDTO{


    private long orderID;
    private long sourceID;
    private long targetID;
    private long amount;
    private long orderTime;
}
