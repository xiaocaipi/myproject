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
		<form class="form-horizontal" action="register.do">
			<a href="index.do">回到首页</a>
			<legend>
				注册新用户
			</legend>
			<div class="control-group">
				<label class="control-label">用户名</label>
				<div class="controls">
				<input type="text" name="username">
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label">密码</label>
				<div class="controls">
				<input type="password" name="password">
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label">重复输入</label>
				<div class="controls">
				<input type="password" name="password2">
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
				<button type="submit">注册</button>
				</div>
			</div>
		</form>
		<s:if test="errmsg.length()>0">
		<div class="alert alert-error">
			<s:property value="errmsg"/>
		</div>
		</s:if>
	</div>
	</div>
</body>
</html>