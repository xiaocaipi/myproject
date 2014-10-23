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
<body style="background-image: url(img/bg.png); background-repeat: no-repeat; background-color: #C0DEED">
<!-- container样式就是固定在一d个大小内适应不同分辨率 -->
	<div class="container">
	<div class="page-header">
		<h1>weibo demo	</h1>
<!-- 		form -inline 在一行里面显示 -->
		<form class="form-inline" action="login.do">
			<s:if test="login_name.length()>0">
			<button type="button" class="btn btn-primary disabled" disabled="disabled">
				当前用户：
				<s:property value="login_name"/>
			</button>
<!-- 			注销用户使用的是login这个action里面的logout的方法 -->
			<a href="logout.do">注销用户</a>
			</s:if>
			<s:else>
			<input type="text" name="username" class="input-small" placeholder="用户名">
			<input type="password" name="password" class="input-small" placeholder="密码">
			<button type="submit" class="btn">登入</button>
			<a href="register.do">注册新用户</a>
			</s:else>
			
		</form>
	</div>
<!-- 	样式分成左右2块 -->
	<div class="container-fluid"
	 style="background:url(img/wash-white-30.png) ;padding-top:20px">
	<div class="row-fluid">
		<div class="span4">
		<!-- 	自定义 的css -->
			<div class="well wbwell" >
				<form action="post.do" style="margin: 0px">
					<textarea rows="4" name="content" style="width: 95%; overflow: hidden;">
					</textarea>
					<button type="submit" class="btn">发送微薄</button>
				</form>
			</div>
			<div class="well wbwell">
			<p>已关注的用户</p>
			<table class="table">
			 <tbody>
			 	<s:iterator value="follow" id="name">
			 		<tr>
			 			<td><s:property value="name"/></td>
			 			<td><a href="unfollow.do?username=<s:property value="name"/>">取消关注</a></td>
			 		</tr>
			 	</s:iterator>
			 </tbody>
			</table>
			</div>
			
		<div class="well wbwell">
			<p>未关注的用户</p>
			<table class="table">
			 <tbody>
			 	<s:iterator value="unfollow" id="name">
			 		<tr>
			 			<td><s:property value="name"/></td>
			 			<td><a href="follow.do?username=<s:property value="name"/>">关注</a></td>
			 		</tr>
			 	</s:iterator>
			 </tbody>
			</table>
			</div>
		</div>
		<div class="span8">
			<div class="well wbwell">
				<table class="table">
					<thead>
						<tr>
							<th>发布人</th>
							<th>内容</th>
							<th>发布时间</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="posts" id="posts">
						<tr>
							<td><s:property value="username"/></td>
							<td><s:property value="content"/></td>
							<td><s:property value="ts"/></td>
						</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	</div>
	</div>
</body>
</html>