<%@ page import="java.util.Date" %>
<%@ page import="java.net.InetAddress" %>
<%@ page import="java.net.URL" %>
<%! static String hostname="";
static boolean ok=false;
static String erreur="idp is up";
%>

<%--! 
static{
	try {
		final InetAddress addr = InetAddress.getLocalHost();
		hostname = new String(addr.getHostName());
		URL url = new URL("http://idp.univ-lille1.fr/idp/HealthCheck.jsp");
		url.openStream();
		ok=true;
	} catch (Throwable e) {
		erreur = e.toString();
		ok = false;
	}
}
%>
<%
if(ok) {
--%>
<html>
<body>
Health Check <%= hostname %> [<%--= erreur --%>]</BR>
Date du jour : 
<%= new Date() %>
</body>
</html>
<%--
} else {
response.sendError(500);
}
--%>