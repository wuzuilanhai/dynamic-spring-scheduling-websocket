package com.biubiu.config;

import com.alibaba.fastjson.JSON;
import com.biubiu.constant.WebSocketConstants;
import com.biubiu.constant.WebSocketMessageConstants;
import com.biubiu.model.OpenWebSocketRequest;
import com.biubiu.support.cache.CustomCacheHolder;
import com.biubiu.support.converter.CustomJsonMessageConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghaibiao on 2017/9/11.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Autowired
    private CustomCacheHolder customCacheHolder;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(WebSocketConstants.DESTINATION_PREFIX);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.addEndpoint(WebSocketConstants.PATH).setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(new ChannelInterceptorAdapter() {

            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String simpSessionId = (String) getMessageInnerContent(message, WebSocketMessageConstants.SIMP_SESSION_ID);
                    customCacheHolder.putSession(simpSessionId);
                } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String simpSessionId = (String) getMessageInnerContent(message, WebSocketMessageConstants.SIMP_SESSION_ID);
                    Object nativeHeaders = getMessageInnerContent(message, WebSocketMessageConstants.NATIVE_HEADERS);
                    if (nativeHeaders instanceof Map) {
                        Map<String, LinkedList> headersMap = (Map) nativeHeaders;
                        OpenWebSocketRequest payload = JSON.parseObject((String) headersMap.get(WebSocketMessageConstants.PAYLOAD).getFirst(), OpenWebSocketRequest.class);
                        InquiryRequest request = new InquiryRequest();
                        BeanUtils.copyProperties(payload, request);
                        String destination = split((String) getMessageInnerContent(message, WebSocketMessageConstants.SIMP_DESTINATION));
                        customCacheHolder.put(simpSessionId, destination, request, payload.getCron());
                    }
                } else if (StompCommand.DISCONNECT.equals(accessor.getCommand()) && getMessageInnerContent(message, WebSocketMessageConstants.SIMP_HEART_BEAT) == null) {
                    //这里有坑，disconnect时会先发个heartbeat，然后再发一次没有带heartbeat的disconnect
                    String simpSessionId = (String) getMessageInnerContent(message, WebSocketMessageConstants.SIMP_SESSION_ID);
                    customCacheHolder.removeSession(simpSessionId);
                }
            }
        });
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        super.configureClientOutboundChannel(registration);
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        messageConverters.add(new CustomJsonMessageConverter());
        return super.configureMessageConverters(messageConverters);
    }

    private Object getMessageInnerContent(Message<?> message, String type) {
        return message.getHeaders().get(type);
    }

    private String split(String string) {
        return string.substring(WebSocketConstants.DESTINATION_PREFIX.length() + 1);
    }

}
