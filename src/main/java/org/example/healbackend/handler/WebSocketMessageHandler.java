package org.example.healbackend.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.healbackend.service.MessageService;
import org.example.healbackend.service.UserService;
import org.example.healbackend.Utils.TimeUtil;
import org.example.healbackend.bean.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从session中获取用户ID并保存会话
        Integer userId = Integer.parseInt(session.getUri().getQuery().split("=")[1]);
        userSessions.put(userId, session);

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode jsonNode = objectMapper.readTree(payload);

        // 解析消息内容
        Message chatMessage = new Message();
        chatMessage.setUserId(jsonNode.get("userId").asInt());
        chatMessage.setReceiveId(jsonNode.get("receiveId").asInt());
        chatMessage.setContent(jsonNode.get("content").asText());

        String time = TimeUtil.getCurrentTimeString();
        chatMessage.setCreateTime(time);
        // 保存消息到数据库
        boolean success = messageService.insertNotice(chatMessage);

        if (success) {
            // 发送消息给接收者
            WebSocketSession receiverSession = userSessions.get(chatMessage.getReceiveId());
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 移除已关闭的会话
        Integer userId = Integer.parseInt(session.getUri().getQuery().split("=")[1]);
        userSessions.remove(userId);
    }
}