package org.skywalking.springcloud.test.projectd.conf;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConf {

    public static final String DEFAULT_QUEUE = "skywalking-001";

    @Bean
    public Queue queue(){
        return new Queue(DEFAULT_QUEUE, true);
    }
}
