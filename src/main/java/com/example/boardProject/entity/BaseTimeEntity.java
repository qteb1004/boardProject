package com.example.boardProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class}) // Auditing 적용위한 어노테이션
@MappedSuperclass // 공통매핑정보가 필요할 때 사용. 부모 클래스를 상속받는 자식 클래스에 매핑정보만 제공한다.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseTimeEntity {
    @CreatedDate // 엔티티가 생성되어 저장될 때 시간 자동으로 저장
    @Column(updatable = false)
    private LocalDateTime regTime;

    @LastModifiedDate // 엔티티의 값을 변경할 떄 시간 자동저장
    private LocalDateTime updateTime;
}
