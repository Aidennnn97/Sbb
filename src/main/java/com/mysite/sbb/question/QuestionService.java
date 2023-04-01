package com.mysite.sbb.question;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public Page<Question> getList(int page, String kw, String categoryName) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().length() == 0){
            return this.questionRepository.findAllByCategory_Name(pageable, categoryName);
        }

        Specification<Question> spec = search(kw.trim(), categoryName);

        return this.questionRepository.findAll(spec, pageable);
    }

    private Specification<Question> search(String kw, String categoryName) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                Join<Question, Category> c = q.join("category", JoinType.LEFT);
                return cb.and(
                        cb.or(
                                cb.like(q.get("subject"), "%" + kw + "%"),  // 제목
                                cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                                cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                                cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                                cb.like(u2.get("username"), "%" + kw + "%")),   // 답변 작성자
                        cb.like(
                                c.get("name"), "%" + categoryName + "%") // category.name = name
                );
            }
        };
    }

    public List<Question> getUserQuestion(SiteUser user){
        return this.questionRepository.findAllByAuthorOrderByModifyDateDesc(user);
    }

    public Question getQuestion(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser siteUser, Category category){
        Question q = new Question();
        Integer cnt = 0;
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setModifyDate(LocalDateTime.now());
        q.setView(cnt);
        q.setAuthor(siteUser);
        q.setCategory(category);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question){
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    @Transactional
    public void updateView(Integer id){
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            Question q = question.get();
            q.setView(q.getView() + 1);
        } else {
            throw new DataNotFoundException("question not found");
        }
    }
}
