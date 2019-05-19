<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body bgcolor="#F4F4F4">
	<div style="padding:200px 100px 10px 100px">
	    <form id="form1" name="form1" method="get" action="/user/login.do">
	        <p align="center">用户登录</p>  
	        <table width="296" align="center">  
	            <tr>  
	                <td width="98" height="34" align="right">用户名：</td>  
	                <td width="182">
	                	<label>
	                		<input name="username" type="text" id="username" />
	                	</label>
	                </td>  
	            </tr>  
	  
	            <tr>  
	                <td height="36"  align="right">密码：</td>  
	                <td>
	                	<label>
	                		<input name="password" type="password" id="password" />
	                	</label>
	                </td>  
	            </tr>  
	            <tr>  
	                <td height="35" colspan="2" align="center">
	                	<label>        
	                        <input type="submit" name="Submit" value="提交" />  
	                	</label> 
	                	<label>
	                		<input type="reset" name="Submit2" value="重置" />  
	                	</label>
	                </td>  
	            </tr>  
	        </table>  
	    </form>  	
	</div>
</body>
</html>