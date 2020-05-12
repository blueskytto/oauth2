<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.Enumeration"%>

<!DOCTYPE html>
<html>
<head>
<title>MagicSSO - IDP Test</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
#content { width:100%; }
#content table { width:100%; border:1px; }
#content table thead { text-align:center; background:#B3CDEE; }
#content table td { padding:.1em; border-right:1px solid #CCC; border-bottom:1px solid #CCC; }
form table { width:50%; border:1px; }
form table thead { text-align:center; background:#B3CDEE; }
form table td { padding:.1em; border-right:1px solid #CCC; border-bottom:1px solid #CCC; }
</style>
<script type="text/javascript">
	var flag = false;

	function showLayer()
	{
		if (!flag) {
			document.getElementById("sysinfo_lay").style.display = "block";
			document.getElementById("showLayer").value = "환경 정보 감추기";
		} else {
			document.getElementById("sysinfo_lay").style.display = "none";
			document.getElementById("showLayer").value = "환경 정보 보기";
		}
		flag = !flag;
	}
</script>
</head>
<body onload="">
	<div id="content">
		<div style="position:absolute; top:73px; right:10px;">
		</div>
		<table>
			<thead>
				<tr>
					<td width="20%">Header Variable</td>
					<td>Header Value</td>
				</tr>
			</thead>
			<tbody>
				<%
					Enumeration<?> eh = request.getHeaderNames();
					while (eh.hasMoreElements()) {
						String skey = (String) eh.nextElement();
						out.println("<tr>");
						out.println("<td>" + skey + "</td>");
						out.println("<td>" + request.getHeader(skey) + "</td>");
						out.println("</tr>");
					}
				%>
			</tbody>
		</table>
		<br>
		<table>
			<thead>
				<tr>
					<td width="20%">Session Variable</td>
					<td>Session Value</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>IDP SessionID</td><td><%=session.getId()%></td>
				</tr>
				<%
					Enumeration<?> em = session.getAttributeNames();
					while (em.hasMoreElements()) {
						String skey = (String) em.nextElement();
						out.println("<tr>");
						out.println("<td>" + skey + "</td>");
						if (skey.equals("AuthnRequest"))
							out.println("<td>" + URLEncoder.encode(((String) session.getAttribute(skey)).substring(0,60), "UTF-8") + "</td>");
						else
							out.println("<td>" + session.getAttribute(skey) + "</td>");
						out.println("</tr>");
					}
				%>
			</tbody>
		</table>
		<br>
		<!-- Sys info-->
		<input type="button" id="showLayer" name="showLayer" value="환경 정보 보기" style="width:150px; margin-left:2px;" onclick="showLayer();return false;"/>
		<div id="sysinfo_lay" style="display: none;">
			<table>
				<thead>
					<tr>
						<td width="20%">Sys pName</td>
						<td>Value</td>
					</tr>
				</thead>
				<tbody>
					<%
						Enumeration<?> es = System.getProperties().propertyNames();
						while (es.hasMoreElements()) {
							String skey = (String) es.nextElement();
							out.println("<tr>");
							out.println("<td>" + skey + "</td>");
							out.println("<td>" + System.getProperty(skey) + "</td>");
							out.println("</tr>");
						}
					%>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
