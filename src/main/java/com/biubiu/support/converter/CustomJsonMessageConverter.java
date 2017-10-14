package com.biubiu.support.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;

/**
 * 自定义json消息解析器
 * Created by zhanghaibiao on 2017/9/27.
 */
public class CustomJsonMessageConverter implements MessageConverter {

    @Override
    public Object fromMessage(Message<?> message, Class<?> targetClass) {
        return null;
    }

    @Override
    public Message<?> toMessage(Object payload, MessageHeaders headers) {
        return new GenericMessage<>(JSON.toJSONString(payload, SerializerFeature.DisableCircularReferenceDetect).getBytes(), headers);
    }

}
