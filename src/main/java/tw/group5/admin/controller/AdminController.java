package tw.group5.admin.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import tw.group5.admin.model.AdminBean;
import tw.group5.admin.model.MemberBean;
import tw.group5.admin.model.MemberDetail;
import tw.group5.admin.service.AdminService;

//@MultipartConfig Spring Boot不需要加
@SessionAttributes(names = { "loginMember" })
@Controller
@RequestMapping("/group5")
public class AdminController {
	@Autowired
	private AdminService adminService;

	@RequestMapping(path = "/admin/fail", method = RequestMethod.POST)
	public String adminLoginAction(Model m) {
		Map<String, String> errorMsgMap = new HashMap<String, String>();
		m.addAttribute("errorMsgMap", errorMsgMap);
		errorMsgMap.put("LoginError", "帳號或密碼錯誤，請重新輸入");
		return "admin/AdminLogin";
	}

	@RequestMapping("/admin/AdminNew")
	public String adminNew() {
		return "admin/AdminNew"; // 導向AdminNew.jsp頁面
	}

	@RequestMapping("/admin/AdminBackstage")
	public String adminBackstage(Model m) throws ParseException {
		Date date = new Date();
		long nowDate =date.getTime();
		System.out.println(nowDate);
		List<MemberBean> result = adminService.selectAllMember();
		int[] arrMonth = new int[]{0,0,0,0,0,0,0,0,0,0};
		int men = 0;
		int women = 0;
		int secret = 0;
		int activeMember = 0;
		for(int i=0; i<result.size(); i++) {
			System.out.println("這是第幾"+ i);
			String createDate = result.get(i).getMemberDetail().getCreateDate();
			String loginDateString = result.get(i).getMemberDetail().getRecentLoginDate();
			if (loginDateString != null && !loginDateString.equals("")) {
				Date loginDate = new SimpleDateFormat("yyyy-MM-dd").parse(loginDateString);				
				long login =loginDate.getTime();
				if (nowDate - login <= 1209600000) {
					activeMember ++;
				}
			}
			
			if(createDate != null) {
				Integer month = Integer.parseInt(createDate.split("-")[1]);
				System.out.println("月份"+month);
				arrMonth[month-1] +=1;
				System.out.println("數量"+arrMonth);	
			}
			String gender = result.get(i).getMemberDetail().getGender();
			if(gender.equals("男")) {
				men++;
			}else if(gender.equals("女")) {
				women++;
			}else {
				secret++;
			}
		}
		int count =  Arrays.stream(arrMonth).sum();
		int[] arrMonthSum = new int[]{0,0,0,0,0,0,0,0,0,0};
		int sum = 0;
		for(int i=0; i<arrMonth.length; i++) {
			arrMonthSum[i] = sum + arrMonth[i];
			sum += arrMonth[i];
		}
		int percentage = (activeMember*100)/count;
		m.addAttribute("percentage", percentage);
		m.addAttribute("allAmount", count);
		m.addAttribute("allMonth", arrMonth);
		m.addAttribute("arrMonthSum",arrMonthSum);
		m.addAttribute("men", men);
		m.addAttribute("women", women);
		m.addAttribute("secret", secret);
		m.addAttribute("activeMember",activeMember);
		
		return "admin/AdminBackstage"; // 導向AdminBackstage.jsp頁面
	}

	@RequestMapping("/admin/AdminMemberNew")
	public String adminMemberNew() {
		return "admin/AdminMemberNew"; // 導向AdminMemberNew.jsp頁面
	}

	@RequestMapping("/FrontStageMain")
	public String FrontStageMain() {
		return "admin/FrontStageMain"; // 導向前台頁面
	}

	@RequestMapping("/user/UserCenter")
	public String UserCenter() {
		return "admin/UserCenter"; // 導向userCenter
	}

	@RequestMapping("/Register")
	public String Register() {
		return "admin/Register"; // 導向Register
	}

	@RequestMapping("/ForgetPassword")
	public String ForgetPassword() {
		return "admin/ForgetPassword"; // 導向ForgetPassword
	}

