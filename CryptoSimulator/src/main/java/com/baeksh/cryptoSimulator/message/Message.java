package com.baeksh.cryptoSimulator.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Message {
  
  
  //거래 관련 메시지
  INITIALIZE_PORTFOLIO_SUCCESS("초기 시드머니가 제공되었습니다."),
  TRADE_SUCCESSFUL("거래가 성공적으로 완료되었습니다."),
  
  //커뮤니티 관련 메시지
  POST_CREATED("게시글이 성공적으로 작성되었습니다."),
  POST_RECOMMENDED("게시글이 성공적으로 추천되었습니다."),
  POST_UPDATED("게시글이 성공적으로 수정되었습니다."),
  POST_DELETED("게시글이 성공적으로 삭제되었습니다."),
  COMMENT_CREATED("댓글이 성공적으로 작성되었습니다."),
  COMMENT_DELETED("댓글이 성공적으로 삭제되었습니다."),
  POST_MARKED_AS_NOTICE("게시글이 공지로 등록되었습니다."),
  USER_BANNED("사용자가 성공적으로 차단되었습니다.");

  private final String message;
}
