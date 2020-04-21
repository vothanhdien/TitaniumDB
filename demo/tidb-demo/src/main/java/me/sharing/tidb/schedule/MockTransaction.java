package me.sharing.tidb.schedule;

import lombok.extern.slf4j.Slf4j;
import me.sharing.tidb.buz.TransactionBusiness;
import me.sharing.tidb.dto.CreateOrderDTO;
import me.sharing.tidb.rep.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author dienvt
 */
@Component
@Slf4j
public class MockTransaction {
    private static final long SOURCE_ID = 200401000000008L;
    private static final long TARGET_ID = 200420008232571L;

    @Autowired
    private TransactionBusiness business;

    @Scheduled( fixedDelay = 5000)
    public void mockTransaction() {
        business.createOrder(SOURCE_ID, TARGET_ID, 10000L);

        boolean isSuccess = new Random().nextBoolean();
        if(isSuccess){
            insertSuccess();
        }else{
            insertFail();
        }


    }

    private void insertSuccess() {

    }

    private void insertFail() {

    }
}
