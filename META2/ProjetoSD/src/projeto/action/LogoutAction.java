
package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.Map;
import projeto.model.ProjetoBean;

public class LogoutAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null;

    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        if((String) session.get("username") != null && !((String)session.get("username")).equals("")) {
                this.getHeyBean().efetuarLogout((String) session.get("username"));
                session.put("loggedin", false); // this marks the user as logged in
                return SUCCESS;
            }
            else{
                return ERROR;
            }
        }


    public void setUsername(String username) {

        this.username = username; // will you sanitize this input? maybe use a prepared statement?
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
