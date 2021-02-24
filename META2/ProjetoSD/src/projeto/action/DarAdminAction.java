package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class DarAdminAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String usertoadmin=null;

    @Override
    public String execute() throws RemoteException {

        // any username is accepted without confirmation (should check using RMI)
        if(session.get("username") != null && !session.get("username").equals("") && this.usertoadmin!=null) {
            String[] info;
            info= this.getHeyBean().darAdmin((String) session.get("username"), usertoadmin).split(" ");
            System.out.println(info[0]);
            if (info[0].equals("n|Definiste")){

                return SUCCESS;
            }
            else{
                return "daradmin";
            }
        }
        else
            return "daradmin";
    }

    public void setUsertoadmin(String usertoadmin ) {
        this.usertoadmin = usertoadmin; // will you sanitize this input? maybe use a prepared statement?
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


