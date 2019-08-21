<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%!String orig = null;%>
<%!String enc = null;%>
<%!String dec = null;%>
<%!String logo = null;%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	orig = (String) session.getAttribute("orig");
	 enc = (String) session.getAttribute("enc");
	 dec = (String) session.getAttribute("dec");
	 logo = (String) session.getAttribute("logo");
	/*orig = "/LogoEncoder/images/test_orig.jpg";
	enc = "/LogoEncoder/images/test_orig_encoded.jpg";
	dec = "/LogoEncoder/images/test_orig_decoded.jpg";
	logo = "/LogoEncoder/images/test_logo.jpg";*/
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Your results</title>
</head>
<body bgcolor="black">
	
	<h2><font color="white">Your Original Image</font></h2>
	<img src="<%=orig%>" alt="Your original image" width="85%">
	<h2><font color="white">Your Encoded Image</font></h2>
	<img src="<%=enc%>" alt="Your encoded image" width="85%">
	<h2><font color="white">Your Decoded Image</font></h2>
	<img src="<%=dec%>" alt="Your decoded image" width="85%">
	<h2><font color="white">Your Logo</font></h2>
	<img src="<%=logo%>" alt="Your logo" width="85%">
</body>
</html>