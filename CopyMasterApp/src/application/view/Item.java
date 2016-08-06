package application.view;

public class Item {

	private String number;
	private String name;
	private String value;
	private String displayValue;
	private boolean isPassword;

	public Item(String sNo, String name, String value, boolean isPassword) {
		this.number = sNo;
		this.name = name;
		this.value = value;
		this.isPassword = isPassword;
		if (isPassword) {
			int len = value.length();
			if (len > 15) {
				this.displayValue = getStars(15) + "(" + len + ")";
			} else {
				this.displayValue = getStars(len);
			}
		} else {
			this.displayValue = value;
		}
	}

	private String getStars(int n) {
		String s = "";
		for (int i = 1; i <= n; i++) {
			s += "*";
		}
		return s;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	public boolean isPassword() {
		return isPassword;
	}

	public void setPassword(boolean isPassword) {
		this.isPassword = isPassword;
	}

}
