<?php
	session_start();
	if(!isset($_SESSION['email'])){
	  // header("location: login.php");
		exit();
	}
?>
<html><head><title>My Account</title>
		<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
	<script>
	
	function restrict(elem){
			var tf = document.getElementById(elem);
			var test = tf.value;
			var rx = new RegExp;
			if(elem == "email1" || elem == "pass1" || elem == "pass2" || elem == "email2"){
				rx = /[' "]/gi;
			} else if(elem == "username"){
				rx = /[^a-z0-9]/gi;
				} else {}
			tf.value = tf.value.replace(rx, "");
				if(tf.value != test){document.getElementById('status').innerHTML = "Please don't use any quotes."}
			}
	
		function save(){
			var e1 = document.getElementById('email1').value;	
			var e2 = document.getElementById('email2').value;	
			var p1 = document.getElementById('pass1').value;	
			var p2 = document.getElementById('pass2').value;	
			
			if(e1 == ""){
				document.getElementById('status').innerHTML = "Please fill out a primary email address.";
			} else if(p1 != p2){
				document.getElementById('status').innerHTML = "The passwords do no match.";
			} else if(p1.length < 4){
				document.getElementById('status').innerHTML = "The passwords must be at least 4 characters.";	
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
						if(tmp){
							document.getElementById('status').innerHTML = "Sucessfully updated information.";
							
							document.getElementById('pass2').value = "";
							document.getElementById('pass1').value = "";
							
						} else{
							document.getElementById('status').innerHTML = "Failed to update information."; 
							
							document.getElementById('pass2').value = "";
							document.getElementById('pass1').value = "";
						}
					}
				  }
				xmlhttp.open("POST","update.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("e1="+e1+"&e2="+e2+"&p1="+p1+"&p2="+p2);		
			}
		}
	</script>
	</head>
	

<body>
	<script src="globalLayout.js"></script>
	<div class="container">
		<form role="form">
			<div class="form-group"> 
				<label for="email">Primary email: </label>
				<input type="email" class="form-control" id="email1" onkeyup="restrict('email1')">
			</div>
			<div class="form-group">
				<label for="email">Secondary email: </label>
				<input type="email" class="form-control" id="email2" onkeyup="restrict('email2')">
			</div>
			<div class="form-group">
				<label for="Password">Change password: </label>
				<input type="password" class="form-control" id="pass1" onkeyup="restrict('pass1')">
				<label for="Password">Retype password: </label>
				<input type="password" class="form-control" id="pass2" onkeyup="restrict('pass2')">
			</div>
			
		</form>
		<button class="btn btn-warning" onclick="save()">Save changes</button>
		<div id="status"></div>
		<span id="tmp">tmp</span>
	</div>
</body>

</html>