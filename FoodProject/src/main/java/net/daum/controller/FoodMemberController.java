package net.daum.controller;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import net.daum.service.FoodService;
import net.daum.vo.Food_MemberVO;

@Controller
public class FoodMemberController {

	@Autowired
	private FoodService foodService;
	
	 

	/*
	 * 회원가입
	 */
	@RequestMapping("/member_join")
	public String Member_join(HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "member_join";
	}

	//회원가입 ok
	@RequestMapping("/member_join_ok")
	public String Member_join_ok(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");

		String user_id = request.getParameter("user_id");
		String user_pwd = request.getParameter("user_pwd");
		String user_name = request.getParameter("user_name");
		String user_birth = request.getParameter("user_birth");
		String user_email = request.getParameter("user_email");
		String user_phone = request.getParameter("user_phone");
		String user_gender = request.getParameter("user_gender");

		Food_MemberVO mvo = new Food_MemberVO();

		mvo.setUser_id(user_id); mvo.setUser_pwd(user_pwd); mvo.setUser_name(user_name);
		mvo.setUser_birth(user_birth); mvo.setUser_email(user_email); mvo.setUser_phone(user_phone);
		mvo.setUser_gender(user_gender);


		this.foodService.insertJoin(mvo);

		model.addAttribute("mvo", mvo);
		model.addAttribute("errorMessage", "회원정보 가입 성공.");

		return "forward:/member_login";
	}


	/*
	 * 회원 로그인
	 */
	@RequestMapping("/member_login")
	public String Member_login(HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "member_login";
	}

	//로그인 ok
	@RequestMapping("/member_login_ok")
	public String Member_login_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {


		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		String user_id = request.getParameter("user_id");
		String user_pwd = request.getParameter("user_pwd");

		Food_MemberVO mvo = this.foodService.loginCheck(user_id); //로그인 인증

		if(mvo == null || mvo.getUser_state() != 0) {
			model.addAttribute("errorMessage", "가입되지 않은 회원입니다.");
			return "forward:/member_login";
		}else {
			if (!mvo.getUser_pwd().equals(user_pwd)) {
				model.addAttribute("errorMessage", "비밀번호가 다릅니다.");
				return "forward:/member_login";

			}else {
				session.setAttribute("session_id", user_id);//세션 id 속성키 이름에 아이디를 저장

				return "redirect:/food_main";
			}

		}
	}

