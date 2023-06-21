package net.daum.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.daum.vo.FoodCommentVO;
import net.daum.vo.FoodLikeVO;
import net.daum.vo.FoodNaviVO;
import net.daum.vo.Food_MemberVO;

public interface FoodService {

	

	List<Map<String, Object>> getFoodAllList();

	List<Map<String, Object>> getListNew(String search_type, String search_field, String food_type, List<String> list1,
			List<String> list2, int startrow, int endrow);

	int getTotalItems(int startrow, int endrow, String food_type,List<String> list1, List<String> list2);

	//회원가입
		void insertJoin(Food_MemberVO mvo);
		
		//session_id 기준 회원정보 조회
		Food_MemberVO loginCheck(String user_id);
		
		//회원정보 수정
		void editInfo(Food_MemberVO mvo);
		
		//회원 비밀번호 수정
		void pwdEdit(Food_MemberVO mvo);
		
		//회원 탈퇴
		void updateState(String session_id);

		//검색에 따라 회원목록 조회
		List<Food_MemberVO> getMemberList(Food_MemberVO mvo);
		
		
		/*
		 * 상세내용 보기
		 */
		FoodNaviVO getFoodCont(int navi_no);
		
		

		/*
		 * 댓글기능
		 */
		//댓글 저장
		int insertComment(String session_id, int comment_navi_no, String inputComment);

		//댓글 목록 조회
		List<FoodCommentVO> getCommentList(int NAVI_NO, int startrow, int endrow);

		//댓글 삭제
		int commentDel(int comment_no);
		
		//댓글 개수
		int getTotalComments(int NAVI_NO);
		
		
		/*
		 * 찜하기
		 */
		
		//찜하기 눌렀는지 안눌렀는지 확인하는 메서드 
		int getLikeCount(FoodLikeVO like);

		//찜하기 UP(insert)
		void likeUp(String session_id, int like_navi_no);
		
		//찜하기 DOWN(delete)
		void likeDown(String session_id, int like_navi_no);
		

		void insertTable(String table, String seq, String name);

		void insertRest(String restaurant_name, String restaurant_review);

		List<Map<String, Object>> selectFood();

		List<Map<String, Object>> selectRest();
		
		List<Map<String, Object>> selectRegion();
		
		List<Map<String, Object>> selectType();

		void insertNavi(String foodCheckbox, String restCheckbox, String regionCheckbox, String typeCheckbox);

		List<Map<String, Object>> selectNavi();

		List<Map<String, Object>> selectPriceUrl();
		
		List<Map<String, Object>> selectNoPrice();
		
		List<Map<String, Object>> selectNoUrl();

		void insertPrice(String priceCheckbox, String price);

		void insertPhoto(String photoCheckbox, String photo_name, String photo_url, String photo_type);

		void deleteTable(String table, String status, String deleteCheckbox, String no);

		List<Map<String, Object>> getLikeList(String session_id);

		

}
