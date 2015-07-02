<?php
	include_once('session.php');

	#Connect to database
	include_once('includeUser.php');

	#Retrieve email1, email2, password1 and password 2 from user.
	#These are escaped to prevent SQL injections.
	$e = mysqli_real_escape_string($conn, $_POST['e']). "@". mysqli_real_escape_string($conn, $_POST['e2']);
if($_POST['e4'] != 'undefined'){
	$e2 = mysqli_real_escape_string($conn, $_POST['e3']) . "@" . mysqli_real_escape_string($conn, $_POST['e4']);
} else {
	$e2 = "";
}
	$p1 = mysqli_real_escape_string($conn, $_POST['p1']);
	$p2 = mysqli_real_escape_string($conn, $_POST['p2']);

	#Check if user input quotes.
	/*if($e != $_POST['e'] || $e2 != $_POST['e2'] || $p1 != $_POST['p1'] ||$p2 != $_POST['p2']){
		echo "Please don't use any quotes: ";
		exit();
	}*/

	#Currently no match of email.
	$match = false;
	#Check email.
	$sql = "SELECT email FROM users";

	#Execute query.
	$result = $conn->query($sql);
	#Check if not empty.
	if ($result->num_rows > 0) {
		#Retrieve data from each row.
		while($row = $result->fetch_assoc()) {
			#If database contains email.
			if($row["email"] === $e){
				#Match is true.
				$match = true;
				break;
			}
		}
	#Correct since email 
	} else {
		echo "0 results";
	}

	if($match){	//Email is already in the database.
		echo"Email is already in the database.";
		exit();
	} else if($p1 != $p2){
		echo "Password do no match. Also you bypassed the javascript code. Please don't do this anymore. ";
		exit();
	} else if($p1 == "" || $p2 == "" || $e == ""){
		echo "There are empty fields. Also you bypassed the javascript code. Please don't do this anymore.";
	} else  if (strlen($p1) < 3 || strlen($p2) > 88) {
		echo "The passwords must be at least 3 characters and less than 88 characters.";
		exit();
	} else{
		$p1 = password_hash($p1, PASSWORD_DEFAULT);
		$sql = "INSERT INTO users (email, email2, pass1) VALUES('$e', '$e2', '$p1')";
		if ($conn->query($sql) === true) {
	    		echo "true";
		} else {
			echo "Error: " . $sql . "<br>" . $conn->error;
		}
	}
?>
