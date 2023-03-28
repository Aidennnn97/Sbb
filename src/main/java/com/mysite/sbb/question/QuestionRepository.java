package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 엔티티에 의해 생성된 데이터베이스 테이블에 접근하는 메서드들을 사용하기 위한 인터페이스
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    // 테이블에 어떤 값을 넣거나 조회하는 등의 CRUD를 어떻게 처리할지 정의<엔티티타입, PK속성타입>
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
}
