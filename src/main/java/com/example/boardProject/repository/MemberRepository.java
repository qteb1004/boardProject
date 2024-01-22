package com.example.boardProject.repository;

import com.example.boardProject.dto.MemberFormDto;
import com.example.boardProject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);
    // 회원가입 시 중복된 회원이 있는지 검사하기 위해서 이메일로 회원을 검사할 수 있도록 쿼리 메소드 작성하기.
}
