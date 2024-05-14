package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.dto.DebitConfirmationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@RabbitListener(queues = "DebitConfirmationQueue")
public class DebitConfirmationListener {

    @Autowired
    private AlbumService albumService;

    @RabbitHandler
    public void debitConfirmationHandler(DebitConfirmationDto debitConfirmationDto) {
        albumService.updateAlbumSale(debitConfirmationDto.getIsDebitConfirmed(), debitConfirmationDto.getUserEmail(), debitConfirmationDto.getIdSpotify());
    }
}