package model.intelligence;

public interface Intelligence {
	public void errorMail();
	public void errorPopup();
	public void errorSMS();
	
	public void checkCritical(String[] newin);

}
