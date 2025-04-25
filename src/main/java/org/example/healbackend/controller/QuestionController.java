package org.example.healbackend.controller;

import org.example.healbackend.bean.Question;
import org.example.healbackend.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/questions")  // 设置统一的路径前缀
public class QuestionController {

    @Autowired
    private QuestionMapper questionMapper;

    private List<Question> questionList = new ArrayList<>();

    // 获取所有问题
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionMapper.getAllQuestions();
    }

    // 根据ID查询问题
    @GetMapping("/{question_id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int question_id) {
        Question question = questionMapper.getQuestionById(question_id);
        if (question != null) {
            return ResponseEntity.ok(question);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 提交问题
    @PostMapping
    public ResponseEntity<String> submitQuestion(@RequestBody Question newQuestion) {
        try {
            // 将新问题插入数据库
            questionMapper.submitQuestion(newQuestion);
            return ResponseEntity.ok("问题提交成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("提交问题失败");
        }
    }

    // 更新问题
    @PutMapping("/{question_id}")
    public ResponseEntity<Void> updateQuestion(@PathVariable int question_id, @RequestBody Question question) {
        question.setQuestion_id(question_id);
        questionMapper.updateQuestion(question);
        return ResponseEntity.noContent().build();  // 更新成功返回204状态码
    }

}
