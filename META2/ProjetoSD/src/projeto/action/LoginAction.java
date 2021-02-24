
package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 4L;
	private Map<String, Object> session;
	private String username = null, password = null;

	@Override
	public String execute() throws RemoteException {
		// any username is accepted without confirmation (should check using RMI)

		String info;
		if(this.username != null && !username.equals("")) {
			info=this.getHeyBean().confereLogin(this.username,this.password);
			String[] temp=info.split("-");
			System.out.println(temp[0]);
			if (temp[0].equals("admin")){

				this.getHeyBean().setUsername(this.username);
				this.getHeyBean().setPassword(this.password);
				//this.getHeyBean().getNotificacoes(this.username);
				session.put("username", username);
				session.put("loggedin", true); // this marks the user as logged in
				session.put("admin",true);
				String[] note;
				note=temp[1].split(" ");
				System.out.println(note[0]);
				if (note[0].equals("Tem")){
					session.put("notifica", temp[1]+"-"+temp[2]);

				}
				else{
					session.put("notifica", temp[1]);
				}

				return SUCCESS;
			}
			else if (temp[0].equals("normal")){
				this.getHeyBean().setUsername(this.username);
				this.getHeyBean().setPassword(this.password);
				session.put("username", username);
				session.put("loggedin", true); // this marks the user as logged in
				session.put("admin",false);
				String[] note;
				note=temp[1].split(" ");
				System.out.println(note[0]);
				if (note[0].equals("Tem")){
					session.put("notifica", temp[1]+temp[2]);
				}
				else{
					session.put("notifica", temp[1]);
				}
				return SUCCESS;
			}
		else{
			return LOGIN;
			}
		}
		else
			return LOGIN;
	}

	public void setUsername(String username) {

		this.username = username; // will you sanitize this input? maybe use a prepared statement?
	}

	public void setPassword(String password) {
		this.password = password; // what about this input? 
	}

	public ProjetoBean getHeyBean() {
		if(!session.containsKey("heyBean"))
			this.setHeyBean(new ProjetoBean());
		return (ProjetoBean) session.get("heyBean");
	}

	public void setHeyBean(ProjetoBean projetoBean) {
		this.session.put("heyBean", projetoBean);
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
