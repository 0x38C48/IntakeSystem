package com.student_online.IntakeSystem.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.student_online.IntakeSystem.mapper.AnswerMapper;
import com.student_online.IntakeSystem.mapper.OptionMapper;
import com.student_online.IntakeSystem.mapper.QuestionMapper;
import com.student_online.IntakeSystem.model.dto.QuestionnaireInfoDto;
import com.student_online.IntakeSystem.model.po.Question;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.student_online.IntakeSystem.model.po.Answer;

@Service
public class QuestionnaireExporterService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private OptionMapper optionMapper;

    public void export(HttpServletResponse response, List<QuestionnaireInfoDto> dataList) throws IOException {
        // 1. 收集所有出现的问题ID
        Set<Integer> allQuestionIds = dataList.stream()
                .flatMap(QuestionnaireInfoDto -> QuestionnaireInfoDto.getAnswers() == null ? Stream.empty() :
                        QuestionnaireInfoDto.getAnswers().stream().map(Answer::getQuestionId))
                .collect(Collectors.toSet());

        List<Question> questions=new ArrayList<>();
        for(int id:allQuestionIds){
            questions.add(questionMapper.getQuestionById(id));
        }
        // 2. 批量查询所有问题内容
        Map<Integer, String> questionContents = questions.stream()
                .collect(Collectors.toMap(Question::getId, Question::getContent));

        List<Integer> orderedQuestionIds = questions.stream()
                .sorted(Comparator.comparingInt(Question::getSort)) // 按order字段排序
                .map(Question::getId)
                .collect(Collectors.toList());

        List<List<String>> headers = new ArrayList<>();
        headers.add(List.of("用户名"));
        headers.add(List.of("姓名"));

// 添加动态问题列
        orderedQuestionIds.forEach(id ->
                headers.add(Collections.singletonList(questionContents.get(id))));

        headers.add(List.of("更新时间"));

        // 5. 准备数据行
        List<Map<String, String>> rows = dataList.stream().map(QuestionnaireInfoDto -> {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("用户名", QuestionnaireInfoDto.getUsername());
            row.put("姓名", QuestionnaireInfoDto.getName());
            // 先将用户的回答按问题ID分组
            Map<Integer, String> userAnswers = QuestionnaireInfoDto.getAnswers() == null ? new HashMap<>() :
                    QuestionnaireInfoDto.getAnswers().stream()
                            .collect(Collectors.toMap(
                                    Answer::getQuestionId,
                                    answer -> {
                                        if (answer.getAnswerContent() != null && !answer.getAnswerContent().isEmpty()) {
                                            return answer.getAnswerContent(); // 填空题答案
                                        } else if (answer.getOptionId() != null) {
                                            // 选择题：显示选项内容而不是ID
                                            return optionMapper.getContentById(answer.getOptionId());
                                        } else {
                                            return ""; // 两者都为空
                                        }
                                    }
                            ));

            // 按问题顺序填充回答
            orderedQuestionIds.forEach(questionId -> {
                row.put(questionContents.get(questionId), userAnswers.getOrDefault(questionId, ""));
            });

            row.put("更新时间", QuestionnaireInfoDto.getUpdateTime().toString());
            return row;
        }).collect(Collectors.toList());

        // 6. 导出Excel
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("问卷数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream())
                .head(headers)
                .sheet("问卷数据")
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动列宽
                .doWrite(rows);
    }
}