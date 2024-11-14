package org.example.boundaryback.friend;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.user.User;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  @Enumerated(EnumType.STRING)
  private RequestStatus status = RequestStatus.PENDING;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sentAt = new Date();  // 친구 요청이 보내진 시간 (생성 시간)

  @Temporal(TemporalType.TIMESTAMP)
  private Date answerAt;  // 상태 변경(수락/거절) 시간

  public enum RequestStatus {
    PENDING, ACCEPTED, REJECTED
  }
}
