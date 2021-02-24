package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class TraduzirAction  extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String url = null;
    public String pesquisa;
    public ArrayList info;


    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        String[] str;
        if (session.containsKey("pesquisa") && !(session.get("pesquisa")==null) && !(session.get("pesquisa")=="") ) {

            info= (ArrayList) session.get("result");
            System.out.println(pesquisa);
            if (!info.equals("")) {
                this.getHeyBean().traduzPesquisa(info);
                session.put("result", info);
                System.out.println(info);
                return SUCCESS;
            }
            return SUCCESS;
        }
        else
            return SUCCESS;

    }

    public void setPesquisa(String pesquisa) {
        this.pesquisa=pesquisa;
    }
    public String getPesquisa() {
        return this.pesquisa;
    }

    public void setInfo(ArrayList info) {
        this.info=info;
    }
    public ArrayList getInfo() {
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


