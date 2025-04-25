package org.example.healbackend.controller;

import org.example.healbackend.bean.MessageStatus;
import org.example.healbackend.service.MessageService;
import org.example.healbackend.bean.Message;
import org.example.healbackend.bean.User;

import org.example.healbackend.service.UserService;
import org.example.healbackend.Utils.Result;
import org.example.healbackend.Utils.TimeUtil;
import org.example.healbackend.Utils.UrlUtil;
import org.example.healbackend.Utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.*;


@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService  messageService;
    @Autowired
    private UserService userService;
    @Operation(summary = "获取用户最近会话列表", description = "根据用户ID获取最近的会话消息列表，包括未读消息数等信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("list/{userId}")
    public Result getMessageList(
            @PathVariable int userId
    ){
        try {
            List<Message> notices=messageService.selectRecentConversations(userId);
            System.out.println(notices);

            List<Map<String, Object>> messageList = new ArrayList<>();
            for (Message notice:notices){
                Long receiveId=(long)(userId==notice.getUserId()?notice.getReceiveId():notice.getUserId());
                System.out.println("receiveId:"+receiveId);
                User user=userService.getUserById(receiveId.intValue());
                System.out.println("user:"+user);
                Map<String, Object> message = new LinkedHashMap<>();

                message.put("userId", user.getId());
                message.put("userName", user.getUsername());
                message.put("avatar", UrlUtil.getUrl(user.getAvatarUrl()));

                message.put("lastMessageContent", notice.getContent());
                message.put("lastMessageTime", notice.getCreateTime());
                message.put("lastMessageSender", user.getId());
//                int receiveId=(notice.getUserId()==userId)?notice.getReceiveId():notice.getUserId();
                int unreadCount=messageService.countUnreadNotices(userId,receiveId.intValue());
                message.put("unreadCount", unreadCount);
                messageList.add(message);
            }
            System.out.println("构造后的数据："+messageList);
            return Result.success().setData(messageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error();
    }

    @Operation(summary = "获取所有需要咨询的聊天用户", description = "根据用户ID获取所有需要咨询的聊天用户列表，包括用户ID、头像等信息")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/partners/{userId}")
    public Result getUserExistingPartners(@PathVariable Integer userId) {
        try {
            // 获取用户的聊天伙伴列表
            HashSet<Integer> partnerIds = messageService.getUserExistingPartners(userId);

            // 将聊天伙伴信息转换为所需的格式
            List<Map<String, Object>> partnerDetails = new ArrayList<>();

            for (Integer partnerId : partnerIds) {
                User partner = userService.getUserById(partnerId); // 获取用户信息
                if (partner != null) {
                    Map<String, Object> partnerData = new LinkedHashMap<>();
                    partnerData.put("userId", partner.getId());
                    partnerData.put("userName", partner.getUsername());
                    partnerData.put("avatar", UrlUtil.getUrl(partner.getAvatarUrl()));
                    partnerDetails.add(partnerData);
                }
            }

            return Result.success().setData(partnerDetails);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().setMessage("获取聊天伙伴失败");
        }
    }

    @GetMapping("allChat/list/{userId}")
    public Result getUserList(@PathVariable Integer userId){
        // 获取该用户的所有最近会话
        List<Message> conversations = messageService.selectRecentConversations(userId);
        // 构建返回数据
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message conversation : conversations) {
            int partnerId = (userId.equals(conversation.getUserId()) ?
                    conversation.getReceiveId() : conversation.getUserId());
            User partner = userService.getUserById(partnerId);
            Map<String, Object> userData = UserUtil.convertUserToMap(partner);
            // 获取未读消息数
            int unreadCount = messageService.countUnreadNotices(userId, partnerId);

            // 获取与该用户的所有历史消息
            List<Message> history = messageService.selectConversation(userId, partnerId);

            Map<String, Object> chatData = new LinkedHashMap<>();
            chatData.put("isConsultants", userId);
            chatData.put("partnerId", partnerId);
            chatData.put("user", userData);
            chatData.put("unreadCount", unreadCount);
            chatData.put("history", history);

            messageList.add(chatData);
        }
        // 发送聊天历史和未读消息数给客户端
        return Result.success().setData(messageList);
    }
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送一条消息到指定接收者")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "消息对象，发送时间后端设置", required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                            example = "{\"userId\": 0, \"receiveId\": 1, \"content\": \"Hello, world!\"}"
                    )))
    public Result addMessage(@RequestBody() Message notice) {
        try {
            // 设置当前时间
            String time = TimeUtil.getCurrentTimeString();
            notice.setCreateTime(time);

            // 确保字段类型一致，避免返回Double类型
            notice.setUserId(notice.getUserId()); // 如果是Double，转为Int
            notice.setReceiveId(notice.getReceiveId()); // 如果是Double，转为Int
            notice.setStatus(notice.getStatus()); // 如果是Double，转为Int

            // 插入消息
            boolean success = messageService.insertNotice(notice);
            if (success) {
                // 返回消息时，将status设置为-1表示不返回已读状态
                notice.setStatus(-1);  //不返回已读状态

                // 确保返回的对象是符合预期格式的
                return Result.success().data(notice);  // 返回成功结果
            }
            return Result.error();  // 返回错误
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error().setMessage("发送消息失败");
        }
    }



    @Operation(summary = "获取聊天历史", description = "获取与指定用户的聊天历史记录")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "包含userId和receiveId的JSON对象", required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(
                            name = "jsonNode",
                            example = "{\"userId\": 0, \"receiveId\": 1}")
            ))
    @PostMapping("/chat")
    public Result getChatHistory(@RequestBody JsonNode jsonNode) {
        try {
            // 检查请求参数
            if (jsonNode == null || !jsonNode.has("userId") || !jsonNode.has("receiveId")) {
                return Result.error().setMessage("请求参数缺失或为空");
            }

            Long userId = jsonNode.get("userId").asLong();  // 获取 userId
            Long receiveId = jsonNode.get("receiveId").asLong();  // 获取 receiveId

            // 查询聊天记录
            List<Message> notices = messageService.selectConversation(userId.intValue(), receiveId.intValue());

            if (notices == null || notices.isEmpty()) {
                return Result.error().setMessage("没有找到聊天记录");
            }

            // 隐藏已读状态，使用枚举代替硬编码
            for (Message notice : notices) {
                notice.setStatus(MessageStatus.HIDDEN.getStatus());
            }

            return Result.success().setData(notices);
        } catch (Exception e) {
            // 记录错误日志
            return Result.error().setMessage("获取聊天历史失败");
        }
    }



    @PostMapping("/read")
    @Operation(summary = "标记消息为已读", description = "将指定用户间的所有消息标记为已读状态")
    public Result readMessage(
            @RequestBody() JsonNode jsonNode
    ){
        try {
            if (jsonNode == null || !jsonNode.has("userId") || !jsonNode.has("receiveId")) {
                return Result.error().setMessage("请求参数缺失或为空");
            }

            Long userId = jsonNode.get("userId").asLong();
            Long receiveId = jsonNode.get("receiveId").asLong();
            messageService.updateMessageStatusByUser(userId.intValue(), receiveId.intValue(), 1);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }

    @PostMapping("/unread")
    @Operation(summary = "获取未读消息", description = "获取与指定用户间的所有未读消息")
    public Result unReadMessage(
            @RequestBody() JsonNode jsonNode
    ){
        try {
            if (jsonNode == null || !jsonNode.has("userId") || !jsonNode.has("receiveId")) {
                return Result.error().setMessage("请求参数缺失或为空");
            }

            Long userId = jsonNode.get("userId").asLong();
            Long receiveId = jsonNode.get("receiveId").asLong();
            List<Message> notices = messageService.selectRecentNoticesByUserAndReceiver(userId.intValue(), receiveId.intValue());
            for (Message notice : notices) {
                notice.setStatus(-1);  // 隐藏已读状态
            }
            System.out.println("获取未读消息：" + notices);
            return Result.success().setData(notices);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }


    /**
     * 获取新聊天用户及其最近消息
     *
     * @param userId 用户ID
     * @return 新聊天用户及其最近消息列表
     */
    @Operation(summary = "获取新聊天用户及其最近消息", description = "获取自上次刷新后新增的聊天用户及其最近一条消息，服务器自动管理刷新时间和已存在聊天伙伴")
    @Parameter(name = "userId", description = "用户ID", required = true)
    @GetMapping("/new-chat-users/{userId}")
    public Result getNewChatUsers(
            @PathVariable int userId
    ) {
        try {
            // 调用服务层方法获取新聊天用户及其最近消息
            // 服务器会自动管理existingPartnerIds和lastRefreshTime
            List<Map<String, Object>> newChatUsers = messageService.findNewChatUsers(
                    userId
            );

            return Result.success().setData(newChatUsers);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error();
        }
    }
}
