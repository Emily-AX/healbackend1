package org.example.healbackend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.healbackend.bean.Message;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 统计用户的未读消息数量
    @Select("""
            SELECT COUNT(*)
            FROM message
            WHERE receive_id = #{receiveId}
            AND user_id = #{userId}
            AND status = #{status}
            """)
    int countUnreadNotices(
            @Param("receiveId") Integer receiveId,
            @Param("userId") Integer userId,
            @Param("status") Integer status
    );

    @Select("""
            SELECT *
            FROM (
                SELECT 
                    id,
                    user_id,
                    receive_id,
                    create_time,
                    content,
                    status,
                    CASE
                        WHEN user_id < receive_id THEN CONCAT(user_id, '_', receive_id)
                        ELSE CONCAT(receive_id, '_', user_id)
                    END AS conversation_key,
                    ROW_NUMBER() OVER (
                        PARTITION BY
                            CASE
                                WHEN user_id < receive_id THEN CONCAT(user_id, '_', receive_id)
                                ELSE CONCAT(receive_id, '_', user_id)
                            END
                        ORDER BY STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') DESC
                    ) AS rn
                FROM message
                WHERE #{userId} IN (user_id, receive_id)
            ) AS ranked_messages
            WHERE rn = 1
            ORDER BY STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') DESC
            """)
    List<Message> selectRecentConversations(@Param("userId") Integer userId);

    // 根据 receive_id 和 user_id 查询所有消息
    @Select("""
            SELECT * FROM message
            WHERE receive_id = #{receiveId}
            AND user_id = #{userId}
            ORDER BY create_time ASC
            """)
    List<Message> selectNoticesByUserAndReceiver(
            @Param("receiveId") Integer receiveId,
            @Param("userId") Integer userId
    );

    @Select("""
            SELECT * FROM message
            WHERE receive_id = #{receiveId}
            AND user_id = #{userId}
            AND status = 0
            ORDER BY create_time ASC
            """)
    List<Message> selectRecentNoticesByUserAndReceiver(
            Integer receiveId,
            Integer userId
    );

    @Select("""
            SELECT * FROM message
            WHERE user_id = #{userId}
            AND receive_id = #{receiveId}
            ORDER BY create_time DESC
            LIMIT 1
            """)
    Message selectLatestNotice(
            @Param("userId") Integer userId,
            @Param("receiveId") Integer receiveId
    );

    @Select("""
            SELECT * FROM message
            WHERE (user_id = #{userId} AND receive_id = #{receiveId})
            OR (user_id = #{receiveId} AND receive_id = #{userId})
            ORDER BY STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') DESC
            LIMIT 1
            """)
    Message selectLatestConversation(
            @Param("userId") Integer userId,
            @Param("receiveId") Integer receiveId
    );

    @Select("""
            SELECT * FROM message
            WHERE (user_id = #{userId} AND receive_id = #{receiveId})
            OR (user_id = #{receiveId} AND receive_id = #{userId})
            ORDER BY STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') ASC
            """)
    List<Message> selectConversation(
            @Param("userId") Integer userId,
            @Param("receiveId") Integer receiveId
    );

    // 根据 id 更新消息状态
    @Update("""
            UPDATE message
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateNoticeStatus(
            @Param("id") Integer id,
            @Param("status") Integer status
    );

    @Update("""
            UPDATE message
            SET status = #{status}
            WHERE (user_id = #{receiveId} and receive_id=#{userId})
            """)
    int updateNoticeStatusByUser(
            Integer userId, Integer receiveId, Integer status
    );

    @Insert({
            "INSERT INTO message (user_id, receive_id, create_time, content, status)",
            "VALUES (#{userId}, #{receiveId}, #{createTime}, #{content}, #{status})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertNotice(Message message);

    /**
     * 查询新增聊天用户
     * @param userId 当前用户ID
     * @param lastRefreshTime 上次刷新时间
     * @param existingPartnerIds 已存在的聊天伙伴ID列表
     * @return 新聊天用户ID列表
     */
    @Select("""
            <script>
            SELECT DISTINCT 
                CASE 
                    WHEN user_id = #{userId} THEN receive_id 
                    ELSE user_id 
                END AS partner_id
            FROM message
            WHERE (user_id = #{userId} OR receive_id = #{userId})
                AND STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') > STR_TO_DATE(#{lastRefreshTime}, '%Y-%m-%d %H:%i:%s')
                <if test='existingPartnerIds != null and existingPartnerIds.size() > 0'>
                    AND CASE 
                        WHEN user_id = #{userId} THEN receive_id 
                        ELSE user_id 
                    END NOT IN
                    <foreach item="item" collection="existingPartnerIds" open="(" separator="," close=")">
                        #{item}
                    </foreach>
                </if>
            </script>
            """)
    List<Integer> findNewChatPartners(
            @Param("userId") Integer userId,
            @Param("lastRefreshTime") String lastRefreshTime,
            @Param("existingPartnerIds") List<Integer> existingPartnerIds
    );

    /**
     * 查询与指定用户的最新消息
     * @param userId 当前用户ID
     * @param partnerIds 聊天伙伴ID列表
     * @return 最新消息列表
     */
    @Select("""
            <script>
            SELECT * FROM (
                SELECT n.*
                FROM message n
                INNER JOIN (
                    SELECT 
                        CASE 
                            WHEN user_id = #{userId} THEN receive_id 
                            ELSE user_id 
                        END AS partner_id, 
                        MAX(STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s')) AS latest_time
                    FROM message
                    WHERE (user_id = #{userId} OR receive_id = #{userId})
                        AND CASE 
                            WHEN user_id = #{userId} THEN receive_id 
                            ELSE user_id 
                        END IN
                        <foreach item="item" collection="partnerIds" open="(" separator="," close=")">
                            #{item}
                        </foreach>
                    GROUP BY partner_id
                ) AS latest
                ON ( (n.user_id = #{userId} AND n.receive_id = latest.partner_id)
                    OR (n.receive_id = #{userId} AND n.user_id = latest.partner_id) )
                AND STR_TO_DATE(n.create_time, '%Y-%m-%d %H:%i:%s') = latest.latest_time
            ) AS latest_messages
            ORDER BY STR_TO_DATE(create_time, '%Y-%m-%d %H:%i:%s') DESC
            </script>
            """)
    List<Message> findLastMessagesByPartners(
            @Param("userId") Integer userId,
            @Param("partnerIds") List<Integer> partnerIds
    );
}
