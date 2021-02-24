package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class AdicionarFacebookAction  extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String authUrl;

    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        authUrl=this.getHeyBean().adFacebook();
        System.out.println(authUrl);
        return SUCCESS;

    }

    public void setAuthUrl(String authUrl ) {
        this.authUrl = authUrl; // will you sanitize this input? maybe use a prepared statement?
    }
    public String getAuthUrl( ) {
        return this.authUrl; // will you sanitize this input? maybe use a prepared statement?
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

