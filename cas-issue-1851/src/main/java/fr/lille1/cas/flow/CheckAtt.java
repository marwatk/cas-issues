package fr.lille1.cas.flow;

import java.util.List;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;


import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.auth.AuthenticationRequest;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResultCode;
import org.ldaptive.auth.AccountState;
import org.ldaptive.Credential;
import org.ldaptive.auth.Authenticator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.L1LdapAuthenticationHandler;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.authentication.principal.WebApplicationServiceFactory;
import org.jasig.cas.web.support.WebUtils;

public class CheckAtt{
  @NotNull
  private String sesameActivationId;
  @NotNull
  private String sesameCompteId;
  @NotNull
  private String servicesAllowed;
  @NotNull
  private boolean enabled;
  @NotNull
  private L1LdapAuthenticationHandler ldapAuthenticationHandler;
  private Log logger = LogFactory.getLog(this.getClass());
  public void setEnabled(boolean enabled){
	this.enabled=enabled;
  }
	public boolean getEnabled(){
	return this.enabled;
  }
  public void setSesameActivationId (final String sesameActivationId) {
    this.sesameActivationId = sesameActivationId;
  } 
  public String getSesameActivationId () {
    return this.sesameActivationId;
  }
  public void setSesameCompteId (final String sesameCompteId) {
    this.sesameCompteId=sesameCompteId;
  } 
  public String getSesameCompteId () {
    return this.sesameCompteId;
  }
  public void setServicesAllowed (final String servicesAllowed) {
    this.servicesAllowed = servicesAllowed;
  } 
  public String getServicesAllowed () {
    return this.servicesAllowed;
  }
  public final String log(final RequestContext context, final Service service) throws Exception {
	logger.debug("CheckAtt service= " +service );
	logger.debug("CheckAtt service= " +WebUtils.getService(context) );
	return "";
  }
  public final String checkAndRedirectService(final RequestContext context, final Service service) throws Exception {
    String _return="";
	String serviceId=service.getId();
    logger.debug("CheckAtt serviceId= " +serviceId );
    if(enabled){
		_return="noTGT"; // par défaut pas d'envoi du TGT
		logger.debug("CheckAtt enabled");
		logger.debug("ldapAuthenticationHandler:"+ldapAuthenticationHandler);
	    final AuthenticationResponse response = ldapAuthenticationHandler.response;
		if(response!=null){
			if(response.getResult()==null){
				logger.error("CheckAtt erreur ldap (ne peut pas avoir de resultat provenant de l'authicator)");
			} else {
				LdapEntry ldapEntry = response.getLdapEntry();
				logger.debug(ldapEntry);
				if(returnContinue(ldapEntry,service)) {
					_return = "continue";
				}  else if(returnActivation(ldapEntry,service)) {
					Service sesameActivationService= new WebApplicationServiceFactory().createService(sesameActivationId);
					WebUtils.putService(context,sesameActivationService);
					_return = "activation";
				} else if(returnCompte(ldapEntry,service)) {
					Service sesameActivationService= new WebApplicationServiceFactory().createService(sesameActivationId);
					WebUtils.putService(context,sesameActivationService);
					_return = "compte";
				} else if(returnNoTGT(ldapEntry,service)) {
				} else {
					logger.debug("CheckAtt n'a pas su se determiner ! (pas d'envoi de TGT)");
				}
			}
		}
    } else {
		_return="continue"; // on envoi le TGT si checkATT est disabled
		logger.debug("CheckAtt disabled");
    }
    logger.debug("CheckAtt returns "+_return);
    return _return;
  }
	/* la methodes suivante est appelée si la précédante renvoie false */
	/* appele en premier : attributs ok */
	protected boolean returnContinue(LdapEntry ldapentry, Service service){
		return true;
	}
	/* appele en deuxième : la personne doit être redirigée vers l'activation de son compte*/
	protected boolean returnActivation(LdapEntry ldapEntry, Service service){
		return true;
	}
	/* appele en troisième : la personne doit être redirigée vers la gestion de son compte (mdp)*/
	protected boolean returnCompte(LdapEntry ldapEntry, Service service){
		return true;
	}
	/* appele en quatrième : les attributs ne sont pas corrects mais le service est autorise*/
	protected boolean returnNoTGT(LdapEntry ldapEntry, Service service){
		return true;
	}


	protected boolean serviceAllowed(String serviceId){
		  String[] services = getServicesAllowed().split(" ");
		  for(String s : services){
			if(s.length()>0&&serviceId.startsWith(s)) return true;
		  }
          return false;
    }
	
	protected boolean containsValue(LdapEntry entry, String attribute, String value) {
		for(String v : entry.getAttribute(attribute).getStringValues()){
			if(v.equals(value)) return true;
		}
		return false;
    }

	protected boolean objectClassContainsValue(LdapEntry entry,  String value) {
		return containsValue(entry, "objectClass", value);
    }


	public void setLdapAuthenticationHandler(final L1LdapAuthenticationHandler ldapAuthenticationHandler){
		this.ldapAuthenticationHandler=ldapAuthenticationHandler;
	}
	public boolean isService(LdapEntry entry) {
		return objectClassContainsValue(entry, "ustlService");
	}

}



