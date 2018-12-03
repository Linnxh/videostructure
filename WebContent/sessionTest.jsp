<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="application/json; charset=UTF-8">
<title>session client</title>
<script type="text/javascript" src="jquery-3.3.1.min.js"></script>  
<script type="text/javascript" language="javascript">
	
	window.onload = function(e){
		var obj = new Object() ;
		obj.name = "ming";
		$.ajax({ 
    		type: "POST", 
    		url: "/videostructure/session/test", 
    		dataType: "json", 
    		data: {}, 
    		success: function (result) { 
    			alert(1);
    		}
		});
	}
	
	function testSession(){
		var uuid = '<%=session.getAttribute("uuid")%>' ;
  		if( uuid == "null" || uuid == "" ){
  			alert("没有登录信息！");
  		}else{
   			alert("有登录信息,登录uuid:"+uuid);
  		}
	}
	
	/*
	function login(){
		var params = {name:"ming",pwd:"123456"};
    	
    	$.ajax({ 
    		type: "POST", 
    		url: "/videostructure/session/test", 
    		dataType: "application/json", 
    		data: JSON.stringify({ "InviteBidInfo":"123"}), 
    		success: function (result) { 
    			alert(1);
    		}
		});
			
                
	}
	*/
	function setSession(){
		var min = $("minute").val();
		var params = {"minute":min};
		$.ajax({
			type: "post",
        	dataType: "json",
        	url: "http://192.168.3.193:8000/videostructure/session/test",
        	data: params,
        	success: function (data) {
            	if (data != "") {
            		alert(data);
	            }
    	    }
    	});
	}
</script>
</head>
<body>
 	<button id="loginBtn" >login</button>
    <input id="minute" type="text">
    <button onclick="setSession()">setSession</button>
    <button onclick="testSession()">testSession</button>
    <div id="content"></div>
</body>
</html>
