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
<script type="text/javascript">
	$(document).ready(function(){
		$("#btn_search").click(function(){
			//ajax对form进行提交返回一个结果在resp里面 
			$("#form_search").ajaxSubmit(function(resp){
				$("#search_res").html(resp);
			});
			//这个form是个submit提交，action返回的不是json，就是普通的返回，是一个search的页面，这边就能把返回的页面放在div里面
			//return false 能使页面不跳转
			//达到普通的提交完成一个ajax的功能
			return false;
		});
		
	});
</script>
</head>
<body style="background-image: url(img/bg.png); background-repeat: no-repeat; background-color: #C0DEED">
<!-- container样式就是固定在一d个大小内适应不同分辨率 -->
	<div class="container">
	<div class="page-header">
		<h1>Cdr DEmo	</h1>
	</div>
	<form action="search.do" id="form_search" class="form-inline">
		<input name="addr" type="text"  class="input"/>
		<button id="btn_search" type="submit" class="btn">提交</button>
	</form>
	<div id="search_res" class="well wbwell"></div>
	</div>
</body>
</html>