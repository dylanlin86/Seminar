package tw.group5.gym.model;

import java.io.Serializable;
import java.util.HashSet;
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
@Table(name = "gym")
@Component
public class GymBean implements Serializable{
	
//	/**
//	 *序列化版本 
//	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "GYMNO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer gymId;
	
	@Column(name = "GYMNAME")
	private String gymName;
	
	@Column(name = "GYMADDRESS")
	private String gymAddress;
	
	@Column(name = "GYMOPENHOURS")
	private String gymOpenHours;
	
	@Column(name = "GYMRATING")
	private Integer rating;
	
	@Column(name = "GYMPICTURE")
	private String gymPicture;
	
	
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "gym", cascade = CascadeType.ALL)
	@Transient
	private Set<GymLog> gymLogs = new HashSet<GymLog>();
	
	public GymBean() {
		
	}
	

	public GymBean(String gymName, String gymAddress, String gymOpenHours) {
		super();
		this.gymName = gymName;
		this.gymAddress = gymAddress;
		this.gymOpenHours = gymOpenHours;
	}
	
	public GymBean(String gymName, String gymAddress) {
		super();
		this.gymName = gymName;
		this.gymAddress = gymAddress;
	}

	public Integer getGymId() {
		return gymId;
	}

	public void setGymId(Integer gymId) {
		this.gymId = gymId;
	}

	public String getGymName() {
		return gymName;
	}

	public void setGymName(String gymName) {
		this.gymName = gymName;
	}

	public String getGymAddress() {
		return gymAddress;
	}

	public void setGymAddress(String gymAddress) {
		this.gymAddress = gymAddress;
	}

	public String getGymOpenHours() {
		return gymOpenHours;
	}

	public void setGymOpenHours(String gymOpenHours) {
		this.gymOpenHours = gymOpenHours;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Set<GymLog> getGymLogs() {
		return gymLogs;
	}


	public void setGymLogs(Set<GymLog> favoriteGyms) {
		this.gymLogs = favoriteGyms;
	}


	public String getGymPicture() {
		return gymPicture;
	}


	public void setGymPicture(String gymPicture) {
		this.gymPicture = gymPicture;
	}

	
	

}
