<!DOCTYPE html>
<html lang="en">

    <head>
		<link rel="icon" 
      type="image/ico" 
      href="favicon.ico">
		
		<!-- Login page-->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>DNS Jedi Login</title>

        <!-- CSS -->
        <link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Roboto:400,100,300,500">
        <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
        <link rel="stylesheet" href="assets/font-awesome/css/font-awesome.min.css">
		<link rel="stylesheet" href="assets/css/form-elements.css">
        <link rel="stylesheet" href="assets/css/style.css">

        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->

        <!-- Favicon and touch icons 
		<link rel="shortcut icon" href="assets/ico/favicon.png">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="assets/ico/apple-touch-icon-144-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="assets/ico/apple-touch-icon-114-precomposed.png">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="assets/ico/apple-touch-icon-72-precomposed.png">
        <link rel="apple-touch-icon-precomposed" href="assets/ico/apple-touch-icon-57-precomposed.png">-->
	<script>
		
		/*This function is not used to anymore. Howver it can be used to restric users from entering certain characters. Malicious characters are now removed on the server. */
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
		
		/*
			When the user clicks log in this function sends the username and password to the server for verification.
		*/
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
						window.location.href= 'index.php';
						var tmp = xmlhttp.responseText;
						if(tmp){
							window.location.href= 'index.php';
						}
						document.getElementById('status').innerHTML = "Respons: " + tmp + " Type of respons: " + typeof(tmp);
					}
				  }
				xmlhttp.open("POST","loginServer.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("e="+e+"&p="+p);	
				
			}
		
		}
	</script>
    </head>

    <body>

        <!-- Top content -->
        <div class="top-content">
        	
            <div class="inner-bg">
                <div class="container">
                    <div class="row">
                        <div class="col-sm-8 col-sm-offset-2 text">
                            <h1><strong>DNS Jedi</strong> Login</h1>
                            <div class="description">
                            	<p>
	                            	Stay up to date with the status of your computer networks
                            	</p>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-6 col-sm-offset-3 form-box">
                        	<div class="form-top">
                        		<div class="form-top-left">
                        			<h3>Please login to continue</h3>
                            		<p>Enter your username and password</p>
                        		</div>
                        		<div class="form-top-right">
                        			<i class="fa fa-key"></i>
                        		</div>
                            </div>
                            <div class="form-bottom">
			                    <form class="login-form" method="post" onsubmit="return false;" action="">
			                    	<div class="form-group">
			                    		<label class="sr-only" for="email">Username</label>
			                        	<input type="text" name="form-username" placeholder="Email..." class="form-username form-control" id="email">
			                        </div>
			                        <div class="form-group">
			                        	<label class="sr-only" for="password">Password</label>
			                        	<input type="password" name="form-password" placeholder="Password..." class="form-password form-control" id="password">
			                        </div>
									<button class="btn" onclick="login()">Sign in!</button>
			                    </form>
								
		                    </div>
                        </div>
                    </div>
                    
                </div>
            </div>
            
        </div>


        <!-- Javascript -->
        <script src="assets/js/jquery-1.11.1.min.js"></script>
        <script src="assets/bootstrap/js/bootstrap.min.js"></script>
        <script src="assets/js/jquery.backstretch.min.js"></script>
        <script src="assets/js/scripts.js"></script>
        
        <!--[if lt IE 10]>
            <script src="assets/js/placeholder.js"></script>
        <![endif]-->
		<div id="status"></div>
    </body>

</html>