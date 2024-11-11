package org.example.boundaryback.message;

import jakarta.persistence.*;
import lombok.*;
import org.example.boundaryback.user.User;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @ManyToOne
  @JoinColumn(name = "receiver_id", nullable = false)
  private User receiver;

  private String content;

  @Temporal(TemporalType.TIMESTAMP)
  private Date sentAt = new Date();

  private boolean isRead = false;
}
