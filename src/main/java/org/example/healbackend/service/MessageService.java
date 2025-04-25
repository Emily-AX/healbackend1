package org.example.healbackend.service;

import org.apache.ibatis.annotations.Param;
import org.example.healbackend.mapper.MessageMapper;
import org.example.healbackend.Utils.TimeUtil;
import org.example.healbackend.Utils.UrlUtil;
import org.example.healbackend.bean.Message;
import org.example.healbackend.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageService {
    // 存储用户最后刷新时间的Map
    private final Map<Integer, String> userLastRefreshTimeMap = new ConcurrentHashMap<>();
    // 存储用户已有聊天伙伴ID列表的Map
    private final Map<Integer, HashSet<Integer>> userExistingPartnersMap = new ConcurrentHashMap<>();
    @Autowired
    private MessageMapper messageMapper;

    public boolean insertNotice(Message notice){
        return messageMapper.insertNotice(notice)>0;
    }

    public int countUnreadNotices(Integer receiveId, Integer userId){
        return messageMapper.countUnreadNotices(receiveId,userId,0);
    }

    public List<Message> selectConversation(Integer userId, Integer receiveId){
        return messageMapper.selectConversation(userId,receiveId);
    }

    public Message selectLatestNotice(Integer userId, Integer receiveId){
        Message notice=messageMapper.selectLatestConversation(userId,receiveId);
        return notice;
    }

    public List<Message> selectRecentConversations(@Param("userId") Integer userId){
        return messageMapper.selectRecentConversations(userId);
    }

    // 根据 receive_id 和 user_id 查询所有消息

    public List<Message> selectNoticesByUserAndReceiver(Integer receiveId, Integer userId){
        return messageMapper.selectNoticesByUserAndReceiver(receiveId ,userId);
    }

    // 根据 id 更新消息状态

    public boolean updateNoticeStatus(Integer id, Integer status){
        return messageMapper.updateNoticeStatus(id,1)>0;
    }

    public boolean updateMessageStatusByUser(Integer userId,Integer receiveId , Integer status){
        return messageMapper.updateNoticeStatusByUser(userId,receiveId,1)>0;
    }

    public List<Message> selectRecentNoticesByUserAndReceiver(Integer receiveId, Integer userId){
        return messageMapper.selectRecentNoticesByUserAndReceiver(receiveId,userId);
    }
    
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    

    public List<Map<String, Object>> findNewChatUsers(Integer userId) {
        return findNewChatUsers(userId, new ArrayList<>());
    }
    
    /**
     * 获取新聊天用户及其最近消息（兼容旧版本）
     * @param userId 当前用户ID
     * @param existingPartnerIds 已存在的聊天伙伴ID列表
     * @return 新聊天用户及其最近消息列表
     */
    public List<Map<String, Object>> findNewChatUsers(Integer userId, List<Integer> existingPartnerIds) {
        // 获取用户的最后刷新时间，如果不存在则使用当前时间并更新
        String lastRefreshTime = getLastRefreshTime(userId);
        if (lastRefreshTime == null) {
            lastRefreshTime = TimeUtil.getCurrentTimeString();
            updateLastRefreshTime(userId, lastRefreshTime);
            // 如果是第一次，直接返回空列表，因为没有新消息
            return new ArrayList<>();
        }
        
        // 获取用户的已有聊天伙伴列表，如果不存在则初始化
        HashSet<Integer> userPartners = getUserExistingPartners(userId);
        
        // 如果前端传递了existingPartnerIds（兼容旧版本），则合并到服务器存储的列表中
        if (existingPartnerIds != null && !existingPartnerIds.isEmpty()) {
            userPartners.addAll(existingPartnerIds);
            updateUserExistingPartners(userId, userPartners);
        }
        
        // 将HashSet转换为List以传递给Mapper
        List<Integer> partnersList = new ArrayList<>(userPartners);
        
        // 查询新增聊天用户
        List<Integer> newPartnerIds = messageMapper.findNewChatPartners(userId, lastRefreshTime, partnersList);
        
        // 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 如果有新的聊天伙伴，获取他们的最后一条消息
        if (!newPartnerIds.isEmpty()) {
            for (Integer partnerId : newPartnerIds) {
                // 获取与该用户的最后一条消息
                Message lastMessage = messageMapper.selectLatestConversation(userId, partnerId);
                if (lastMessage != null) {
                    // 获取聊天伙伴的用户信息
                    User partner = userService.getUserById(partnerId);
                    
                    // 构建返回数据
                    Map<String, Object> chatUser = new LinkedHashMap<>();
                    chatUser.put("userId", partner.getId());
                    chatUser.put("userName", partner.getUsername());
                    chatUser.put("avatar", UrlUtil.getUrl(partner.getAvatarUrl()));
                    chatUser.put("lastMessageContent", lastMessage.getContent());
                    chatUser.put("lastMessageTime", lastMessage.getCreateTime());
                    chatUser.put("lastMessageSender", lastMessage.getUserId());
                    
                    // 计算未读消息数
                    int unreadCount = messageMapper.countUnreadNotices(userId, partnerId, 0);
                    chatUser.put("unreadCount", unreadCount);
                    
                    result.add(chatUser);
                    
                    // 将新的聊天伙伴添加到已有列表中
                    userPartners.add(partnerId);
                }
            }
            
            // 更新用户的已有聊天伙伴列表
            updateUserExistingPartners(userId, userPartners);
        }
        
        // 更新用户的最后刷新时间为当前时间
        updateLastRefreshTime(userId, TimeUtil.getCurrentTimeString());
        
        return result;
    }
    

    // 存储用户最后刷新时间到Redis
    public void updateLastRefreshTime(Integer userId, String refreshTime) {
        redisTemplate.opsForValue().set("user:lastRefreshTime:" + userId, refreshTime);
    }
    // 从Redis获取用户最后刷新时间
    public String getLastRefreshTime(Integer userId) {
        Object value = redisTemplate.opsForValue().get("user:lastRefreshTime:" + userId);
        return value == null ? null : value.toString();
    }
    /**
     * 获取用户的已有聊天伙伴列表
     * @param userId 用户ID
     * @return 已有聊天伙伴ID集合，如果不存在则返回空集合
     */
    public HashSet<Integer> getUserExistingPartners(Integer userId) {
        Object obj = redisTemplate.opsForValue().get("user:partners:" + userId);
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            HashSet<Integer> set = new HashSet<>();
            for (Object o : list) {
                if (o instanceof Integer) {
                    set.add((Integer) o);
                } else if (o instanceof Number) {
                    set.add(((Number) o).intValue());
                } else if (o != null) {
                    try {
                        set.add(Integer.parseInt(o.toString()));
                    } catch (Exception ignore) {}
                }
            }
            return set;
        } else if (obj instanceof HashSet) {
            return (HashSet<Integer>) obj;
        } else if (obj != null) {
            try {
                List<?> list = (List<?>) obj;
                HashSet<Integer> set = new HashSet<>();
                for (Object o : list) {
                    set.add(Integer.parseInt(o.toString()));
                }
                return set;
            } catch (Exception ignore) {}
        }
        return new HashSet<>();
    }
    /**
     * 更新用户的已有聊天伙伴列表
     * @param userId 用户ID
     * @param partners 聊天伙伴ID集合
     */
    private void updateUserExistingPartners(Integer userId, HashSet<Integer> partners) {
        redisTemplate.opsForValue().set("user:partners:" + userId, new ArrayList<>(partners));
    }
}
