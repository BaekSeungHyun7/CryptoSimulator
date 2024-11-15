package com.baeksh.cryptoSimulator.controller;

import com.baeksh.cryptoSimulator.service.CommunityService;
import com.baeksh.cryptoSimulator.service.PortfolioService;
import com.baeksh.cryptoSimulator.dto.CommentRequestDto;
import com.baeksh.cryptoSimulator.dto.CommunityPostDto;
import com.baeksh.cryptoSimulator.dto.CommunityPostResponseDto;
import com.baeksh.cryptoSimulator.dto.IncludeSnapshotOption;
import com.baeksh.cryptoSimulator.dto.PortfolioDto;
import com.baeksh.cryptoSimulator.dto.PostUpdateRequestDto;
import com.baeksh.cryptoSimulator.dto.CommentResponseDto;
import com.baeksh.cryptoSimulator.entity.UserEntity;
import com.baeksh.cryptoSimulator.message.Message;
import com.baeksh.cryptoSimulator.repository.UserRepository;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

  private final CommunityService communityService;
  private final UserRepository userRepository;
  private final PortfolioService portfolioService;
  
  /**
   * 게시물 생성
   * @param postDto 게시물 데이터
   * @param auth 인증 정보
   * @return 생성된 게시물 정보
   */
  @PostMapping("/post")
  public ResponseEntity<CommunityPostResponseDto> createPost(
      @RequestBody CommunityPostDto postDto, Authentication auth) {

    Long userId = Long.valueOf(auth.getPrincipal().toString());

    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    PortfolioDto portfolioSnapshot = null;
    if (IncludeSnapshotOption.isYes(postDto.getIncludeSnapshot())) {
      portfolioSnapshot = portfolioService.getUserPortfolio(userId);
    }

    CommunityPostResponseDto responseDto = communityService.createPost(
        user, postDto.getTitle(), postDto.getContent(), portfolioSnapshot);

    return ResponseEntity.ok(responseDto);
  }

  /**
   * 게시물 조회
   * @param postId 조회할 게시물 ID
   * @return 조회된 게시물 정보
   */
  @GetMapping("/post/{post-id}")
  public ResponseEntity<CommunityPostResponseDto> getPost(@PathVariable("post-id") Long postId) {
    
    CommunityPostResponseDto responseDto = communityService.getPost(postId);
    
    return ResponseEntity.ok(responseDto);
  }

  /**
   * 댓글 작성
   * @param auth 인증 정보
   * @param postId 댓글을 추가할 게시물 ID
   * @param commentRequest 댓글 데이터
   * @return 작성된 댓글 정보
   */
  @PostMapping("/post/{post-id}/comment")
  public ResponseEntity<CommentResponseDto> createComment(
      Authentication auth, @PathVariable("post-id") Long postId, @RequestBody CommentRequestDto commentRequest) {
    
    Long userId = Long.parseLong(auth.getName());
    
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    CommentResponseDto createdComment = communityService.createComment(user, postId, commentRequest.getComment());
    
    return ResponseEntity.ok(createdComment);
  }

  /**
   * 게시글 추천
   * @param auth 인증 정보
   * @param postId 추천할 게시물 ID
   * @return 추천 결과 메시지
   */
  @PostMapping("/post/{post-id}/recommend")
  public ResponseEntity<Message> recommendPost(Authentication auth, @PathVariable("post-id") Long postId) {
    
    Long userId = Long.parseLong(auth.getName());
    
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    communityService.recommendPost(user, postId);
    
    return ResponseEntity.ok(Message.POST_RECOMMENDED);
  }

  /**
   * 게시글 수정
   * @param auth 인증 정보
   * @param postId 수정할 게시물 ID
   * @param updateRequestDto 수정할 게시물 데이터
   * @return 수정된 게시물 정보
   */
  @PutMapping("/post/{post-id}")
  public ResponseEntity<CommunityPostResponseDto> updatePost(
      Authentication auth,
      @PathVariable("post-id") Long postId,
      @RequestBody PostUpdateRequestDto updateRequestDto) {

    Long userId = Long.parseLong(auth.getName());
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    PortfolioDto portfolioSnapshot = null;
    if (IncludeSnapshotOption.isYes(updateRequestDto.getIncludeSnapshot())) {
      portfolioSnapshot = portfolioService.getUserPortfolio(userId);
    }

    CommunityPostResponseDto updatedPost = communityService.updatePost(
        user, postId,
        updateRequestDto.getTitle(),
        updateRequestDto.getContent(),
        portfolioSnapshot);

    return ResponseEntity.ok(updatedPost);
  }

  /**
   * 게시글 삭제
   * @param auth 인증 정보
   * @param postId 삭제할 게시물 ID
   * @return 삭제 결과 메시지
   */
  @DeleteMapping("/post/{post-id}")
  public ResponseEntity<Message> deletePost(Authentication auth, @PathVariable("post-id") Long postId) {
    
    Long userId = Long.parseLong(auth.getName());
    
    UserEntity user = userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    communityService.deletePost(user, postId);
    
    return ResponseEntity.ok(Message.POST_DELETED);
  }

  /**
   * 댓글 삭제
   * @param auth 인증 정보
   * @param commentId 삭제할 댓글 ID
   * @return 삭제 결과 메시지
   */
  @DeleteMapping("/comment/{comment-id}")
  public ResponseEntity<Message> deleteComment(Authentication auth, @PathVariable("comment-id") Long commentId) {
    
    Long userId = Long.parseLong(auth.getName());
    
    UserEntity user = userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    communityService.deleteComment(user, commentId);
    
    return ResponseEntity.ok(Message.COMMENT_DELETED);
  }

  /**
   * 게시글을 공지로 설정
   * @param auth 인증 정보
   * @param postId 공지로 설정할 게시물 ID
   * @return 공지 설정 결과 메시지
   */
  @PostMapping("/post/{post-id}/notice")
  public ResponseEntity<Message> markAsNotice(Authentication auth, @PathVariable("post-id") Long postId) {
    
    Long userId = Long.parseLong(auth.getName());
    
    UserEntity user = userRepository.findById(userId)
      .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    communityService.markAsNotice(user, postId);
    
    return ResponseEntity.ok(Message.POST_MARKED_AS_NOTICE);
  }

  /**
   * 게시글 목록 조회
   * @return 게시글 목록
   */
  @GetMapping("/posts")
  public ResponseEntity<Page<CommunityPostResponseDto>> getPosts(Pageable pageable) {
    
    Page<CommunityPostResponseDto> posts = communityService.getPosts(pageable);
    
    return ResponseEntity.ok(posts);
  }

  /**
   * 회원 차단
   * @param auth 인증 정보
   * @param userId 차단할 회원 ID
   * @param days 차단 기간 (1일, 7일, 영구 차단)
   * @return 차단 결과 메시지
   */
  @PostMapping("/ban/{user-id}")
  public ResponseEntity<Message> banUser(
      Authentication auth, @PathVariable("user-id") Long userId, @RequestParam int days) {
    
    Long adminId = Long.parseLong(auth.getName());
    
    UserEntity admin = userRepository.findById(adminId)
      .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    communityService.banUser(admin, userId, days);
    
    return ResponseEntity.ok(Message.USER_BANNED);
  }
}