	//로그아웃
	@RequestMapping("/member_logout")
	public String Member_logout(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {

		response.setContentType("text/html;charset=UTF-8");
		//브라우저에 출력되는 문자와 태그 ,언어코딩 타입을 설정

		session.invalidate();//세션 만료 => 로그아웃
		model.addAttribute("errorMessage", "로그아웃 되었습니다.");
		return "redirect:/food_main";		
	}

	//회원정보 수정
	@RequestMapping("/member_edit")
	public String Member_edit(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");

		String session_id = (String)session.getAttribute("session_id");

		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {

			Food_MemberVO mvo = this.foodService.loginCheck(session_id);

			model.addAttribute("mvo", mvo);
			String user_gender = mvo.getUser_gender();
			model.addAttribute("isMale", user_gender.equals("남자"));
			model.addAttribute("isFemale", user_gender.equals("여자"));

			return "/member_edit";
		}
	}	

	//회원정보 수정
	@RequestMapping("/member_edit_ok")
	public String Member_edit_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");
		//브라우저에 출력되는 문자와 태그 ,언어코딩 타입을 설정

		String session_id = (String)session.getAttribute("session_id");

		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";

		} else {

			String user_name = request.getParameter("user_name");
			String user_birth = request.getParameter("user_birth");
			String user_email = request.getParameter("user_email");
			String user_phone = request.getParameter("user_phone");
			String user_gender = request.getParameter("user_gender");

			Food_MemberVO mvo = new Food_MemberVO();

			mvo.setUser_id(session_id); mvo.setUser_name(user_name); mvo.setUser_gender(user_gender); 
			mvo.setUser_birth(user_birth); mvo.setUser_email(user_email); mvo.setUser_phone(user_phone);

			this.foodService.editInfo(mvo);

			model.addAttribute("errorMessage", "회원정보 수정 성공.");

			return "forward:/food_main";
		}
	}	

	@RequestMapping("/member_pwdEdit")
	public String member_pwdEdit(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");
		//브라우저에 출력되는 문자와 태그 ,언어코딩 타입을 설정

		String session_id = (String)session.getAttribute("session_id");

		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {
			return "/member_pwdEdit";
		}
	}


	//비밀번호 수정
	@RequestMapping("/member_pwdEdit_ok")
	public String Member_pwdEdit_ok(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");
		//브라우저에 출력되는 문자와 태그 ,언어코딩 타입을 설정
		HttpSession session=request.getSession();//세션 객체 생성

		String session_id = (String)session.getAttribute("session_id");

		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {

			String user_pwd = request.getParameter("user_pwd");
			String new_pwd = request.getParameter("new_pwd");

			Food_MemberVO mvo = this.foodService.loginCheck(session_id);
			if(user_pwd.equals(mvo.getUser_pwd())) {
				mvo.setUser_pwd(new_pwd);	
				this.foodService.pwdEdit(mvo);
				model.addAttribute("errorMessage", "비밀번호 변경 성공.");
				return "forward:/food_main";
			} else {
				model.addAttribute("errorMessage", "현재 비밀번호가 다릅니다.");
				return "forward:/member_pwdEdit";
			}								
		}
	}	

	// 회원 탈퇴
	@RequestMapping("/member_withdrawal")
	public String member_del(Model model, HttpSession session, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String session_id = (String)session.getAttribute("session_id"); // 세션아이디를 구함
		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {
			return "/member_withdrawal";		
		}
	}
	
	// 회원 탈퇴 ok
	@RequestMapping("/member_withdrawal_ok")
	public String member_del_ok(Model model, HttpSession session, HttpServletResponse response, HttpServletRequest request) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String session_id = (String)session.getAttribute("session_id"); // 세션아이디를 구함
		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {

			String user_id = request.getParameter("user_id");
			String user_pwd = request.getParameter("user_pwd");

			Food_MemberVO mvo = this.foodService.loginCheck(session_id);
			if(user_pwd.equals(mvo.getUser_pwd()) && user_id.equals(session_id)) {
				this.foodService.updateState(session_id);
				session.invalidate();
				model.addAttribute("errorMessage", "회원탈퇴 성공");
				return "forward:/food_main";
			} else {
				model.addAttribute("errorMessage", "아이디 또는 비밀번호가 다릅니다.");
				return "forward:/member_withdrawal";
			}		
		}
	}

	//관리자 메인 페이지
	@RequestMapping("/admin_main")
	public String admin_main(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");

		String session_id = (String)session.getAttribute("session_id");

		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {
			return "/admin_main";
		}
	}
	
	//관리자 회원관리
	@RequestMapping("/member_manage")
	public String member_manage(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
		request.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=UTF-8");
		
		String session_id = (String)session.getAttribute("session_id");
		
		if(session_id==null) {
			model.addAttribute("errorMessage", "다시 로그인 하세요.");
			return "forward:/member_login";
		} else {
			return "/member_manage";
		}
	}
	
	//관리자 회원관리
		@RequestMapping("/food_manage")
		public String food_manage(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
			request.setCharacterEncoding("UTF-8");	
			response.setContentType("text/html;charset=UTF-8");
			
			String session_id = (String)session.getAttribute("session_id");
			
			String currentTab = (String) session.getAttribute("currentTab");

		    // 탭 상태를 설정하기 위해 모델에 전달
		    model.addAttribute("currentTab", currentTab);
			
			if(session_id==null) {
				model.addAttribute("errorMessage", "다시 로그인 하세요.");
				return "forward:/member_login";
			} else {
				
				List<Map<String,Object>> food = foodService.selectFood();
				List<Map<String,Object>> rest = foodService.selectRest();
				List<Map<String,Object>> region = foodService.selectRegion();
				List<Map<String,Object>> type = foodService.selectType();
				List<Map<String,Object>> navi = foodService.selectNavi();
				List<Map<String,Object>> price_url = foodService.selectPriceUrl();
				List<Map<String,Object>> no_price = foodService.selectNoPrice();
				List<Map<String,Object>> no_url = foodService.selectNoUrl();
				
				
				model.addAttribute("food", food);
				model.addAttribute("rest", rest);
				model.addAttribute("region", region);
				model.addAttribute("type", type);
				model.addAttribute("navi", navi);
				model.addAttribute("price_url", price_url);
				model.addAttribute("no_price", no_price);
				model.addAttribute("no_url", no_url);
				
				return "/food_manage";
			}
		}

		//관리자 회원관리
				@RequestMapping("/food_manage_ok")
				public String food_manage_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
					
					String table =request.getParameter("table");
					String seq =request.getParameter("seq");
					String name =request.getParameter("name");
					
					foodService.insertTable(table,seq,name);
					
					

				    return "redirect:/food_manage";
				}
				
				@RequestMapping("/food_manage_ok_2")
				public String food_manage_ok_rest(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
					
					
					String restaurant_name =request.getParameter("restaurant_name");
					String restaurant_review =request.getParameter("restaurant_review");
					
					foodService.insertRest(restaurant_name,restaurant_review);
					
					

				    return "redirect:/food_manage";
				}
				
				@RequestMapping("/Navi_ok")
				public String navi_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
					
					
					String foodCheckbox =request.getParameter("foodCheckbox");
					String restCheckbox =request.getParameter("restCheckbox");
					String regionCheckbox =request.getParameter("regionCheckbox");
					String typeCheckbox =request.getParameter("typeCheckbox");
					
					foodService.insertNavi(foodCheckbox,restCheckbox,regionCheckbox,typeCheckbox);
					
					

				    return "redirect:/food_manage";
				}
				
				@RequestMapping("/price_ok")
				public String price_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
					
					
					String priceCheckbox =request.getParameter("priceCheckbox");
					String price =request.getParameter("price");
					
					
					foodService.insertPrice(priceCheckbox,price);
					
					

				    return "redirect:/food_manage";
				}
				
				
				@RequestMapping("/delete_ok")
				public String delete_ok(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
					
					
					String table =request.getParameter("table");
					String status =request.getParameter("status");
					String deleteCheckbox =request.getParameter("deleteCheckbox");
					String no =request.getParameter("no");
					
					foodService.deleteTable(table,status,deleteCheckbox,no);
					
					

				    return "redirect:/food_manage";
				}
				
				
				
				@PostMapping("/photo_ok")
				public String photo_ok(Model model, @RequestParam("photo_url") MultipartFile file, HttpServletRequest request, HttpSession session) throws Exception {
				    // Firebase 스토리지 버킷 정보
				    String projectId = "juan-40a06";
				    String bucketName = "juan-40a06.appspot.com";

				    // Firebase 인증 정보 가져오기
				    String filePath = "juan-40a06-firebase-adminsdk-uwf3u-3b1c8052dc.json";
				    InputStream serviceAccount = getClass().getResourceAsStream("/" + filePath);
				    GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

				    // Firebase 스토리지 클라이언트 초기화
				    FirebaseOptions options = FirebaseOptions.builder()
				            .setCredentials(credentials)
				            .setProjectId(projectId)
				            .build();
				   
				    if (FirebaseApp.getApps().isEmpty()) {
				        // FirebaseApp이 초기화되지 않은 경우에만 초기화
				        FirebaseApp.initializeApp(options);
				    }
				    

				    // Firebase 스토리지 클라이언트 가져오기
				    StorageClient storageClient = StorageClient.getInstance();

				    String photoCheckbox = request.getParameter("photoCheckbox");
				    String photo_name = request.getParameter("photo_name");
				    String photo_type = request.getParameter("photo_type");

				    String photo_url = ""; // 기본값으로 빈 문자열 설정

				    if (!file.isEmpty()) {
				        // 파일 업로드 처리
				        Calendar cal = Calendar.getInstance();
				        int year = cal.get(Calendar.YEAR);
				        int month = cal.get(Calendar.MONTH) + 1;
				        int date = cal.get(Calendar.DATE);

				        String homedir = year + "-" + month + "-" + date;
				        String fileName = "photo" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
				        String fileDBName = homedir + "/" + fileName;

				        // 업로드 및 url 생성
				        Bucket bucket = storageClient.bucket(bucketName);
				        Blob blob = bucket.create(fileDBName, file.getInputStream());
				        URL url = blob.signUrl(1, TimeUnit.DAYS);
				        String fileUrl = url.toString();
				        photo_url = fileUrl;

				       

				        foodService.insertPhoto(photoCheckbox, photo_name, photo_url, photo_type);
				    } else {
				        String fileDBName = ""; // 첨부하지 않았을 때 빈 공백 저장
				        photo_url = fileDBName;
				        System.out.println("사진" + photo_url);
				        foodService.insertPhoto(photoCheckbox, photo_name, photo_url, photo_type);
				    }

				    // photo_url을 활용하여 원하는 작업 수행

				    return "redirect:/food_manage";
				}
				







			
				
				  
				
				
				 @PostMapping("/save_tab_to_session")
				  public String saveTabToSession( HttpServletRequest request) {
				    HttpSession session = request.getSession();
				    String tabId = request.getParameter("tabId");
				    session.setAttribute("currentTab", tabId);
				    return "null";
				  }
				
				
	
	/* 관리자 회원정보 조회 AJAX
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/member_search")
	public List<Food_MemberVO> member_search (Model model, Food_MemberVO fvo, String search_field, String search_type) throws Exception{

		search_field = "%" + search_field + "%" ;

		System.out.println("search_type : " + search_type);
		System.out.println("search_field : " + search_field);

		fvo.setSearch_type(search_type);
		fvo.setSearch_field(search_field);

		List<Food_MemberVO> mlist = this.foodService.getMemberList(fvo);			
		System.out.println(mlist.get(0).getUser_name());
		return mlist;
	}
}
