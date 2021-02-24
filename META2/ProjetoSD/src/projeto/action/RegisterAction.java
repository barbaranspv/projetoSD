
package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class RegisterAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;

    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        String info;

        if(this.username != null && !username.equals("")) {
            info=this.getHeyBean().registaUtilizador(this.username,this.password);
            if (info.equals("admin")){

                this.getHeyBean().setUsername(this.username);
                this.getHeyBean().setPassword(this.password);
                session.put("username", username);
                session.put("loggedin", true); // this marks the user as logged in
                session.put("admin",true);
                return SUCCESS;
            }

            else if (info.equals("normal")){

                this.getHeyBean().setUsername(this.username);
                this.getHeyBean().setPassword(this.password);
                session.put("username", username);
                session.put("loggedin", true); // this marks the user as logged in
                session.put("admin",false);
                return SUCCESS;
            }
            else{
                return "register";
            }
        }
        else
            return "register";
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
