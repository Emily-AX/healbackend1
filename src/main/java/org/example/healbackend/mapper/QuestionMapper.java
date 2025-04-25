package org.example.healbackend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.healbackend.bean.Question;
import org.example.healbackend.bean.Questions;

import java.util.ArrayList;

@Mapper
public interface QuestionMapper {
    @Insert("INSERT INTO questions (user_id, title, content, is_anonymous, view_count, like_count, created_at) VALUES (#{userId}, #{title}, #{content}, #{anonymous}, 0, 0, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int createQuestion(Questions question);

    @Update("UPDATE questions SET title = #{title}, content = #{content}, is_anonymous = #{anonymous} WHERE question_id = #{questionId} AND user_id = #{userId}")
    int updateQuestion(Question question);

    @Delete("DELETE FROM questions WHERE question_id = #{questionId} AND user_id = #{userId}")
    int deleteQuestion(@Param("questionId") int questionId, @Param("userId") int userId);

    @Select("SELECT * FROM questions WHERE question_id = #{questionId}")
    Questions getQuestionById(@Param("questionId") int questionId);
    
    @Select("SELECT * FROM questions WHERE user_id = #{userId}")
    ArrayList<Questions> getQuestionsByUserId(@Param("userId") int userId);
   
    @Select("SELECT * FROM questions")
    ArrayList<Questions> getAllQuestions();

    @Select("SELECT * FROM questions ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    ArrayList<Questions> getQuestionsByPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM questions")
    int getQuestionCount();

    @Delete("DELETE FROM question_likes WHERE question_id = #{questionId}")
    void deleteQuestionLikes(@Param("questionId") int questionId);

    @Delete("DELETE FROM question_comments WHERE question_id = #{questionId}")
    void deleteQuestionComments(@Param("questionId") int questionId);

    void submitQuestion(Question newQuestion);
}