<html><head><title>Login</title>
	<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script></head>
	<script>
		function restrict(elem){
			var tf = document.getElementById(elem);
			var test = tf.value;
			var rx = new RegExp;
			if(elem == "email" || elem == "password"){
				rx = /[' "]/gi;
			} else if(elem == "username"){
				rx = /[^a-z0-9]/gi;
				} else {}
			tf.value = tf.value.replace(rx, "");
				if(tf.value != test){document.getElementById('status').innerHTML = "Please don't use any quotes."}
			}
		
		function login(){
			var e = document.getElementById('email').value;
			var p = document.getElementById('password').value;
			
			if(p == "" || e == ""){
				document.getElementById('status').innerHTML = "Please fill out all the fields.";
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
						if(tmp)
							window.location.href= 'dbaccess.php';
						document.getElementById('status').innerHTML = "Respons: " + tmp + " Type of respons: " + typeof(tmp);
					}
				  }
				xmlhttp.open("POST","loginServer.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("e="+e+"&p="+p);	
				
			}
		
		}
	</script>
	<body>
		<!--Adds the NAV bar <script src="globalLayout.js"></script>  --> 
		
	<div class="container">
		<h4>Login: </h4> 
		 <form role="form">
			<div class="form-group">
				<label for="email">Email address: </label>
				<input type="email" class="form-control" id="email" onkeyup="restrict('email')">
			</div>
			<div class="form-group">
				<label for="email">Password: </label>
				<input type="password" class="form-control" id="password" onkeyup="restrict('password')">
			</div>
			 <button class="btn btn-default" type="button" onclick="login()">Login</button>
		</form>
		<div id="status"></div>
		</div>
	</body>

</html>