/*

package com.baeksh.cryptoSimulator.service;



import com.baeksh.cryptoSimulator.dto.PortfolioDto;
import com.baeksh.cryptoSimulator.entity.*;
import com.baeksh.cryptoSimulator.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(OrderAnnotation.class)
public class CommunityServiceTest {

  @Mock
  private CommunityPostRepository postRepository;
  
  @Mock
  private CommentRepository commentRepository;
  
  @Mock
  private RecommendationRepository recommendationRepository;
  
  @Mock
  private BanRepository banRepository;
  
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CommunityService communityService;

  private static final Logger logger = LoggerFactory.getLogger(CommunityServiceTest.class);

  private UserEntity testUser;
  private CommunityPost testPost;
  private Comment testComment;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    
    // 테스트용 사용자, 게시글, 댓글
    testUser = new UserEntity();
    testUser.setUserId(1L);
    testUser.setUsername("테스트유저");
    
    testPost = new CommunityPost();
    testPost.setPostId(1L);
    testPost.setUser(testUser);
    testPost.setTitle("테스트 게시글");
    
    testComment = new Comment();
    testComment.setCommentId(1L);
    testComment.setUser(testUser);
    testComment.setPost(testPost);
  }
  
  
  @Test
  @Order(1)
  @DisplayName("1 게시글 작성 테스트")
  public void testCreatePost() {
    try {
      // given
      logger.info("1 게시글 작성 테스트 시작");
      String title = "새로운 게시글";
      String content = "내용내용";
      
      testPost.setTitle(title); // 제목 설정
      testPost.setContent(content); // 내용 설정
      when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);
      
      CommunityPost createdPost = communityService.createPost(testUser, title, content, null);

      logger.info("assertNotNull 검증 시작");
      assertNotNull(createdPost, "생성된 게시글이 null이 아님 정상");
      
      logger.info("assertEquals - User 검증 시작");
      assertEquals(testUser, createdPost.getUser());

      logger.info("assertEquals - Title 검증 시작: createdPost.getTitle() = " + createdPost.getTitle());
      assertEquals(title, createdPost.getTitle());

      logger.info("assertEquals - Content 검증 시작");
      assertEquals(content, createdPost.getContent());
      
      logger.info("1 게시글 작성 테스트 통과");
      verify(postRepository, times(1)).save(any(CommunityPost.class));
      
    } catch (Exception e) {
      logger.error("1 게시글 작성 테스트 실패: 예외 발생 - " + e.getMessage(), e);
      fail("예외 발생: " + e.getMessage());
    }
  }

  @Test
  @Order(2)
  @DisplayName("2. 게시글 조회 테스트")
  public void testGetPost() {
    // given
    Long postId = 1L;
    
    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    CommunityPost post = communityService.getPost(postId);
    
    // then
    assertEquals(testPost, post);
    assertEquals(1, post.getViewCount());
    logger.info("2 게시글 조회 테스트 통과");
  }

 
  @Test
  @Order(3)
  @DisplayName("3. 게시글 수정 테스트")
  public void testUpdatePost() {
    try {
      // given
      logger.info("3 게시글 수정 테스트 시작");
      Long postId = 1L;
      String updatedTitle = "수정된 제목1";
      String updatedContent = "수정된 내용2";
      
      // PortfolioDto 객체 생성 및 설정
      PortfolioDto updatedPortfolio = new PortfolioDto();
      updatedPortfolio.setUsername("bbb");
      updatedPortfolio.setBalance(BigDecimal.valueOf(850722.0));
      updatedPortfolio.setDebt(BigDecimal.ZERO);
      // 필요에 따라 cryptoList 등 다른 필드도 설정

      CommunityService spyCommunityService = spy(communityService);
      doReturn(testPost).when(spyCommunityService).getPost(postId);

      when(postRepository.save(any(CommunityPost.class))).thenReturn(testPost);

      // when
      CommunityPost updatedPost = spyCommunityService.updatePost(
          testUser, postId, updatedTitle, updatedContent, updatedPortfolio);

      // then
      assertNotNull(updatedPost, "업데이트된 게시글이 null이 아님 정상");
      assertEquals(updatedTitle, updatedPost.getTitle());
      assertEquals(updatedContent, updatedPost.getContent());
      logger.info("3 게시글 수정 테스트 통과");
    } catch (Exception e) {
      logger.error("3 게시글 수정 테스트 실패: " + e.getMessage(), e);
      fail("예외 발생: " + e.getMessage());
    }
  }


  
  @Test
  @Order(4)
  @DisplayName("4. 게시글 삭제 테스트")
  public void testDeletePost() {
    // given
    Long postId = 1L;
    
    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    communityService.deletePost(testUser, postId);
    
    // then
    verify(postRepository, times(1)).delete(testPost);
    logger.info("4 게시글 삭제 테스트 통과");
  }

  @Test
  @Order(5)
  @DisplayName("5. 댓글 작성 테스트")
  public void testCreateComment() {
    // given
    Long postId = 1L;
    String commentContent = "댓글테스트1";
    
    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
    Comment createdComment = communityService.createComment(testUser, postId, commentContent);
    
    // then
    assertEquals(testComment, createdComment);
    logger.info("5 댓글 작성 테스트 통과");
  }

  @Test
  @Order(6)
  @DisplayName("6. 댓글 삭제 테스트")
  public void testDeleteComment() {
    // given
    Long commentId = 1L;
    
    // when
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(testComment));
    communityService.deleteComment(testUser, commentId);
    
    // then
    verify(commentRepository, times(1)).delete(testComment);
    logger.info("6 댓글 삭제 테스트 통과");
  }

  @Test
  @Order(7)
  @DisplayName("7. 게시글 추천 테스트")
  public void testRecommendPost() {
    // given
    Long postId = 1L;
    
    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    when(recommendationRepository.findByPostAndUser(testPost, testUser)).thenReturn(Optional.empty());
    communityService.recommendPost(testUser, postId);
    
    // then
    verify(recommendationRepository, times(1)).save(any(Recommendation.class));
    logger.info("7 게시글 추천 테스트 통과");
  }

  @Test
  @Order(8)
  @DisplayName("8. 게시글 공지 설정 테스트")
  public void testMarkAsNotice() {
    // given
    Long postId = 1L;
    testUser.setRole(Role.ADMIN);
    
    // when
    when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));
    communityService.markAsNotice(testUser, postId);
    
    // then
    assertTrue(testPost.isNotice());
    verify(postRepository, times(1)).save(testPost);
    logger.info("8 게시글 공지 설정 테스트 통과");
  }

  @Test
  @Order(9)
  @DisplayName("9. 게시글 목록 조회 테스트")
  public void testGetPosts() {
    // given
    List<CommunityPost> postList = List.of(testPost);
    
    // when
    when(postRepository.findByOrderByIsNoticeDescCreatedAtDesc()).thenReturn(postList);
    List<CommunityPost> result = communityService.getPosts();
    
    // then
    assertEquals(1, result.size());
    assertEquals(testPost, result.get(0));
    logger.info("9 게시글 목록 조회 테스트 통과");
  }

  @Test
  @Order(10)
  @DisplayName("10. 사용자 차단 테스트")
  public void testBanUser() {
    // given
    Long userId = 2L;
    testUser.setRole(Role.ADMIN);
    UserEntity bannedUser = new UserEntity();
    bannedUser.setUserId(userId);
    
    // when
    when(userRepository.findById(userId)).thenReturn(Optional.of(bannedUser));
    communityService.banUser(testUser, userId, 7);
    
    // then
    verify(banRepository, times(1)).save(any(Ban.class));
    logger.info("10 사용자 차단 테스트 통과");
  }
}

*/


