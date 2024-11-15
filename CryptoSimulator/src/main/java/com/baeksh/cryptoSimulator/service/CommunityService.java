package com.baeksh.cryptoSimulator.service;

import com.baeksh.cryptoSimulator.dto.PortfolioDto;
import com.baeksh.cryptoSimulator.dto.CommunityPostResponseDto;
import com.baeksh.cryptoSimulator.dto.CommentResponseDto;
import com.baeksh.cryptoSimulator.entity.*;
import com.baeksh.cryptoSimulator.exception.CustomException;
import com.baeksh.cryptoSimulator.exception.ErrorCode;
import com.baeksh.cryptoSimulator.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final CommunityPostRepository postRepository;
  private final CommentRepository commentRepository;
  private final RecommendationRepository recommendationRepository;
  private final BanRepository banRepository;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;

  /**
   * 게시글 작성
   * @param user 작성자
   * @param title 게시글 제목
   * @param content 게시글 내용
   * @param portfolioSnapshot 포트폴리오 스냅샷
   * @return 생성된 게시글
   */
  public CommunityPostResponseDto createPost(UserEntity user, String title, String content, PortfolioDto portfolioSnapshot) {
    String portfolioSnapshotJson = null;
    if (portfolioSnapshot != null) {
      try {
        portfolioSnapshotJson = objectMapper.writeValueAsString(portfolioSnapshot);
      } catch (Exception e) {
        throw new CustomException(ErrorCode.SERIALIZATION_ERROR);
      }
    }

    CommunityPost post = CommunityPost.builder()
        .user(user)
        .title(title)
        .content(content)
        .portfolioSnapshot(portfolioSnapshotJson)
        .createdAt(LocalDateTime.now())
        .build();
    postRepository.save(post);

    return toCommunityPostResponseDto(post);
  }

  /**
   * 게시글 조회 (조회수 증가)
   * @param postId 게시글 ID
   * @return 조회된 게시글
   */
  @Transactional
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  public CommunityPostResponseDto getPost(Long postId) {
    CommunityPost post = postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    post.incrementViewCount();
    return toCommunityPostResponseDto(post);
  }

  /**
   * 게시글 수정
   * @param user 요청자
   * @param postId 수정할 게시글 ID
   * @param title 수정할 제목
   * @param content 수정할 내용
   * @param portfolioSnapshot 수정할 포트폴리오 스냅샷
   * @return 수정된 게시글
   */
  @Transactional
  public CommunityPostResponseDto updatePost(
      UserEntity user, Long postId, String title, String content, PortfolioDto portfolioSnapshot) {
    
    CommunityPost post = getPostEntity(postId);
    
    if (!post.getUser().equals(user)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    post.setTitle(title);
    post.setContent(content);
    
    // 조건적으로 포트폴리오 스냅샷을 JSON 문자열로 저장
    if (portfolioSnapshot != null) {
      try {
        String portfolioSnapshotJson = objectMapper.writeValueAsString(portfolioSnapshot);
        post.setPortfolioSnapshot(portfolioSnapshotJson);
      } catch (Exception e) {
        throw new CustomException(ErrorCode.SERIALIZATION_ERROR);
      }
    } else {
      post.setPortfolioSnapshot(null);  // 스냅샷이 없으면 null
    }

    post.setUpdatedAt(LocalDateTime.now());
    postRepository.save(post);
    
    return toCommunityPostResponseDto(post);
  }

  /**
   * 게시글 삭제
   * @param user 요청자
   * @param postId 삭제할 게시글 ID
   */
  public void deletePost(UserEntity user, Long postId) {
    CommunityPost post = getPostEntity(postId);
    if (!post.getUser().equals(user) && !user.isModeratorOrAdmin()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
    postRepository.delete(post);
  }

  /**
   * 댓글 작성
   * @param user 작성자
   * @param postId 댓글을 추가할 게시글 ID
   * @param commentContent 댓글 내용
   * @return 작성된 댓글
   */
  public CommentResponseDto createComment(UserEntity user, Long postId, String commentContent) {
    CommunityPost post = getPostEntity(postId);
    Comment comment = Comment.builder()
        .user(user)
        .post(post)
        .comment(commentContent)
        .build();
    commentRepository.save(comment);
    return toCommentResponseDto(comment);
  }

  /**
   * 댓글 삭제
   * @param user 요청자
   * @param commentId 삭제할 댓글 ID
   */
  public void deleteComment(UserEntity user, Long commentId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    if (!comment.getUser().equals(user) && !user.isModeratorOrAdmin()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
    commentRepository.delete(comment);
  }

  /**
   * 게시글 추천
   * @param user 추천자
   * @param postId 추천할 게시글 ID
   */
  public void recommendPost(UserEntity user, Long postId) {
    CommunityPost post = getPostEntity(postId);
    Optional<Recommendation> existingRecommendation = recommendationRepository.findByPostAndUser(post, user);
    if (existingRecommendation.isPresent()) {
      throw new CustomException(ErrorCode.ALREADY_RECOMMENDED);
    }
    Recommendation recommendation = Recommendation.builder()
        .post(post)
        .user(user)
        .build();
    recommendationRepository.save(recommendation);
  }

  /**
   * 게시글을 공지로 설정
   * @param user 관리자
   * @param postId 공지로 설정할 게시글 ID
   */
  public void markAsNotice(UserEntity user, Long postId) {
    if (!user.isAdmin()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
    CommunityPost post = getPostEntity(postId);
    post.setNotice(true);
    postRepository.save(post);
  }

  /**
   * 게시글 목록 조회 (공지글 우선, 최신순 정렬)
   * @param pageable 페이징 정보
   * @return 정렬된 게시글 목록 (페이징 처리)
   */
  @Transactional(readOnly = true)
  public Page<CommunityPostResponseDto> getPosts(Pageable pageable) {
    Page<CommunityPost> posts = postRepository.findAllByOrderByIsNoticeDescCreatedAtDesc(pageable);
    return posts.map(this::toCommunityPostResponseDto);
  }
  
  /**
   * 유저 차단
   * @param admin 관리자
   * @param userId 차단할 유저 ID
   * @param days 차단 일수 (1일, 7일, 영구 차단)
   */
  @Transactional
  public void banUser(UserEntity admin, Long userId, int days) {
    if (!admin.isAdmin()) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    
    LocalDate banEndDate = (days == -1) ? null : LocalDate.now().plusDays(days);

    Ban ban = Ban.createBan(user, banEndDate); // 정적 메서드 사용
    banRepository.save(ban);
  }

  // CommunityPost 엔티티를 CommunityPostResponseDto로 변환하는 메서드
  private CommunityPostResponseDto toCommunityPostResponseDto(CommunityPost post) {
    return new CommunityPostResponseDto(
      post.getPostId(),
      post.getTitle(),
      post.getContent(),
      post.getUser().getUsername(),
      post.getCreatedAt(),
      post.getUpdatedAt(),
      post.isNotice(),
      post.getRecommendationCount(),
      post.getViewCount(),
      post.getPortfolioSnapshot()
    );
  }

  // Comment 엔티티를 CommentResponseDto로 변환하는 메서드
  private CommentResponseDto toCommentResponseDto(Comment comment) {
    return new CommentResponseDto(
      comment.getCommentId(),
      comment.getComment(),
      comment.getUser().getUsername(),
      comment.getCommentedAt()
    );
  }

  // CommunityPost 엔티티 조회용 헬퍼 메서드
  private CommunityPost getPostEntity(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
  }
}



