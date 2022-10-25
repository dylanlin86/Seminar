package tw.group5.activity.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import tw.group5.activity.model.ActivityActivity;
import tw.group5.activity.model.ActivitySignUp;
import tw.group5.activity.model.ActivityVoucher;
import tw.group5.activity.model.UploadImages;
import tw.group5.activity.service.ActivityActivityService;
import tw.group5.activity.service.ActivitySignUpService;
import tw.group5.activity.service.ActivityVoucherService;
import tw.group5.admin.model.MemberBean;

@Controller
@RequestMapping("/group5")
public class ActivityFunctionController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/*
	 * 其他方法
	 */
	public HashMap<String, Integer> statisticsActivity(List<ActivityActivity> activities) {
		
		for (ActivityActivity activity : activities) {
//			if(activity.getStartTime()>="2022-1-1") {
//				
//			}
		}
		
		return null;
	}
	
	/*
	 * CKEditor上傳圖片
	 */
	@PostMapping("/admin/uploadimages")
	@ResponseBody
	public UploadImages processUploadImages(MultipartFile upload) throws IllegalStateException, IOException {
		
		System.out.println("上傳圖片中");
		
		String fileName = upload.getName()+System.currentTimeMillis()+".jpg";
		String saveFileDir = "C:/images/admin";
		String saveFileDir1 = "/Path/"+fileName;
		File saveFilePath = new File(saveFileDir, fileName);
		upload.transferTo(saveFilePath);
		UploadImages result = new UploadImages();
		result.setUploaded("true");
		result.setUrl(saveFileDir1);
		return result;
	}
	
	/*
	 * statistics
	 */
	@GetMapping("/admin/statisticsmain.controller")
	public String processStatisticsMainAction(Model m) {
		List<ActivityActivity> orderByDate = avtivityService.orderByDate();
		System.out.println("開始日期"+ orderByDate.get(0).getStartTime());
		System.out.println("結束日期"+ orderByDate.get(orderByDate.size()-1).getStartTime());
		
		 m.addAttribute("activity", avtivityService.countMonthActivity());
		return "activity/ActivityStatistics";
	}
	
	
	/*
	 * voucher
	 */
	@Autowired
	public ActivityVoucherService voucherService;

	@GetMapping("/admin/vouchermain.controller")
	public String processvoucherMainAction(Model m) {
		List<ActivityVoucher> voucher = voucherService.findAll();
		m.addAttribute("voucher_queryAll", voucher);
		m.addAttribute("page", "voucher");
		return "activity/ActivityQueryAll";
	}

	@PostMapping("/admin/addvoucher.controller")
	@ResponseBody
	public ActivityVoucher voucherAdd(@RequestBody ActivityVoucher voucher, Model m)
		throws IllegalStateException, IOException {
		voucher.setReviseTime(voucherService.getTime());
		return voucherService.insert(voucher);
	}

	@PostMapping("/admin/deletevoucher.controller")
	public String voucherDelete(Model m, int dataId) {
		voucherService.delete(dataId);
		return "redirect:vouchermain.controller";
	}
	
	/*
	 * Activity--------------------------------------------------------------------------------
	 */
	@Autowired
	private ActivityActivityService avtivityService;
	
	//前往頁面
	@GetMapping("/activityuser")
	public String UserCenter(Model m) {
		List<ActivityActivity> activity = avtivityService.findAll();
		/*
		HashMap<Integer, Integer> countMember = new HashMap<Integer, Integer>();
		for (ActivityActivity activity1 : activity) {
			countMember.put(activity1.getActivityId(), signUpService.countSignUpMember(activity1.getActivityId()));
			activity1.setTotalSignUp(signUpService.countSignUpMember(activity1.getActivityId()));
		}
		m.addAttribute("activity_countMember", countMember);
		*/
		m.addAttribute("showImg", avtivityService.selectImgToShow());
		m.addAttribute("activity_queryAll", activity);
		return "activity/ActivityAllActivityUser"; 
	}
	
	//會員已報名活動查看
		@GetMapping("/user/selfactivtyconfirm/{memberId}")
		public String selfActivtyConfirm(@PathVariable(name = "memberId")int memberId, Model m) {
			List<ActivityActivity> activity = signUpService.usersActivityQuery(memberId);
			
			System.out.println("抓報名活動="+memberId);
			
			m.addAttribute("activity_queryAll", activity);
			return "activity/UserSelfActivityConfirm"; 
		}
	
	//查詢
	@GetMapping("/toactivity/{activityId}")
	public String toactivity(@PathVariable(name = "activityId")int activityId, Model m) {
		ActivityActivity aa = avtivityService.selectById(activityId);
		m.addAttribute("query_activity", aa);
		return "activity/ActivityActivityDetailUser"; 
	}
	
	@GetMapping("/admin/activitymain.controller")
	public String processMainActivityAction(Model m) {
		List<ActivityActivity> activity = avtivityService.findAll();
		m.addAttribute("activity_queryAll", activity);
		m.addAttribute("page", "activity");
		return "activity/ActivityQueryAll";
	}
	
	//新增
	@PostMapping("/admin/addactivity.controller")
	public String voucherAdd(@ModelAttribute(name = "activity") ActivityActivity activity, String add, MultipartFile photo,String activityContent, Model m)
			throws IllegalStateException, IOException {
		if (add.equals("新增")) {
			ActivityActivity aAdd = new ActivityActivity();
			m.addAttribute(aAdd);
			return "activity/ActivityActivityAdd";
		} else {
			
			System.out.println("抓CK="+ activityContent);
			
			if(!photo.isEmpty()) {
				String imgName = avtivityService.processImg(activity.getActivityTitle(), photo);
				activity.setPhotoData(imgName);
			}
			activity.setReviseTime(avtivityService.getTime());
			if (avtivityService.insert(activity)!=null) {
				m.addAttribute("query_activity", avtivityService.selectById(activity.getActivityId()));
				return "activity/ActivityConfirm";
			}
			return "redirect:activitymain.controller";
		}
	}
	
	//更新
	@PostMapping("/admin/updateactivity.controller")
	public String activityUpdate(@ModelAttribute(name = "activity") ActivityActivity activity, String update, int dataId, String oldimg, MultipartFile photo, Model m )
			throws IllegalStateException, IOException {
		if (update.equals("修改")) {
			ActivityActivity aaUpdate = avtivityService.selectById(dataId);
			
			System.out.println("舊物件活動內容:"+aaUpdate.getActivityContent());
			
			m.addAttribute("update_activity", aaUpdate);
			return "activity/ActivityActivityUpdate";
		} else {
			
			System.out.println("更新資料ID : " + activity.getActivityId());
			System.out.println("更新內容 : " + activity.getActivityContent());
			
			activity.setReviseTime(avtivityService.getTime());
			activity.setTotalSignUp(avtivityService.selectById(activity.getActivityId()).getTotalSignUp());
			if (photo.isEmpty()) {
				System.out.println("抓舊圖片=" + oldimg);
				activity.setPhotoData(oldimg);
			} else {
				System.out.println("抓新圖片");
				String imgName = avtivityService.processImg(activity.getActivityTitle(), photo);
				activity.setPhotoData(imgName);
			}
			if (avtivityService.update(activity) != null) {
				m.addAttribute("query_activity", activity);
				m.addAttribute("query", true);
				return "activity/ActivityConfirm";
			}
			return "redirect:activitymain.controller";
		}
	}
	
	//刪除
	@PostMapping("/admin/deleteactivity.controller")
	public String activityDelete(Model m, int dataId) {
		avtivityService.delete(dataId);
		return "redirect:activitymain.controller";
	}
	
	@PostMapping("/admin/queryactivity.controller")
	public String activityQuery(@RequestParam("dataId") int id, Model m) {
		ActivityActivity aa = avtivityService.selectById(id);
		m.addAttribute("query_activity", aa);
		Set<ActivitySignUp> signUp = signUpService.queryByActivity(aa);
		m.addAttribute("signUp_queryAll", signUp);
		m.addAttribute("query", true);
		return "activity/ActivityConfirm";
	}
	
	@PostMapping("/admin/queryactivityajax.controller/{id}")
	@ResponseBody
	public Set<ActivitySignUp> activityQueryAjax(@PathVariable int id, Model m) {
		ActivityActivity aa = avtivityService.selectById(id);
//		m.addAttribute("query_activity", aa);
		Set<ActivitySignUp> signUp = signUpService.queryByActivity(aa);
//		m.addAttribute("signUp_queryAll", signUp);
//		m.addAttribute("query", true);
		
		return signUp;
	}
	
	/*
	 * signUp--------------------------------------------------------------------------------
	 */
	@Autowired
	private ActivitySignUpService signUpService;
	
	@PostMapping("/user/toactivitysignup")
	public String toActivitySignUp(int activityId, HttpSession session ,Model m) {
		ActivityActivity aa = avtivityService.selectById(activityId);
		m.addAttribute("loginM", (MemberBean) session.getAttribute("loginMember"));
		m.addAttribute("signUpActivity", aa);
		return "activity/ActivitySignUp"; 
	}
	
	//新增
	@PostMapping("/user/signupadd.controller")
	public String signUpAdd(Model m, String memberAccount, 
									int memberId,
									String memberName,
									String memberPhone,
									String memberEmail,
									int activityId) {
		ActivitySignUp signUp=new ActivitySignUp();
		ActivityActivity activity = avtivityService.selectById(activityId);
		signUp.setActivity(activity);
		signUp.setMemberId(memberId);
		signUp.setMemberName(memberName);
		signUp.setEmail(memberEmail);
		signUp.setPhone(memberPhone);
		signUp.setSignUpTime(signUpService.getTime());
		
		System.out.println(memberAccount+","+memberId+","+memberName+","+memberPhone+","+memberEmail+",");
		ActivitySignUp signUp1 = signUpService.insert(signUp);
		m.addAttribute("signUp_add", signUp1);
		return "redirect:/group5/toactivity/"+activityId; 
	}
	
	//前往頁面
	@PostMapping("/admin/signupmain.controller")
	public String signUpMain(int activityId, Model m) {
		Set<ActivitySignUp> signUp = signUpService.queryByActivity(avtivityService.selectById(activityId));
		m.addAttribute("signUp_queryAll", signUp);
		return "activity/ActivityAllSignup"; 
	}
	
	//刪除
	@PostMapping("/admin/signupdelete.controller")
	@ResponseStatus(HttpStatus.OK)
	public void signDelete(@RequestBody int id) {
		System.out.println("抓刪除ID="+id);
		signUpService.delete(id);
	}
	
	@GetMapping("/test000")
	public String test000() {
		return "activity/test001";
	}
	
	@PostMapping("/test001")
	@ResponseStatus(HttpStatus.OK)
	public void test001(String testId, String testName) {
		System.out.println("抓測試ID="+testId);
		System.out.println("抓測試NAME="+testName);
	}
	
}
