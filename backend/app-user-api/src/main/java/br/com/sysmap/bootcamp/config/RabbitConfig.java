package br.com.sysmap.bootcamp.config;

import br.com.sysmap.bootcamp.domain.listeners.WalletCreationListener;
import br.com.sysmap.bootcamp.domain.listeners.WalletDebitListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Configuration
public class RabbitConfig {

    @Bean
    public RestTemplate restTemplate() { return new RestTemplate(); }

    @Bean
    public Queue walletCreationQueue() { return new Queue("WalletCreationQueue"); }

    @Bean
    public WalletCreationListener walletCreationListener() {
        return new WalletCreationListener();
    }

    @Bean
    public Queue walletDebitQueue() { return new Queue("WalletDebitQueue"); }

    @Bean
    public WalletDebitListener walletDebitListener() {
        return new WalletDebitListener();
    }

    @Bean
    public SimpleMessageConverter converter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("org.springframework.amqp.core.Message", "br.com.sysmap.bootcamp.dto.*", "java.util.*", "java.time.*", "java.math.*", "java.lang.Boolean"));
        return converter;
    }

}
