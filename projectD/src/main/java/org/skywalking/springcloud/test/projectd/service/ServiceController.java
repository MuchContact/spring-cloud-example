package org.skywalking.springcloud.test.projectd.service;

import com.rabbitmq.client.Channel;
import org.apache.skywalking.apm.agent.core.base64.Base64;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.network.trace.component.ComponentsDefine;
import org.skywalking.springcloud.test.projectd.conf.RabbitConf;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class ServiceController {
//    @Trace
    @RabbitListener(queues = RabbitConf.DEFAULT_QUEUE)
    public void home(Channel channel, Message message) throws InterruptedException, IOException {
//        ContextCarrier contextCarrier = generateContextCarrier((String) message.getMessageProperties().getHeaders().get("sw8"));
//        AbstractSpan entrySpan = ContextManager.createEntrySpan("projectd-consumer", contextCarrier);
////        AbstractSpan entrySpan = ContextManager.createExitSpan("projectd-consumer", contextCarrier, "localhost:8080");
//        entrySpan.start();
//        Thread.sleep(2 * 1000);
//        System.out.println(TraceContext.traceId());
//        System.out.println(message.getMessageProperties());
//        System.out.println(new String(message.getBody(), Charset.defaultCharset()));
//        ContextManager.awaitFinishAsync(entrySpan);
//        ContextManager.stopSpan();

        AbstractSpan activeSpan = ContextManager.createEntrySpan("RabbitMQ/ddd", null).start(System.currentTimeMillis());
        activeSpan.setComponent(ComponentsDefine.RABBITMQ_CONSUMER);
        SpanLayer.asMQ(activeSpan);
        ContextCarrier contextCarrier = new ContextCarrier();
        CarrierItem next = contextCarrier.items();

        while (next.hasNext()) {
            next = next.next();
            MessageProperties properties = message.getMessageProperties();
            if (properties.getHeaders() != null && properties.getHeaders().get(next.getHeadKey()) != null) {
                next.setHeadValue(properties.getHeaders().get(next.getHeadKey()).toString());
            }
        }
        ContextManager.extract(contextCarrier);

        Thread.sleep(2 * 1000);
        System.out.println(contextCarrier.getTraceId());

        ContextManager.stopSpan();

    }

    private ContextCarrier generateContextCarrier(String text) {
        ContextCarrier carrier = new ContextCarrier();
        String[] parts = text.split("-", 8);
        if (parts.length == 8) {
            try {
                String traceId = Base64.decode2UTFString(parts[1]);
                reflectInvoke(carrier, "setTraceId", traceId);
//                ContextManager.extract();
                String traceSegmentId = Base64.decode2UTFString(parts[2]);
                reflectInvoke(carrier, "setTraceSegmentId", traceSegmentId);
                Integer spanId = Integer.parseInt(parts[3]);
                reflectInvokeInt(carrier, "setSpanId", spanId);
                String parentService = Base64.decode2UTFString(parts[4]);
                reflectInvoke(carrier, "setParentService", parentService);
                String parentServiceInstance = Base64.decode2UTFString(parts[5]);
                reflectInvoke(carrier, "setParentServiceInstance", parentServiceInstance);
                String parentEndpoint = Base64.decode2UTFString(parts[6]);
                reflectInvoke(carrier, "setParentEndpoint", parentEndpoint);
                String addressUsedAtClient = Base64.decode2UTFString(parts[7]);
                reflectInvoke(carrier, "setAddressUsedAtClient",addressUsedAtClient);
            } catch (IllegalArgumentException var5) {
                ;
            }
        }
        return carrier;
    }

    private void reflectInvoke(ContextCarrier carrier, String s, String traceId) {
        try {
            Method m2 = ContextCarrier.class.getDeclaredMethod(s, String.class);
            m2.setAccessible(true);
            m2.invoke(carrier, traceId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    private void reflectInvokeInt(ContextCarrier carrier, String s, Integer traceId) {
        try {
            Method m2 = ContextCarrier.class.getDeclaredMethod(s, int.class);
            m2.setAccessible(true);
            m2.invoke(carrier, traceId);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
