package tw.group5.activity.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "activity")
@Component
public class ActivityActivity {

	@Id
	@Column(name = "activityid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int activityId;
	
	@Column(name = "a_account")
	private String a_account;
	
	@Column(name = "typecontent")
	private String typeContent;
	
	@Column(name = "activitytitle")
	private String activityTitle;
	
	@Column(name = "activitycontent")
	private String activityContent;
	
	@Column(name = "signupdeadline")
	private String signUpDeadline;
	
	@Column(name = "starttime")
	private String startTime;
	
	@Column(name = "endtime")
	private String endTime;
	
	private String holder;
	private String location;
	
	@Column(name = "revisetime")
	private String reviseTime;
	
	@Column(name = "photodata")
	private String photoData;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "activity", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<ActivitySignUp> signUp = new LinkedHashSet<ActivitySignUp>();

	@Column(name = "totalsignup")
	private int totalSignUp;

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public String getA_account() {
		return a_account;
	}

	public void setA_account(String a_account) {
		this.a_account = a_account;
	}

	public String getTypeContent() {
		return typeContent;
	}

	public void setTypeContent(String typeContent) {
		this.typeContent = typeContent;
	}

	public String getActivityTitle() {
		return activityTitle;
	}

	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}

	public String getActivityContent() {
		return activityContent;
	}

	public void setActivityContent(String activityContent) {
		this.activityContent = activityContent;
	}

	public String getSignUpDeadline() {
		return signUpDeadline;
	}

	public void setSignUpDeadline(String signUpDeadline) {
		this.signUpDeadline = signUpDeadline;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getReviseTime() {
		return reviseTime;
	}

	public void setReviseTime(String reviseTime) {
		this.reviseTime = reviseTime;
	}

	public String getPhotoData() {
		return photoData;
	}

	public void setPhotoData(String photoData) {
		this.photoData = photoData;
	}

	public Set<ActivitySignUp> getSignUp() {
		return signUp;
	}

	public void setSignUp(Set<ActivitySignUp> signUp) {
		this.signUp = signUp;
	}

	public int getTotalSignUp() {
		return totalSignUp;
	}

	public void setTotalSignUp(int totalSignUp) {
		this.totalSignUp = totalSignUp;
	}
	
	
}