	// 常常使用到的查找管理員
	private String SearchAdmin(Model m) {
		List<String> adminTitleList = adminService.adminTitleList();
		List<AdminBean> result = adminService.selectAll();
		System.out.println("查找資料成功");
		m.addAttribute("adminList", result);
		m.addAttribute("adminTitleList", adminTitleList);
		return "admin/AdminAdmin";
	}

	@RequestMapping(path = "/admin/adminAdminAction.controller")
	private String adminAdminAction(Model m) {
		return SearchAdmin(m);
	}

	@PostMapping(path = "/admin/adminSubmitActionModifyAndNew.controller") // 新增+修改
	private String newAdminSubmitAction(String account, String pwd, Integer authority, String modifyimage, String sub,
			String idNumString, String modifyAdminPhoto, String originalRealPassword,
			@RequestParam("filepath") MultipartFile mf, Model m) {
		String adminPhoto;
		// 新增
		String bcEncode = new BCryptPasswordEncoder().encode(pwd);
		if (sub.equals("newAdminSubmitAction")) {
			adminPhoto = adminService.imageProcess(account, modifyimage, mf, false);
			AdminBean adminBean = new AdminBean(account, bcEncode, authority, adminPhoto);
			adminService.insert(adminBean);
			System.out.println("管理員新增成功");
		}
		// 修改******
		else {
			AdminBean adminBean;
			String modifyPassword;
			if (pwd.equals("******")) {
				System.out.println("123456" + originalRealPassword);
				modifyPassword = originalRealPassword;
			} else {
				modifyPassword = new BCryptPasswordEncoder().encode(pwd);
			}
			if (modifyimage.equals("true")) {
				adminPhoto = adminService.imageProcess(account, modifyimage, mf, false);
				adminBean = new AdminBean(account, modifyPassword, authority, adminPhoto);
			} else {
				adminBean = new AdminBean(account, modifyPassword, authority, modifyAdminPhoto);
			}
			Integer idNum = Integer.parseInt(idNumString);
			adminBean.setId(idNum);
			AdminBean aBean = adminService.updateOne(adminBean);
			System.out.println((m.getAttribute("loginMember")));
			// 圖片更動 修改頭像
			AdminBean nowBean = (AdminBean) m.getAttribute("loginMember");
			if (aBean.getId().equals(nowBean.getId())) {
				m.addAttribute("loginMember", aBean);
			}
			System.out.println("管理員修改成功");
		}
		return SearchAdmin(m);
	}

	@RequestMapping(path = "/admin/modifyAdminNameAction.controller")
	private String modifyAdminNameAction(String modifyId, String modifyAdminPhoto, String modifyAdminName,
			String modifyAdminPwd, String modifyAuthority, Model m) {
		m.addAttribute("account", modifyId);
		m.addAttribute("modifyAdminPhoto", modifyAdminPhoto);
		m.addAttribute("name", modifyAdminName);
		m.addAttribute("pwd", modifyAdminPwd);
		m.addAttribute("auth", modifyAuthority);
		System.out.println("查找單一資料成功");
		return "admin/AdminNew";
	}

	@PostMapping(path = "/admin/deleteNameAction.controller") // 刪除，回傳id字串
	private String deleteNameAction(String deleteNames, Model m) {
		String[] memberList = deleteNames.split(" ");
		List<Integer> memberListInteger = new ArrayList<Integer>();
		for (int i = 0; i < memberList.length; i++) {
			memberListInteger.add(Integer.parseInt(memberList[i]));
		}
		adminService.deleteData(memberListInteger);
		return SearchAdmin(m);
	}

	// 常常使用到的查找會員
	private String SearchMember(Model m) {
		List<String> memberTitleList = adminService.memberTitleList();
		List<MemberBean> resultMember = adminService.selectAllMember();
		System.out.println("查找資料成功");
		m.addAttribute("MemberList", resultMember);
		m.addAttribute("memberTitleList", memberTitleList);
		return "admin/AdminMember";
	}

	@RequestMapping(path = "/admin/adminMemberAction.controller")
	private String adminMemberAction(Model m) {
		return SearchMember(m);
	}

