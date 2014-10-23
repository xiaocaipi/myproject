<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="3rd/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<!-- container样式就是固定在一d个大小内适应不同分辨率 -->
	<div class="container">
	<div class="page-header">
		<h1>weibo demo	</h1>
<!-- 		form -inline 在一行里面显示 -->
		<a href="index.do">回到首页</a>
		
		<s:if test="errmsg.length()>0">
		<div class="alert alert-error">
			<s:property value="errmsg"/>
		</div>
		</s:if>
	</div>
	</div>
</body>
</html>