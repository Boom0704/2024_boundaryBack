package org.example.boundaryback.friend;

import org.example.boundaryback.user.User;
import org.example.boundaryback.user.UserService; // 유저 서비스가 필요
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

  private final FriendRequestService friendRequestService;
  private final UserService userService; // 유저 정보를 얻기 위한 서비스

  @Autowired
  public FriendRequestController(FriendRequestService friendRequestService, UserService userService) {
    this.friendRequestService = friendRequestService;
    this.userService = userService;
  }

  // 친구 요청 생성
  @PostMapping("/send")
  public ResponseEntity<FriendRequest> sendRequest(
      @RequestParam("senderUsername") String senderUsername,
      @RequestParam("receiverUsername") String receiverUsername) {

    Optional<User> sender = userService.getUserByUsername(senderUsername);
    Optional<User> receiver = userService.getUserByUsername(receiverUsername);

    FriendRequest request = friendRequestService.sendRequest(sender, receiver);
    return ResponseEntity.ok(request);
  }

  // 친구 요청 수락 또는 거절
  @PostMapping("/{requestId}/answer")
  public ResponseEntity<FriendRequest> answerRequest(
      @PathVariable Long requestId,
      @RequestParam("status") FriendRequest.RequestStatus status) {

    FriendRequest request = friendRequestService.answerRequest(requestId, status);
    return ResponseEntity.ok(request);
  }

  // 받은 친구 요청 확인
  @GetMapping("/received")
  public ResponseEntity<List<FriendRequest>> getReceivedRequests(@RequestParam("username") String username) {
    Optional<User> user = userService.getUserByUsername(username);
    List<FriendRequest> requests = friendRequestService.getReceivedRequests(user);
    return ResponseEntity.ok(requests);
  }

  // 보낸 친구 요청 확인
  @GetMapping("/sent")
  public ResponseEntity<List<FriendRequest>> getSentRequests(@RequestParam("username") String username) {
    Optional<User> user = userService.getUserByUsername(username);
    List<FriendRequest> requests = friendRequestService.getSentRequests(user);
    return ResponseEntity.ok(requests);
  }

  // 상태별 받은 친구 요청 확인
  @GetMapping("/received/status")
  public ResponseEntity<List<FriendRequest>> getReceivedRequestsByStatus(
      @RequestParam("username") String username,
      @RequestParam("status") FriendRequest.RequestStatus status) {

    Optional<User> user = userService.getUserByUsername(username);
    List<FriendRequest> requests = friendRequestService.getReceivedRequestsByStatus(user, status);
    return ResponseEntity.ok(requests);
  }

  // 상태별 보낸 친구 요청 확인
  @GetMapping("/sent/status")
  public ResponseEntity<List<FriendRequest>> getSentRequestsByStatus(
      @RequestParam("username") String username,
      @RequestParam("status") FriendRequest.RequestStatus status) {

    Optional<User> user = userService.getUserByUsername(username);
    List<FriendRequest> requests = friendRequestService.getSentRequestsByStatus(user, status);
    return ResponseEntity.ok(requests);
  }
}
