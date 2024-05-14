package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.services.WalletServices;
import br.com.sysmap.bootcamp.dto.WalletCreationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RabbitListener(queues = "WalletCreationQueue")
public class WalletCreationListener {

    @Autowired
    private WalletServices walletServices;

    @RabbitHandler
    public void walletCreationListener(WalletCreationDto walletCreationDto) {
        walletServices.createWallet(walletCreationDto);
    }
}