	@PostMapping(path = "/admin/adminMemberModifyAndNew.controller") // 新增+修改
	private String newMemberSubmitAction(String account, String pwd, String modifyimage, String sub, String idNumString,
			String name, String email, String gender, String nickname, String birthday, String cellphone,
			String zipcode, String address, String registerReferralCode, String match, String pairContactInfo,
			String pairRequest, String pairInfo, String nowimage, @RequestParam("mute") String muteModify,
			@RequestParam("referralCode") String referralCodeModify, String originalRealPassword,
			@RequestParam("postPermission") String postPermissionModify,
			@RequestParam("recentLoginDate") String recentLoginDateModify, @RequestParam("filepath") MultipartFile mf,
			Model m, String detailId) {

		String memberPhoto;
		Integer authority = 0;
		Integer pairWilling = Integer.parseInt(match);
		// 新增
		if (sub.equals("newMemberSubmitAction")) {
			String bcEncode = new BCryptPasswordEncoder().encode(pwd);
			System.out.println("正在新增會員");
			// 推薦碼需要會員ID才能生成，設定初值為空字串
			String referralCode = "";
			Integer mute = 0;
			Integer postPermission = 0;
			String recentLoginDate = adminService.getDate();
			memberPhoto = adminService.imageProcess(account, modifyimage, mf, true);

			MemberDetail mDetail = new MemberDetail(gender, nickname, birthday, cellphone, zipcode, address,
					referralCode, registerReferralCode, mute, postPermission, pairWilling, pairContactInfo, pairRequest,
					pairInfo, null);
			MemberBean mBean = new MemberBean(account, bcEncode, authority, name, memberPhoto, email, mDetail);
			MemberBean mBean1 = adminService.insert(mBean);
			referralCode = adminService.encode(mBean1.getId());
			// 生成推薦碼
			MemberDetail memberDetail = mBean1.getMemberDetail();
			memberDetail.setReferralCode(referralCode);
			adminService.updateCodeById(memberDetail);
			System.out.println("會員新增成功");

			// 修改
		} else {
			String modifyPassword;
			System.out.println(originalRealPassword);
			System.out.println(pwd);
			if (pwd.equals("******")) {
				modifyPassword = originalRealPassword;
			} else {
				modifyPassword = new BCryptPasswordEncoder().encode(pwd);
			}
			String referralCode = referralCodeModify;
			Integer mute = Integer.parseInt(muteModify);
			Integer postPermission = Integer.parseInt(postPermissionModify);
			String recentLoginDate = recentLoginDateModify;
			MemberDetail mDetail = new MemberDetail(gender, nickname, birthday, cellphone, zipcode, address,
					referralCode, registerReferralCode, mute, postPermission, pairWilling, pairContactInfo, pairRequest,
					pairInfo, recentLoginDate);
			mDetail.setId(Integer.parseInt(detailId));
			MemberBean mBean;
			if (modifyimage.equals("true")) {
				memberPhoto = adminService.imageProcess(account, modifyimage, mf, false);
				mBean = new MemberBean(account, modifyPassword, authority, name, memberPhoto, email, mDetail);
			} else {
				mBean = new MemberBean(account, modifyPassword, authority, name, nowimage, email, mDetail);
			}
			Integer idNum = Integer.parseInt(idNumString);
			mBean.setId(idNum);
			adminService.updateOne(mBean);
			System.out.println("會員修改成功");
		}
		return SearchMember(m);
	}

	@PostMapping(path = "/admin/modifyMemberNameAction.controller") //查找會員資料
	private String modifyMemberNameAction(@RequestParam("modifyId") String modifyId, Model m) {
		Integer id = Integer.parseInt(modifyId);
		MemberBean resultMember = adminService.selectOneMember(id);
		System.out.println("查找單一資料成功");
		m.addAttribute("OneMember", resultMember);
		return "admin/AdminMemberNew";
	}

	@PostMapping(path = "/admin/deleteMemberNameAction.controller") // 刪除會員(字串Ids)
	private String deleteMemberNameAction(String deleteNames, Model m) {
		System.out.println("進入刪除");
		String[] memberList = deleteNames.split(" ");
		List<Integer> memberListInteger = new ArrayList<Integer>();
		for (int i = 0; i < memberList.length; i++) {
			memberListInteger.add(Integer.parseInt(memberList[i]));
		}
		adminService.deleteMemberData(memberListInteger);
		return SearchMember(m);
	}

}