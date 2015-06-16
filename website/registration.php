<html>
<head>	<title>Registration</title>
	
	 <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
	<script>
	
		
	function testFunc(){
		document.getElementById('test').innerHTML = "Its working";
		return false;
	}
		
	function restrict(elem){
	var tf = document.getElementById(elem);
	var test = tf.value;
	var rx = new RegExp;
	if(elem == "email" || elem == "pass1" || elem == "pass2"){
		rx = /[' "]/gi;
	} else if(elem == "username"){
		rx = /[^a-z0-9]/gi;
		} else {}
	tf.value = tf.value.replace(rx, "");
		if(tf.value != test){document.getElementById('status').innerHTML = "Please don't use any quotes."}
		//document.getElementById('test').innerHTML = tf.value;
	}
		
	//////The following two function contain a bug, and because of this the registration page does not work. The following funcitons are never called. 

		
	function signUp(){
			
	//var x= 	emailCheck(); 
	//	document.getElementById('extra').innerHTML = x + " type of result :   " + typeof(x);
		if(true){
			var e = document.getElementById('email').value;
		//document.getElementById('extra').innerHTML = e + "type of e :   " + typeof(e);
			var p1 = document.getElementById('pass1').value;
			var p2 = document.getElementById('pass2').value;
			if(e == "" || p1== "" || p2 == ""){
				document.getElementById('status').innerHTML	= "Please fill out all the fields";
			} else if(p1 != p2){
				document.getElementById('status').innerHTML	= "The password fields do not match. ";
			} else {
					var xmlhttp;
				if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
				  xmlhttp=new XMLHttpRequest();
				  }
				else  {// code for IE6, IE5
				  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				  }
				xmlhttp.onreadystatechange=function(){
				  if (xmlhttp.readyState==4 && xmlhttp.status==200)
					{
						var tmp = xmlhttp.responseText;
						
						if(tmp.search("true") > 0){
							document.getElementById('myForm').innerHTML = "Succesfully registered a new email address";
						}	else { 
							document.getElementById("status").innerHTML= "Registration failed: " + tmp + " type:" + typeof(tmp)+ " Length: "  + tmp.length;
						}
					}
				  }
				xmlhttp.open("POST","register.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("e="+e+"&p1="+p1+"&p2=" + p2);
			}
			
		} else {
			document.getElementById('status').innerHTML	= "The email already registered. Please choose a different email address.";
		}
	}
		
		function emailCheck(){
			var result= false;
		var e = document.getElementById('email').value;
		if(e != ""){
			var xmlhttp;
			if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
			  }
			else {// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  }
			xmlhttp.onreadystatechange=function() {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200){
				  var tmp = xmlhttp.responseText.toString;
				 // tmp = "fail";
				 // tmp ="succeed";
					if(tmp == "false"){
						document.getElementById("signUp").innerHTML="Failed emailCheck";
						result = false;
					} if(tmp == true){
						document.getElementById("signUp").innerHTML="Succeeded emailCheck";
						result =  true;
					} else {
							document.getElementById("signUp").innerHTML="else of emailCheck. respons text = " + tmp + " Type of tmp: " + typeof(tmp);
							result = false;
						}
				  return result;
				}
			  } 
			
		} else{
			document.getElementById('extra').innerHTML = "Please fill out the email field.";
				  result =  false;
			}
		
			xmlhttp.open("POST","emailCheck.php",true);
			xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xmlhttp.send("e="+e);
		}
		
		
	</script>
</head>
	<body>
				<!--Adds the NAV bar --> 
	<script src="globalLayout.js"></script>
	
		
		<div class="container" id="myForm">
		<form role="form" name="signupform" id="signupform">
			<div class="form-group">
				<label for="email">E-mail address: </label>
				<input type="email" class="form-control" id="email" onkeyup="restrict('email')"> 
			</div>
			<div class="form-group">
				<label for="pass1">Password: </label>
				<input type="password" class="form-control" id="pass1" maxlength="16" onkeyup="restrict('pass1')">
			</div>
			<div class="form-group">
				<label for="pass2">Retype password: </label>
				<input type="password" class="form-control" id="pass2" maxlength="16" onkeyup="restrict('pass2')">
			</div>
			
		</form>
				<button class="btn btn-default" onclick="signUp()" >Submit</button>
			
			<div id="test"></div>
			<div id="status" class="text-warning"></div>
			<div id="signUp"></div>
			
			<div id='extra'></div>
		</div>
		
		
		
	</body>
</html>