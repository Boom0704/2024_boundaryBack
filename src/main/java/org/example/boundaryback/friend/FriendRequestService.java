package org.example.boundaryback.friend;

import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FriendRequestService {

  private final FriendRequestRepository friendRequestRepository;
  private  final UserService userService;

  @Autowired
  public FriendRequestService(FriendRequestRepository friendRequestRepository, UserService userService) {
    this.friendRequestRepository = friendRequestRepository;
    this.userService = userService;
  }

  public FriendRequest sendRequest(Optional<User> senderOpt, Optional<User> receiverOpt) {
    if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
      throw new IllegalArgumentException("보낸 사람 또는 받은 사람이 존재하지 않습니다.");
    }

    User sender = senderOpt.get();
    User receiver = receiverOpt.get();

    if (sender.equals(receiver)) {
      throw new IllegalArgumentException("자기 자신에게 친구 요청을 할 수 없습니다.");
    }

    // 이미 보낸 친구 요청이 존재하는지 확인
    Optional<FriendRequest> reverseRequest = friendRequestRepository
        .findBySenderAndReceiver(receiver, sender);
    Optional<FriendRequest> existingRequest = friendRequestRepository
        .findBySenderAndReceiver(sender, receiver);

    // 기존 요청이 있는지 확인하고, 상태가 ACCEPTED나 PENDING이면 예외 처리
    if (existingRequest.isPresent()) {
      FriendRequest existing = existingRequest.get();
      if (existing.getStatus() == FriendRequest.RequestStatus.PENDING ||
          existing.getStatus() == FriendRequest.RequestStatus.ACCEPTED) {
        throw new IllegalArgumentException("이미 친구 요청이 존재합니다.");
      }
    }

    // 상대방이 보낸 요청이 PENDING 상태라면, 그 요청을 ACCEPTED로 업데이트 후 새 요청을 ACCEPTED로 처리
    if (reverseRequest.isPresent()) {
      FriendRequest reverseR = reverseRequest.get();
      if (reverseR.getStatus() == FriendRequest.RequestStatus.PENDING) {
        // 기존 요청을 ACCEPTED 상태로 처리
        this.answerRequest(reverseR.getId(), FriendRequest.RequestStatus.ACCEPTED);

        // 새 요청을 ACCEPTED로 생성
        FriendRequest request = FriendRequest.builder()
            .sender(sender)
            .receiver(receiver)
            .status(FriendRequest.RequestStatus.ACCEPTED)
            .sentAt(new Date())
            .answerAt(new Date()) // 이미 수락된 요청의 응답 시간 설정
            .build();

        return friendRequestRepository.save(request);
      }
    }

    // 새로운 PENDING 요청을 생성
    FriendRequest request = FriendRequest.builder()
        .sender(sender)
        .receiver(receiver)
        .status(FriendRequest.RequestStatus.PENDING)
        .sentAt(new Date()) // sentAt을 현재 시간으로 설정
        .build();

    return friendRequestRepository.save(request);
  }


  // 요청 수락 또는 거절
  @Transactional
  public FriendRequest answerRequest(Long requestId, FriendRequest.RequestStatus status) {
    // 요청을 데이터베이스에서 찾기
    FriendRequest request = friendRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

    // 요청 상태 업데이트
    request.setStatus(status);
    request.setAnswerAt(new Date()); // 현재 시간으로 answerAt 설정

    // 요청이 ACCEPTED인 경우 친구 관계를 추가
    if (status == FriendRequest.RequestStatus.ACCEPTED) {
      User sender = request.getSender();
      User receiver = request.getReceiver();

      // UserService에서 친구 관계 설정을 담당
      userService.addFriend(sender.getUsername(), receiver.getUsername());
    }

    // 친구 요청 상태를 업데이트한 후 반환
    return friendRequestRepository.save(request);
  }


  // 특정 유저가 받은 요청 확인
  public List<FriendRequest> getReceivedRequests(Optional<User> userOpt) {
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("유저가 존재하지 않습니다.");
    }
    return friendRequestRepository.findByReceiver(userOpt.get());
  }

  // 특정 유저가 보낸 요청 확인
  public List<FriendRequest> getSentRequests(Optional<User> userOpt) {
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("유저가 존재하지 않습니다.");
    }
    return friendRequestRepository.findBySender(userOpt.get());
  }

  // 특정 유저가 받은 상태별 요청 확인
  public List<FriendRequest> getReceivedRequestsByStatus(Optional<User> userOpt, FriendRequest.RequestStatus status) {
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("유저가 존재하지 않습니다.");
    }
    return friendRequestRepository.findByReceiverAndStatus(userOpt.get(), status);
  }

  // 특정 유저가 보낸 상태별 요청 확인
  public List<FriendRequest> getSentRequestsByStatus(Optional<User> userOpt, FriendRequest.RequestStatus status) {
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("유저가 존재하지 않습니다.");
    }
    return friendRequestRepository.findBySenderAndStatus(userOpt.get(), status);
  }
}
