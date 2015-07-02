<?php 
	include_once('session.php');

?>

<html>
<head>	<title>Registration</title>
	<link rel="icon" 
      type="image/ico" 
      href="favicon.ico">
	
	 <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="custom.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
	<script>
	
		//Debuggin function. 
	function testFunc(){
		document.getElementById('test').innerHTML = "Its working";
		return false;
	}
		
		//This function is not used anymore. This function restricts users from entering certain characters in the input fields. 
	function restrict(elem){
	var tf = document.getElementById(elem);
	var test = tf.value;
	var rx = new RegExp;
	if(elem == "email" || elem == "pass1" || elem == "pass2"  || elem =="email2"){
		rx = /[' "]/gi;
	} else if(elem == "username"){
		rx = /[^a-z0-9]/gi;
		} else {}
	tf.value = tf.value.replace(rx, "");
		if(tf.value != test){document.getElementById('status').innerHTML = "Please don't use any quotes."}
		//document.getElementById('test').innerHTML = tf.value;
	}


		/*
			When the user clicks signup, this function quickly checks whether all fields have been filled in and whether the passwords match.
			It then sends the information to the server. The server will either sucessfully add the new user, or give an error if something goes wrong.
			The server will again check whether the passwords match and remove and malicious code. 
		*/
	function signUp(){
			
		if(true){
			var e = document.getElementById('email').value.split('@');

			var p1 = document.getElementById('pass1').value;
			var p2 = document.getElementById('pass2').value;
			var e2 = document.getElementById('email2').value.split('@');
			if(e[0] == "" || p1== "" || p2 == "" || e[1] == ""){
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
					document.getElementById('status').innerHTML = "Status: " + xmlhttp.responseText;
					{
						var tmp = xmlhttp.responseText;

						if(tmp === "true"){
							document.getElementById('status').innerHTML = "Succesfully registered a new email address";
							document.getElementById('email').value = '';
							document.getElementById('email2').value = '';
							document.getElementById('pass1').value = '';
							document.getElementById('pass2').value = '';
						}	else { 
						}
					}
				  }
				xmlhttp.open("POST","register.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("e="+e[0]+"&p1="+p1+"&p2=" + p2 + "&e2=" + e[1] + "&e3=" + e2[0] + "&e4=" + e2[1]);
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
	
		
<div class="container-fluid" id="myForm">
    <section class="container">
		<div class="container-page">				
			<div class="col-md-12">
				<h3 class="dark-grey">Registration</h3>
			<form class=\"form-inline\" onsubmit="return false;">	
				<div class="form-group col-lg-12">
					<label for="email">E-mail address: </label>
					<input type="email" class="form-control" id="email">  <!-- onkeyup="restrict('email')" -->
				</div>
				
				<div class="form-group col-lg-12">
					<label for="email2">Secondary E-mail address: </label>
					<input type="email" class="form-control" id="email2" >
				</div>
				
				<div class="form-group col-lg-6">
					<label for="pass1">Password: </label>
					<input type="password" class="form-control" id="pass1" maxlength="88" ><!--onkeyup="restrict('pass1')" -->
				</div>
								
				<div class="form-group col-lg-6">
					<label for="pass2">Retype password: </label>
					<input type="password" class="form-control" id="pass2" maxlength="88" ><!--onkeyup="restrict('pass2')" -->
				</div>
				<div class="form-group col-lg-6">
				<button type ="submit" class="btn btn-default" onclick="signUp()" >Register</button>
				</div>
				
			</form>
			</div>
		
	
		</div>
	</section>
</div>
		<div id="test"></div>
			<div id="status" class="text-warning"></div>
			<div id="signUp"></div>
			
			<div id='extra'></div>
	</body>
</html>
