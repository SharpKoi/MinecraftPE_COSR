package cosr.mcpemail;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class McpeGmail {
	private static Properties props = null;
	private static Session session = null;
	private static MimeMessage msg = null;
	private static String mailAddress = "None";
	private static String password = "None";
	private static String source = "Minecraft PE Server";
	
	private String subtitle;
	private String content;
	private String sender;
	private String recipient;
	
	public McpeGmail(String sender, String recipient, String subtitle, String content) {
		this.sender = sender;
		this.recipient = recipient;
		this.subtitle = subtitle;
		this.content = content;
	}
	
	public McpeGmail() {
		this("Unknown", "", "(No Subject)", "None");
	}
	
	/*Use command to set the public mail address and password*/
	public static void setPublicSender(String mailAddress, String password) {
		McpeGmail.mailAddress = mailAddress;
		McpeGmail.password = password;
	}
	
	/*Use command to set mail source*/
	public static void setMailSource(String source) {
		McpeGmail.source = source;
	}
	
	/*Use this after mail address and password setter command executed*/
	public static void init() {
		props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", 587);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.password", password);
		session = Session.getDefaultInstance(props, 
			new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(mailAddress, password);
				}
			});
		msg = new MimeMessage(session);
	}
	
	public boolean check() {
		return (props == null || session == null || msg == null || mailAddress.equals("None"))? false : true;
	}
	
	public boolean sendOut() throws AuthenticationFailedException {
		if(check()) {
			try {
				InternetAddress from = new InternetAddress(mailAddress);
				InternetAddress to = new InternetAddress(this.recipient);
				msg.setFrom(from);
				msg.setRecipient(Message.RecipientType.TO, to);
				msg.setSubject(subtitle, "Utf-8");
				msg.setText(this.compoundContent(), "Utf-8", "html");
				Transport.send(msg);
				return true;
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private String compoundContent() {
		String content = "";
		
		content = "<pre style=\"font-size:12px;\">\r\n" + 
					"<strong style=\"color:#0066ff;\">寄件人: </strong><font style=\"color:#003377;\">" + this.sender + "</font>\r\n" + 
					"</pre>\r\n" + 
					
					"<pre style=\"font-size:12px;\">\r\n" + 
					"<strong style=\"color:#0066ff;\">來源: </strong><font style=\"color:#003377;\">" + McpeGmail.source + "</font>" + 
					"</pre>\r\n" + 
					
					"<pre style=\"font-size:12px;\">\r\n" + 
					"<strong style=\"color:#0066ff;\">內容: </strong>\r\n" + 
					"<font>" + this.content + "</font>" + 
					"</pre>";
		
		return content;
	}
}
