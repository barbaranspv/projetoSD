package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class PesquisarAction extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String shareUrl = null;
    public String pesquisa;
    public ArrayList info;


    @Override
    public String execute() throws RemoteException, UnsupportedEncodingException {
        // any username is accepted without confirmation (should check using RMI)

        if (!(pesquisa==null) ) {
            pesquisa= URLDecoder.decode(pesquisa,"UTF-8");
            info = this.getHeyBean().efetuarPesquisa((String) session.get("username"), pesquisa);
            System.out.println(pesquisa);
            if (!info.equals("")) {
                session.put("result", info);
                session.put("pesquisa", pesquisa);
                System.out.println(info);
                        }
            pesquisa= URLEncoder.encode(pesquisa,"UTF-8");
            shareUrl="https://www.facebook.com/dialog/feed?app_id=***REMOVED***&display=popup&link=https://ucbusca.pt:8443/ProjetoSD/pesquisar.action?pesquisa="+pesquisa +"&redirect_uri=https://ucbusca.pt:8443/ProjetoSD/pesquisar.action?pesquisa="+pesquisa;
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

    public void setShareUrl(String shareUrl) {
        this.shareUrl=shareUrl;
    }
    public String getShareUrl() {
        return this.shareUrl;
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


