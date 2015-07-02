<?php 
	session_start(); 

	include_once('includeUser.php');

	#Check if user did not input any quotes.
	if(mysqli_real_escape_string($conn, $_POST['p']) != $_POST['p'] || mysqli_real_escape_string($conn, $_POST['e']) != $_POST['e']){
		echo "Please don't use any quotes.";
		exit();
	}
	
	#Retrieve email.	
	$e = mysqli_real_escape_string($conn, $_POST['e']);
	#Retrieve password.
	$p = mysqli_real_escape_string($conn, $_POST['p']);

	#Actual password and input password don't match.
	$match = false;
	#Retrieve email1 and password from users.
	$sql = "SELECT pass1 FROM users WHERE email = '" .$e ."'";
	#Execute query
	$result = $conn->query($sql);

	#Check if there is any output.
	if ($result->num_rows > 0) {
		#Iterate over rows
   		while($row = $result->fetch_assoc()) {
			#Check whether entered password is equal to stored password after hashing.
			if(password_verify($p, $row["pass1"])){
				$p = $row["pass1"];
				$match = true;
				break;
			}
  		}
	}else {
  		echo "0 results";
	}

	$conn->close();

	if($match){	//exists
		$_SESSION['email']  = $e;
		$_SESSION['pass1'] = $p;
		//header("location: dbaccess.php");
		echo true;
		exit();
	} else{
		echo "Failed to login";
	}
		?>
