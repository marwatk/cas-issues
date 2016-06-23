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

public class L1CheckAtt extends CheckAtt{

  private Log logger = LogFactory.getLog(this.getClass());
 
	/* la methodes suivante est appelée si la précédante renvoie false */
	/* appele en premier : attributs ok */
	protected boolean returnContinue(LdapEntry ldapentry, Service service){
		return isLocalMailRecipient(ldapentry) && hasPassword(ldapentry);
	}
	/* appele en deuxième : la personne doit être redirigée vers l'activation de son compte*/
	protected boolean returnActivation(LdapEntry ldapEntry, Service service){
		return !isLocalMailRecipient(ldapEntry)&&!getSesameActivationId().equals(service.getId());
	}
	/* appele en troisième : la personne doit être redirigée vers la gestion de son compte (mdp)*/
	protected boolean returnCompte(LdapEntry ldapEntry, Service service){
		return isLocalMailRecipient(ldapEntry)&&!hasPassword(ldapEntry)&&!getSesameCompteId().equals(service.getId());
	}
	/* appele en quatrième : les attributs ne sont pas corrects mais le service est autorise*/
	protected boolean returnNoTGT(LdapEntry ldapEntry, Service service){
		return serviceAllowed(service.getId());
	}




	private boolean isLocalMailRecipient(LdapEntry entry) {
		return objectClassContainsValue(entry, "inetLocalMailRecipient");
	}
	private boolean hasPassword(LdapEntry entry) {
		return entry.getAttribute("ntPassword")!=null;
	}


}



