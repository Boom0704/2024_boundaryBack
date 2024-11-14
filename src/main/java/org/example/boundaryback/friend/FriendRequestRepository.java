package org.example.boundaryback.friend;

import org.example.boundaryback.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

  // 유저가 보낸 요청을 찾는 쿼리
  List<FriendRequest> findBySender(User sender);

  // 유저가 받은 요청을 찾는 쿼리
  List<FriendRequest> findByReceiver(User receiver);

  // 유저가 보낸 요청 상태별로 찾는 쿼리
  List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.RequestStatus status);

  // 유저가 받은 요청 상태별로 찾는 쿼리
  List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.RequestStatus status);

  Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
}
