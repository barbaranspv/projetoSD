package projeto.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import projeto.model.ProjetoBean;

import java.rmi.RemoteException;
import java.util.Map;

public class AdicionarFacebook2Action extends ActionSupport implements SessionAware {

    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code = null;

    @Override
    public String execute() throws RemoteException {
        // any username is accepted without confirmation (should check using RMI)
        String info;
        String result;
        String[] splitres;
        result=this.getHeyBean().adFacebook2(code);
        System.out.println((result));
        splitres= result.split("id\":\"");
        result=splitres[1].replaceAll("\"}", "");
        System.out.println(result);
        if(result != null) {
            info = this.getHeyBean().confereLogin(result,"hjgkfldmejfgiureonoswsaosaoza");
            System.out.println(info);
            String[] temp=info.split("-");
            System.out.println(temp[0]);
            if (temp[0].equals("admin")) {

                this.getHeyBean().setUsername(result);
                this.getHeyBean().setPassword("hjgkfldmejfgiureonoswsaosaoza");
                session.put("username", result);
                session.put("loggedin", true); // this marks the user as logged in
                session.put("admin", true);
                String[] note;
                note=temp[1].split(" ");
                if (note[0].equals("Tem")){
                    session.put("notifica", temp[1]+temp[2]);

                }
                else{
                    session.put("notifica", temp[1]);
                }
                return SUCCESS;
            } else if (temp[0].equals("normal")) {
                this.getHeyBean().setUsername(result);
                this.getHeyBean().setPassword("hjgkfldmejfgiureonoswsaosaoza");
                session.put("username", result);
                session.put("loggedin", true); // this marks the user as logged in
                session.put("admin", false);
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
            else{
                info=this.getHeyBean().registaUtilizador(result,"hjgkfldmejfgiureonoswsaosaoza");
                if (info.equals("admin")){
                    this.getHeyBean().setUsername(result);
                    this.getHeyBean().setPassword("hjgkfldmejfgiureonoswsaosaoza");
                    session.put("username", result);
                    session.put("loggedin", true); // this marks the user as logged in
                    session.put("admin",true);
                    return SUCCESS;
                }

                else if (info.equals("normal")){

                    this.getHeyBean().setUsername(result);
                    this.getHeyBean().setPassword("hjgkfldmejfgiureonoswsaosaoza");
                    session.put("username", result);
                    session.put("loggedin", true); // this marks the user as logged in
                    session.put("admin",false);
                    return SUCCESS;
                }
                else{
                    return "register";
                }
            }
        }
        System.out.println(code);
        return SUCCESS;

    }

    public void setCode(String code ) {
        this.code = code; // will you sanitize this input? maybe use a prepared statement?
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

