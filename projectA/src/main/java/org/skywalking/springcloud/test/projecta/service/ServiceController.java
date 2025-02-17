package org.skywalking.springcloud.test.projecta.service;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.skywalking.springcloud.test.projecta.conf.RabbitConf;
import org.skywalking.springcloud.test.projecta.vo.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {


    @Autowired
    private RibbonCallService projectBServiceCall;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FeignCallService projectCServiceCall;

    @RequestMapping(value = "/projectA/{name}")
    @ResponseBody
    public Result hi(@PathVariable(required = false) String name) {
        projectBServiceCall.call(name);
        projectCServiceCall.call(name);
        rabbitTemplate.convertAndSend(RabbitConf.DEFAULT_QUEUE, name);
        return new Result(TraceContext.traceId());
    }

}
