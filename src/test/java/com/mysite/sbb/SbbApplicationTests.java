package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class SbbApplicationTests {
	@Autowired	// Spring의 DI기능으로 questionRepository 객체를 스프링이 자동으로 생성해줌
	private QuestionRepository questionRepository;
	@Autowired
	private AnswerRepository answerRepository;

	@Test
	@DisplayName("질문 생성")
	void testJpa() {
		Question q1 = new Question();
		q1.setSubject("What is sbb?");
		q1.setContent("I want to know about sbb.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);

		Question q2 = new Question();
		q2.setSubject("Spring Boot Model Question.");
		q2.setContent("Is id generated automatically?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);
	}

	@Test
	@DisplayName("모든 데이터 조회")
	void testJpa2(){
		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("What is sbb?", q.getSubject());
	}

	@Test
	@DisplayName("id 값으로 데이터 조회")
	void testJpa3(){
		// Optional은 null 처리를 유연하게 처리하기 위해 사용하는 클래스
		Optional<Question> oq = this.questionRepository.findById(1);
		if (oq.isPresent()) {	// isPresent로 null이 아닌지를 확인한 후
			Question q = oq.get();	// get으로 실제 Question 객체 값을 얻어야 한다
			assertEquals("What is sbb?", q.getSubject());
		}
	}

	@Test
	@DisplayName("제목으로 데이터 조회")
	void testJpa4(){	// Jpa에 findBySubject라는 메서드는 없기 때문에 repository에 해당 메서드 추가
		Question q = this.questionRepository.findBySubject("What is sbb?");
		assertEquals(1, q.getId());
	}

	@Test
	@DisplayName("제목과 내용으로 데이터 조회")
	void testJpa5(){
		Question q = this.questionRepository.findBySubjectAndContent("What is sbb?", "I want to know about sbb.");
		assertEquals(1, q.getId());
	}

	@Test
	@DisplayName("제목에 특정 문자열이 포함되어 있는 데이터 조회")
	void testJpa6(){
		List<Question> questionList = this.questionRepository.findBySubjectLike("%sbb%");
		Question q = questionList.get(0);
		assertEquals("What is sbb?", q.getSubject());
	}

	@Test
	@DisplayName("데이터 수정")
	void testJpa7(){
		Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		q.setSubject("수정된 제목");
		this.questionRepository.save(q);
	}

	@Test
	@DisplayName("데이터 삭제")
	void testJpa8(){
		assertEquals(2, this.questionRepository.count());
		Optional<Question> oq = this.questionRepository.findById(1);
		assertTrue(oq.isPresent());
		Question q = oq.get();
		this.questionRepository.delete(q);
		assertEquals(1, this.questionRepository.count());
	}

	@Test
	@DisplayName("답변 데이터 생성 후 저장")
	void testJpa9(){
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("Yes, It's automatically created");
		a.setQuestion(q);
		a.setCreateDate(LocalDateTime.now());
		this.answerRepository.save(a);
	}

	@Test
	@DisplayName("답변 조회")
	void testJpa10(){
		Optional<Answer> oa = this.answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = oa.get();
		assertEquals(2, a.getQuestion().getId());
	}

	@Test
	@Transactional
	@DisplayName("질문, 답변 찾기")
	void testJpa11(){
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("Yes, It's automatically created", answerList.get(0).getContent());
	}

}
