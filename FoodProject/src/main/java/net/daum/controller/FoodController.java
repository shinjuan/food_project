package net.daum.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.daum.service.FoodService;
import net.daum.vo.FoodCommentVO;
import net.daum.vo.FoodLikeVO;
import net.daum.vo.FoodNaviVO;
import net.daum.vo.FoodVO;

@Controller
public class FoodController {

	@Autowired
	private FoodService foodService;

	@RequestMapping("/food_main")
	public String Food_main(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session, FoodVO f) {
		String session_id = (String)session.getAttribute("session_id"); 	
		
		//int navi_no = Integer.parseInt(request.getParameter("NAVI_NO"));
		if(session_id != null) {
			model.addAttribute("session_id", session_id);
			int page = 1;
			String food_type = "All";
			List<String> list1 = null;
			List<String> list2 = null;

			int limit = 10; // 한 페이지에 보여지는 목록 개수 
			int startrow = ((page - 1) * limit + 1); // 시작 행 번호
			int endrow = (startrow + limit - 1); // 끝 행 번호

			int totalItems = this.foodService.getTotalItems(startrow, endrow, food_type, list1, list2);
			int totalPages = (int) Math.ceil((double) totalItems / limit);

			int pagecount = totalPages;
			
			
			

			List<Map<String,Object>> all = foodService.getFoodAllList();
			System.out.println("all:"+all);
			model.addAttribute("all", all);
			model.addAttribute("pagecount", pagecount);
			
			return "/food_main"; 

		} else {
			int page = 1;
			String food_type = "All";
			List<String> list1 = null;
			List<String> list2 = null;

			int limit = 10; // 한 페이지에 보여지는 목록 개수 
			int startrow = ((page - 1) * limit + 1); // 시작 행 번호
			int endrow = (startrow + limit - 1); // 끝 행 번호

			int totalItems = this.foodService.getTotalItems(startrow, endrow, food_type, list1, list2);
			int totalPages = (int) Math.ceil((double) totalItems / limit);

			int pagecount = totalPages;
			
			

			List<Map<String,Object>> all = foodService.getFoodAllList();
			System.out.println("all:"+all);
			model.addAttribute("all",all);
			model.addAttribute("pagecount",pagecount);
			
			return "/food_main";
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/food_list2")
	public Map<String, Object> checkbox_Search(@RequestBody HashMap<String,Object> requestData, 
			HttpSession session, Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {

		String food_type = (String) requestData.get("food_type");
	    List<String> list1 =  (List<String>) requestData.get("list1");
	    List<String> list2 = (List<String>) requestData.get("list2");
	    String search_field = (String) requestData.get("search_field");
	    String search_type = (String) requestData.get("search_type");
	    Integer pageStr = (Integer) requestData.get("page");
	    
	    System.out.println("requestData :"+requestData);
	    
	    session.setAttribute("food_type", food_type);
		
	    int page;
		  
		  System.out.println("페이지확인:"+pageStr);
		  if (pageStr == null) {
		    page = 1;
		  } else {
		    page = pageStr;
		    System.out.println("페이지:" + page);
		  }

		  int limit = 10; // 한 페이지에 보여지는 목록 개수 
		  int startrow = ((page - 1) * limit + 1); // 시작 행 번호
		  int endrow = (startrow + limit - 1); // 끝 행 번호

		  int totalItems = this.foodService.getTotalItems(startrow, endrow, food_type, list1, list2);
		  int totalPages = (int) Math.ceil((double) totalItems / limit);
		  
		  System.out.println(totalItems);
		  System.out.println(totalPages);

		  List<Map<String,Object>> flist = this.foodService.getListNew(search_type,search_field, food_type, list1, list2, startrow, endrow);
		  System.out.println("flist: "+ flist);
		  
		  // Map에 flist와 totalPages를 담아서 반환
		  Map<String, Object> resultMap = new HashMap<>();
		  resultMap.put("flist", flist);
		  resultMap.put("totalPages", totalPages);

		  return resultMap;
	}	
	

	//게시글 내용 보기
	@RequestMapping("/food_cont")
	public String Food_cont(Model model, HttpServletRequest request, HttpSession session) {
		String session_id = (String)session.getAttribute("session_id");
		int navi_no = Integer.parseInt(request.getParameter("NAVI_NO"));
		
		
		
		if(session_id != null) { 	

			FoodNaviVO fcont = this.foodService.getFoodCont(navi_no);
			
			model.addAttribute("session_id", session_id);
			model.addAttribute("fcont", fcont);

			System.out.println("상세보기 페이지");

			FoodLikeVO like = new FoodLikeVO();

			like.setLike_navi_no(navi_no);
			like.setSession_id(session_id);
			int re = this.foodService.getLikeCount(like);
			System.out.println(re);
			model.addAttribute("FoodLike", like);
			model.addAttribute("likeCount", re);
			model.addAttribute("session_id", session_id);
			
			//댓글 목록, 페이징
			int page = 1;
			int limit = 5; // 한 페이지에 보여지는 목록 개수 
			int startrow = ((page - 1) * limit + 1); // 시작 행 번호
			int endrow = (startrow + limit - 1); // 끝 행 번호
			int totalComments = this.foodService.getTotalComments(navi_no);
			int totalPages = (int) Math.ceil((double) totalComments / limit);
			List<FoodCommentVO> clist = this.foodService.getCommentList(navi_no, startrow, endrow);
			model.addAttribute("clist", clist);
			model.addAttribute("totalPages", totalPages);

			return "/food_cont";
		} else {
			FoodNaviVO fcont = this.foodService.getFoodCont(navi_no);
			model.addAttribute("fcont", fcont);
			
			//댓글 목록, 페이징
			int page = 1;
			int limit = 5; // 한 페이지에 보여지는 목록 개수 
			int startrow = ((page - 1) * limit + 1); // 시작 행 번호
			int endrow = (startrow + limit - 1); // 끝 행 번호
			int totalComments = this.foodService.getTotalComments(navi_no);
			int totalPages = (int) Math.ceil((double) totalComments / limit);
			List<FoodCommentVO> clist = this.foodService.getCommentList(navi_no, startrow, endrow);
			model.addAttribute("clist", clist);
			model.addAttribute("totalPages", totalPages);
			
			return "/food_cont";
		}
	}
	
	/* 찜하기 AJAX
	 * 
	 */
	@ResponseBody 
	@RequestMapping("/likeUp")
	public void likeup(@RequestBody Map<String, Object> request) {
	  int like_navi_no = (int) request.get("NAVI_NO");
	  String session_id = (String) request.get("session_id");
	  this.foodService.likeUp(session_id, like_navi_no);
	}

	@ResponseBody
	@RequestMapping("/likeDown")
	public void likeDown(@RequestBody Map<String, Object> request) {
	  int like_navi_no = (int) request.get("NAVI_NO");
	  String session_id = (String) request.get("session_id");
	  this.foodService.likeDown(session_id, like_navi_no);

	}
	
	//찜목록
		@RequestMapping("/like_list")
		public String admin_main(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
			request.setCharacterEncoding("UTF-8");	
			response.setContentType("text/html;charset=UTF-8");

			String session_id = (String)session.getAttribute("session_id");

			if(session_id==null) {
				model.addAttribute("errorMessage", "다시 로그인 하세요.");
				return "forward:/member_login";
			} else {
				
				 List<Map<String,Object>> likelist = this.foodService.getLikeList(session_id);
				 
				  
				 model.addAttribute("likelist", likelist);
				
				return "/food_like";
			}
		}
	
	
		/*
		 * 댓글기능
		 */
		//댓글 작성
		@ResponseBody
		@RequestMapping("/CommentWrite")
		public int CommentWrite(@RequestBody Map<String, Object> requestData) {
			int comment_navi_no = (int) requestData.get("NAVI_NO");
			String session_id = (String) requestData.get("session_id");
			String InputComment = (String) requestData.get("InputComment");

			int re = this.foodService.insertComment(session_id, comment_navi_no, InputComment);

			return re;

		}

		//댓글 목록 조회
		@ResponseBody
		@RequestMapping("/CommentList")
		public Map<String, Object> CommentList(Model model, @RequestBody Map<String, Object> requestData) {
			int NAVI_NO = Integer.parseInt((String) requestData.get("NAVI_NO"));
			Integer pageStr = (Integer) requestData.get("currentPage");
			
			int page;

			System.out.println("페이지확인:"+pageStr);
			if (pageStr == null) {
				page = 1;
			} else {
				page = pageStr;
				System.out.println("페이지:" + page);
			}


			int limit = 5; // 한 페이지에 보여지는 목록 개수 
			int startrow = ((page - 1) * limit + 1); // 시작 행 번호
			int endrow = (startrow + limit - 1); // 끝 행 번호

			int totalComments = this.foodService.getTotalComments(NAVI_NO);
			int totalPages = (int) Math.ceil((double) totalComments / limit);

			List<FoodCommentVO> clist = this.foodService.getCommentList(NAVI_NO, startrow, endrow);
			System.out.println("토탈코멘츠:"+totalComments);
			System.out.println("토탈페이지스:"+totalPages);
			System.out.println("씨리스트:"+clist);
			
			
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("clist", clist);
			resultMap.put("totalPages", totalPages);
			System.out.println("리절트맵스:"+resultMap);

			return resultMap;

		}

		//댓글 삭제
		@ResponseBody
		@RequestMapping("/CommentDel")
		public int CommentDel(@RequestBody Map<String, Object> requestData) {
			int comment_no = (Integer) requestData.get("comment_no");
			System.out.println("commentNO:"+comment_no);
			int re = this.foodService.commentDel(comment_no);

			return re;

		}
	
}
