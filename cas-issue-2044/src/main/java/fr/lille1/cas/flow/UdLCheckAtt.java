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

public class UdLCheckAtt{
  @NotNull
  private String urlExpired;
  @NotNull
  private String urlBlocked;
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
  public void setUrlExpired (final String urlExpired) {
    this.urlExpired = urlExpired;
  } 
  public String getUrlExpired () {
    return this.urlExpired;
  }
  public String getUrlBlocked() {
    return this.urlBlocked;
  }
  public void setUrlBlocked (final String urlBlocked) {
    this.urlBlocked=urlBlocked;
  } 

  public final String checkAndRedirectService(final RequestContext context, final Service service) throws Exception {
    String _return="";
	String serviceId=service.getId();
    logger.debug("UdLCheckAtt serviceId= " +serviceId );
    if(enabled){
		_return="noTGT"; // par défaut pas d'envoi du TGT
		logger.debug("UdLCheckAtt enabled");
		logger.debug("ldapAuthenticationHandler:"+ldapAuthenticationHandler);
	    final AuthenticationResponse response = ldapAuthenticationHandler.response;
		if(response!=null){
			if(response.getResult()==null){
				logger.error("CheckAtt erreur ldap (ne peut pas avoir de resultat provenant de l'authenticator)");
			} else {
				LdapEntry ldapEntry = response.getLdapEntry();
				logger.debug(ldapEntry);
				if(returnContinue(ldapEntry,service)) {//retour vers service demandé
					_return = "continue";
				}  else if(returnExpired(ldapEntry,service)) {
					Service expiredService= new WebApplicationServiceFactory().createService(urlExpired);
					WebUtils.putService(context,expiredService);
					_return = "expired";
				} else if(returnBlocked(ldapEntry,service)) {
					Service blockedService= new WebApplicationServiceFactory().createService(urlBlocked);
					WebUtils.putService(context,blockedService);
					_return = "blocked";
				} else if(returnNoTGT(ldapEntry,service)) {
				} else {
					logger.debug("UdLCheckAtt n'a pas su se determiner ! (pas d'envoi de TGT)");
				}
			}
		}
    } else {
		_return="continue"; // on envoi le TGT si checkATT est disabled
		logger.debug("UdLCheckAtt disabled");
    }
    logger.debug("UdLCheckAtt returns "+_return);
    return _return;
  }
	/* la methodes suivante est appelée si la précédante renvoie false */
	/* appele en premier : attributs ok */
	protected boolean returnContinue(LdapEntry ldapEntry, Service service){
		return "Active".equals(ldapEntry.getAttribute("udlAccountStatus"));
	}
	/* appele en deuxième : la personne doit être redirigée vers le lien expiré*/
	protected boolean returnExpired(LdapEntry ldapEntry, Service service){
		return "Expired".equals(ldapEntry.getAttribute("udlAccountStatus"));
	}
	/* appele en troisième : la personne doit être redirigée vers le lien bloqué*/
	protected boolean returnBlocked(LdapEntry ldapEntry, Service service){
		return "Blocked".equals(ldapEntry.getAttribute("udlAccountStatus"));
	}
	/* appele en quatrième : les attributs ne sont pas corrects mais le service est autorise*/
	protected boolean returnNoTGT(LdapEntry ldapEntry, Service service){
		return true;
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



