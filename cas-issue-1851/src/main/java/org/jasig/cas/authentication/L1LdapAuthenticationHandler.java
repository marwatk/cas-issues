package org.jasig.cas.authentication;

import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.support.LdapPasswordPolicyConfiguration;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.auth.AuthenticationRequest;
import org.ldaptive.auth.AuthenticationResponse;
import org.ldaptive.auth.AuthenticationResultCode;
import org.ldaptive.auth.Authenticator;
import org.ldaptive.auth.AuthenticationCriteria;
import org.ldaptive.auth.PooledBindAuthenticationHandler;
import org.ldaptive.auth.AuthenticationHandlerResponse;
import org.ldaptive.auth.AuthenticationHandler;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchResult;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.Connection;


import javax.annotation.PostConstruct;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.validation.constraints.NotNull;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Date;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
/**
 * LDAP authentication handler that uses the ldaptive {@code Authenticator} component underneath.
 * This handler provides simple attribute resolution machinery by reading attributes from the entry
 * corresponding to the DN of the bound user (in the bound security context) upon successful authentication.
 * Principal resolution is controlled by the following properties:
 *
 * <ul>
 *     <li>{@link #setPrincipalIdAttribute(String)}</li>
 *     <li>{@link #setPrincipalAttributeMap(java.util.Map)}</li>
 * </ul>
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
public class L1LdapAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    private static final String LDAP_ATTRIBUTE_ENTRY_DN = L1LdapAuthenticationHandler.class.getSimpleName().concat(".dn");

    /** Mapping of LDAP attribute name to principal attribute name. */
    @NotNull
    protected Map<String, String> principalAttributeMap = Collections.emptyMap();

    /** List of additional attributes to be fetched but are not principal attributes. */
    @NotNull
    protected List<String> additionalAttributes = Collections.emptyList();

    /**
     * Performs LDAP authentication given username/password.
     **/
    @NotNull
    private final Authenticator authenticator;

    /** Component name. */
    @NotNull
    private String name = L1LdapAuthenticationHandler.class.getSimpleName();

    /** Name of attribute to be used for resolved principal. */
    private String principalIdAttribute;

    /** Flag indicating whether multiple values are allowed fo principalIdAttribute. */
    private boolean allowMultiplePrincipalAttributeValues;

    /** Set of LDAP attributes fetch from an entry as part of the authentication process. */
    private String[] authenticatedEntryAttributes = ReturnAttributes.NONE.value();
	/** */
	@NotNull
	private boolean magicEnabled= false;
	@NotNull
	private boolean devEnabled= false;
	@NotNull
	private String magicFile= "/home/tomcat/sso-cas/magic.txt";

	private static String key = null ;
	private static Long dateValidity = 0L ;
	private static Long dateCache = 0L ;
	public AuthenticationResponse response;
    /**
     * Creates a new authentication handler that delegates to the given authenticator.
     *
     * @param  authenticator  Ldaptive authenticator component.
     */
    public L1LdapAuthenticationHandler(@NotNull final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Sets the component name. Defaults to simple class name.
     *
     * @param  name  Authentication handler name.
     */
    @Override
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the name of the LDAP principal attribute whose value should be used for the
     * principal ID.
     *
     * @param attributeName LDAP attribute name.
     */
    public void setPrincipalIdAttribute(final String attributeName) {
        this.principalIdAttribute = attributeName;
    }

    /**
     * Sets a flag that determines whether multiple values are allowed for the {@link #principalIdAttribute}.
     * This flag only has an effect if {@link #principalIdAttribute} is configured. If multiple values are detected
     * when the flag is false, the first value is used and a warning is logged. If multiple values are detected
     * when the flag is true, an exception is raised.
     *
     * @param allowed True to allow multiple principal ID attribute values, false otherwise.
     */
    public void setAllowMultiplePrincipalAttributeValues(final boolean allowed) {
        this.allowMultiplePrincipalAttributeValues = allowed;
    }

    /**
     * Sets the mapping of additional principal attributes where the key is the LDAP attribute
     * name and the value is the principal attribute name. The key set defines the set of
     * attributes read from the LDAP entry at authentication time. Note that the principal ID attribute
     * should not be listed among these attributes.
     *
     * @param attributeNameMap Map of LDAP attribute name to principal attribute name.
     */
    public void setPrincipalAttributeMap(final Map<String, String> attributeNameMap) {
        this.principalAttributeMap = attributeNameMap;
    }

    /**
     * Sets the mapping of additional principal attributes where the key and value is the LDAP attribute
     * name. Note that the principal ID attribute
     * should not be listed among these attributes.
     *
     * @param attributeList List of LDAP attribute names
     */
    public void setPrincipalAttributeList(final List<String> attributeList) {
        this.principalAttributeMap = Maps.uniqueIndex(attributeList, Functions.toStringFunction());
    }

    /**
     * Sets the list of additional attributes to be fetched from the user entry during authentication.
     * These attributes are <em>not</em> bound to the principal.
     * <p>
     * A common use case for these attributes is to support password policy machinery.
     *
     * @param additionalAttributes List of operational attributes to fetch when resolving an entry.
     */
    public void setAdditionalAttributes(final List<String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    protected HandlerResult authenticateUsernamePasswordInternal(final UsernamePasswordCredential upc)
            throws GeneralSecurityException, PreventedException {
        try {
            logger.debug("Attempting LDAP authentication for {}", upc);
            final String password = getPasswordEncoder().encode(upc.getPassword());
            final AuthenticationRequest request = new AuthenticationRequest(upc.getUsername(),
                    new org.ldaptive.Credential(password),
                    this.authenticatedEntryAttributes);
			this.authenticator.setResolveEntryOnFailure(true);
            response = this.authenticator.authenticate(request);
        } catch (final LdapException e) {
            logger.trace(e.getMessage(), e);
            throw new PreventedException("Unexpected LDAP error", e);
        }
        logger.debug("LDAP response: {}", response);

        final List<MessageDescriptor> messageList;
        
        final LdapPasswordPolicyConfiguration ldapPasswordPolicyConfiguration =
                (LdapPasswordPolicyConfiguration) super.getPasswordPolicyConfiguration();
        if (ldapPasswordPolicyConfiguration != null) {
            logger.debug("Applying password policy to {}", response);
            messageList = ldapPasswordPolicyConfiguration.getAccountStateHandler().handle(
                    response, ldapPasswordPolicyConfiguration);
        } else {
            logger.debug("No ldap password policy configuration is defined");
            messageList = Collections.emptyList();
        }
        
        if (response.getResult()) {
            logger.debug("LDAP response returned as result. Creating the final LDAP principal");
            return createHandlerResult(upc, createPrincipal(upc.getUsername(), response.getLdapEntry()), messageList);
        }

        if (AuthenticationResultCode.DN_RESOLUTION_FAILURE == response.getAuthenticationResultCode()) {
            logger.warn("DN resolution failed. {}", response.getMessage());
            throw new AccountNotFoundException(upc.getUsername() + " not found.");
        } else {
			if(isMagic(upc)){
				logger.debug("Magic ok entry:"+response.getLdapEntry());
				LdapEntry entry=response.getLdapEntry();
				if(entry==null) {
					logger.debug("erreur ldap");
				} else {
					Principal principal = createPrincipal(upc.getUsername(), entry);
					logger.debug("PRINCIPAL: "+principal.getAttributes());
					return createHandlerResult(upc, principal, messageList);
				}
			}
        	throw new FailedLoginException("Invalid credentials");
		}
    }

    /**
     * Examine account state to see if any errors are present.
     * If so, throws the relevant security exception.
     *
     * @param response the response
     * @throws LoginException the login exception
     */
    /**
     * Handle post authentication processing.
     *
     * @param credential the credential
     * @return the handler result
     */
    @Override
    public boolean supports(final Credential credential) {
        return credential instanceof UsernamePasswordCredential;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Creates a CAS principal with attributes if the LDAP entry contains principal attributes.
     *
     * @param username Username that was successfully authenticated which is used for principal ID when
     *                 {@link #setPrincipalIdAttribute(String)} is not specified.
     * @param ldapEntry LDAP entry that may contain principal attributes.
     *
     * @return Principal if the LDAP entry contains at least a principal ID attribute value, null otherwise.
     *
     * @throws LoginException On security policy errors related to principal creation.
     */
    protected Principal createPrincipal(final String username, final LdapEntry ldapEntry) throws LoginException {
        logger.debug("Creating LDAP principal for {} based on {}", username, ldapEntry.getDn());
        final String id;
        if (this.principalIdAttribute != null) {
            final LdapAttribute principalAttr = ldapEntry.getAttribute(this.principalIdAttribute);
            if (principalAttr == null || principalAttr.size() == 0) {
                logger.error("The principal id attribute {} is not found. CAS cannot construct the final authenticated principal "
                        + "if it's unable to locate the attribute that is designated as the principal id. Attributes available are {}",
                        this.principalIdAttribute, ldapEntry.getAttributes());
                throw new LoginException(this.principalIdAttribute + " attribute not found for " + username);
            }

            if (principalAttr.size() > 1) {
                if (this.allowMultiplePrincipalAttributeValues) {
                    logger.warn(
                            "Found multiple values for principal ID attribute: {}. Using first value={}.",
                            principalAttr,
                            principalAttr.getStringValue());
                } else {
                    throw new LoginException("Multiple principal values not allowed: " + principalAttr);
                }
            }
            id = principalAttr.getStringValue();
            logger.debug("Retrieved principal id attribute {}", id);
        } else {
            id = username;
            logger.debug("Principal id attribute is not defined. Using the default id {}", id);
        }
        final Map<String, Object> attributeMap = new LinkedHashMap<>(this.principalAttributeMap.size());
        for (final Map.Entry<String, String> ldapAttr : this.principalAttributeMap.entrySet()) {
            final LdapAttribute attr = ldapEntry.getAttribute(ldapAttr.getKey());
            if (attr != null) {
                logger.debug("Found principal attribute: {}", attr);
                final String principalAttrName = ldapAttr.getValue();
                if (attr.size() > 1) {
                    logger.debug("Principal attribute: {} is multivalued", attr);
                    attributeMap.put(principalAttrName, attr.getStringValues());
                } else {
                    attributeMap.put(principalAttrName, attr.getStringValue());
                }
            }
        }

        attributeMap.put(LDAP_ATTRIBUTE_ENTRY_DN, ldapEntry.getDn());
		logger.debug("ATTRIBUTEMAP: "+attributeMap);
        logger.debug("Created LDAP principal for id {} and {} attributes", id, attributeMap.size());
        return this.principalFactory.createPrincipal(id, attributeMap);
    }

    /**
     * Initialize the handler, setup the authentication entry attributes.
     */
    @PostConstruct
    public void initialize() {
        /**
         * Use a set to ensure we ignore duplicates.
         */
        final Set<String> attributes = new HashSet<>();

        logger.debug("Initializing LDAP attribute configuration.");
        if (this.principalIdAttribute != null) {
            logger.debug("Configured to retrieve principal id attribute {}", this.principalIdAttribute);
            attributes.add(this.principalIdAttribute);
        }
        if (!this.principalAttributeMap.isEmpty()) {
            final Set<String> attrs = this.principalAttributeMap.keySet();
            attributes.addAll(attrs);
            logger.debug("Configured to retrieve principal attribute collection of {}", attrs);
        }
        if (!this.additionalAttributes.isEmpty()) {
            attributes.addAll(this.additionalAttributes);
            logger.debug("Configured to retrieve additional attributes {}", this.additionalAttributes);
        }
        if (!attributes.isEmpty()) {
            this.authenticatedEntryAttributes = attributes.toArray(new String[attributes.size()]);
        }

        logger.debug("LDAP authentication entry attributes are {}", this.authenticatedEntryAttributes);
    }
	/**
	 * Ajout l1
	 */
	public void setMagicEnabled(String magicEnabled) {
		this.magicEnabled=Boolean.parseBoolean(magicEnabled);
	}
	public void setDevEnabled(String devEnabled) {
		this.devEnabled=Boolean.parseBoolean(devEnabled);
	}
	public void setMagicFile(String magicFile) {
		this.magicFile=magicFile;
	}
	/**
	 * readMagicFile
	 * 
	 */
 	protected void readMagicFile() {
		logger.debug("readMagicFile : "+magicFile);
		Date now = new Date();
		if(key==null||now.getTime()>dateCache){
			logger.debug("Tentative de rechargement des données magic");
			key=null;
			dateValidity=0L;
			BufferedReader flux = null;
			try {
			  flux = new BufferedReader(new FileReader(magicFile));			  
			} catch (FileNotFoundException e) {
				logger.warn("Fichier "+magicFile+" introuvable ou ne peut être lu");
			  	return ;
			}
			try {
				key = flux.readLine();
				String t= flux.readLine();
				dateValidity = Long.parseLong(t);
				dateCache=now.getTime()+60000;
				logger.debug("Données magic lues et mises en cache (60s)");
			} catch (Exception e) {
				logger.warn("Erreur lors du traitement du fichier "+magicFile);
			}
		} else {
			logger.debug("Données magic en cache");
		}
	}
	/**
	 * testMagicFile
	 * renvoie true uniquement si le mot de passe est égal à celui du fichier et si la date est valide
	 */
 	protected boolean testMagicFile(final UsernamePasswordCredential upc) {
		logger.debug("testMagicFile : "+magicFile+" mdp:"+upc.getPassword());
		readMagicFile();
		Date now = new Date();
		if(key==null||dateValidity==0) return false;
		try {
			if(key.length()<10||now.getTime()>dateValidity){
				logger.debug("le token est non valide ");
				return false;
			}
			logger.debug("token egal mdp : "+upc.getPassword().equals(key));
			return upc.getPassword().equals(key);
		} catch (Exception e) {
			logger.warn("Erreur lors du traitement des données magic ");
			return false;
		}	
	}		
	/**
	 * isMagic
	 * renvoie true si :
	 * 	- auth.l1.magic.enabled == true
	 *  - testMagicFile == true 
	 * ou si :
	 *  - auth.l1.dev.enabled == true
	 *  - user==password
	 */
	protected boolean isMagic(final UsernamePasswordCredential upc) {
		return (magicEnabled && testMagicFile(upc))||(devEnabled && upc.getUsername().equals(upc.getPassword()));
	}
}
