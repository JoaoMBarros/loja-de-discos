package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.services.WalletServices;
import br.com.sysmap.bootcamp.dto.WalletDebitDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RabbitListener(queues = "WalletDebitQueue")
public class WalletDebitListener {
    @Autowired
    private WalletServices walletServices;

    @RabbitHandler
    public void walletDebitListener(WalletDebitDto walletDebitDto) {
        walletServices.debitWallet(walletDebitDto);
    }
}