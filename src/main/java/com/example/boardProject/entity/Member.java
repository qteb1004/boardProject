package com.example.boardProject.entity;

import com.example.boardProject.constant.Role;
import com.example.boardProject.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true) // 이메일을 통해 회원을 구분해야 하기 때문에 동일한값이 들어올 수 없음
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING) // 자바의 enum 타입을 엔티티 속성으로 지정. enum 사용 시 자동으로 순서가 저장되는데, 순서가 바뀔경우 문제가 발생할 수 있음으로 String으로 저장하기를 권장.
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder){
        // Member 엔티티를 생성하는 메소드. Member 엔티티에 회원을 생성하는 메소드를 만들어서 관리하면 코드가 변경돼도
        // 한 곳만 수정하면 되는 이점이 있다.

        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        // 스프링 시큐리티 설정 클래스에 등록한 BCr어쩌고를 파라미터로 넘겨서 비밀번호를 암호화한다.
        member.setPassword(password);
        member.setRole(Role.USER);
        return member;
    }
}
