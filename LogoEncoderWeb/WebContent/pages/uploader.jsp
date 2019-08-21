<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Logo Encoder Upload Page</title>
</head>
<body>
<body onload="uploadForm.reset()">
	<form method="post" enctype="multipart/form-data" action="upload.html" name="uploadForm">
		
		<h3>Please choose the file that you want encoded or decoded.</h3>
		<input type="file" name="origFile" readonly="readonly"/><br>
		<h3>If you are encoding please also submit a black</h3>
		<h3>and white logo file of the same size as the original.</h3>
		<input type="file" name="logoFile" readonly="readonly"/><br>
		<h3>Please enter a name for the files to share:</h3>
		<input type="text" name="name" /><br> <input type="radio"
			name="encode" value="true">Encode<br> <input
			type="radio" name="encode" value="false">Decode
		<input type="submit" value="Upload Files"/>
		
	</form>
</body>
</html>