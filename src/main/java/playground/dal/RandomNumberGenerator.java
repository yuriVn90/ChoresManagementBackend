package playground.dal;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class RandomNumberGenerator implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6876199095482643818L;
	private Long nextNumber;

	public RandomNumberGenerator() {
	}
	
	@Id
	@GeneratedValue(generator = MyCustomGenerator.generatorName)
    @GenericGenerator(name = MyCustomGenerator.generatorName, strategy = "playground.dal.MyCustomGenerator")
	public Long getNextNumber() {
		return nextNumber;
	}

	public void setNextNumber(Long nextNumber) {
		this.nextNumber = nextNumber;
	}

}
