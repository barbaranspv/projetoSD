package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class VerPesquisasAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    public String info[]=null;


    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        if(session.get("username") != null && !session.get("username").equals("") ) {
            info = this.getHeyBean().verPesquisas((String) session.get("username"));
            this.setInfo(info);

        }
        return "verpesquisas";

    }

    public void setInfo(String[] Info) {
        this.info=info;
    }

    public String[] getInfo() {
        return this.info;
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


