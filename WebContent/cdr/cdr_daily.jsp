<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="3rd/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="js/jquery.form.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

</head>
<body style="background-image: url(img/bg.png); background-repeat: no-repeat; background-color: #C0DEED">
	<div class="container">
	<div class="page-header">
		<h1>Cdr Daily DEmo	</h1>
	</div>
	<div id="search_res" class="well wbwell"></div>
	<table class="table">
		<thead>
			<tr>
				<th>日期</th>
				<th>条数</th>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="dailyreport" id="cdr">
			  <tr>
			  	<td><s:property value="key"/></td>
			  	<td><s:property value="value"/></td>
			  </tr>
			</s:iterator>
		</tbody>
	</table>
	</div>
</body>
</html>