package com.example.boardProject.service;

import com.example.boardProject.entity.Member;
import com.example.boardProject.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // final이나 @NonNull이 붙은 필드에 생성자를 생성해준다. 빈에 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등록이 가능하다면 @Autowired 어노테이션 없이 의존성 주입이 가능하다.
public class MemberService implements UserDetailsService {
    // UserDetailsService 인터페이스는 데이터베이스에서 회원 정보를 가져오는 역할을 담당
    // loadUserByUsername() 메소드가 존재하며, 회원정보를 조회하여 사용자의 정보와 권한을 갖는 UserDetailService 를
    // 구현하고 있는 클래스를 통해 로그인 기능을 구현한다.

    @Autowired
    MemberRepository memberRepository;


    // 회원저장
    public Member saveMember(Member member){
        validateDuplicateMember(member); // 중복여부검사
        return memberRepository.save(member);
    }


    // 중복여부검사
    private void validateDuplicateMember(Member member){ // 매개변수 member: 새로 추가하려는 회원
        Member findMember = memberRepository.findByEmail(member.getEmail());
        // 주어진 객체의 이메일을 사용하여 해당 이메일을 가진 회원을 데이터베이스에서 찾는다.
        // 만약 해당 이메일을 가진 회원이 데이터베이스에 존재한다면, findMember 변수에 해당 회원 정보가 할당된다.
        if(findMember != null ){ // findMember 변수가 null 이 아니라면 이미 가입된 회원임을 의미, 즉 중복회원
            throw new IllegalStateException("이미 가입된 회원입니다.");
            // 위 조건이 참일경우, 중복회원임을 나타내는 예외를 던진다.
            // Ille 어쩌고는 런타임 예외로, 프로그램의 흐름을 방해하고 예외적인 상황을 나타낸다.
        }
    }

    // UserDetail : 스프링 시큐리티에서 회원의 정보를 담기 위해서 사용하는 인터페이스
    // 이 인터페이스를 직접 구현하거나 스프링 시큐리티에서 제공하는 User 클래스를 사용한다.
    // User 클래스는 UserDetails 인터페이스를 구현하고 있는 클래스이다.
     @Override // 사용자의 이메일을 기반으로 해당 사용자의 정보를 로드하여 Spring Security 인증 및 권한 부여를 위해 사용하는  UserDetails 객체 생성
    public UserDetails loadUserByUsername(String email) throws // 로그인 할 유저의 email 을 파라미터로 전달받는다.
            UsernameNotFoundException { // 만약 사용자가 존재하지 않을경우 예외 던짐
        Member member = memberRepository.findByEmail(email);
        // 주어진 이메일로 데이터베이스에서 해당 이메일을 가진 회원정보를 조회

        if(member == null){// Member 객체가 null 이라면 해당 이메일을 가진 회원이 데이터베이스에 존재하지 않는것을 의미
            throw new UsernameNotFoundException(email);
            // 위 조건이 참일경우, User어쩌고 예외 발생, 이 예외는 Spring Security 에서 제공하는 예외로,
            // 인증을 위해 사용자 정보를 찾을 수 없을 때 발생시키는 예외이다.
        }

        return User.builder() // UserDetail 을 구현하고 있는 User 객체를 반환해준다. User 객체 생성을 위해 생성자로 회원이메일,비번,role 을 파라미터로 넘겨준다.
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
        // 회원정보가 존재하는 경우, UserDetails 객체를 생성하여 반환한다.
         // 회원의 이메일 비밀번호 역할(role) 을 User.builder() 를 사용하여 설정한다.
         // User 객체는 Spring Security 가 인증 및 권한 부여를 위해 사용하는 객체이다.
    }
}
