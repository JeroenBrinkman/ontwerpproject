<?php
	#Start session.
	session_start();

	$match = false;

	#Check if session is valid.
	if(isset($_SESSION["email"]) && isset($_SESSION["pass1"])){
		$match = true;
		#Connect to database.
		#TODO remove hardcoded stuff such as dbname, servername, username, password.
		#TODO Note, conn2 cannot be named conn, this will introduce a bug since conn is already opened somewhere else.
		$dbname = "users";
		$servername = "localhost";
		$username = "henk";
		$password = "henk";
		// Create connection
		$conn2 = new mysqli($servername, $username, $password, $dbname);
		// Check connection
		if ($conn2->connect_error) {
			die("Connection failed: " . $conn->connect_error);
		} 
		#Request email from session.
		$sessionMail = $_SESSION["email"];
		#Request password from session.
		$sessionPass = $_SESSION["pass1"];
		#Create sql query to select password from user.
		$sql = "SELECT pass1 FROM users WHERE email = '" .$sessionMail ."'";

		#Execute sql query.
		$result = $conn2->query($sql);
		#Check if user is in database.
		if ($result->num_rows > 0) {
			#Iterate over rows.
 			while($row = $result->fetch_assoc()) {
				#Check whether entered password is equal to stored password after hashing.
				if($row["pass1"] === $sessionPass){
					$match = true;
					break;
				}
			}
		}
		$conn2->close();
	}

	#If session does not match, redirect to login page.
	if(!$match){
		header("location: login.php");
		exit();
	}
?>
