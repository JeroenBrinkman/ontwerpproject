<!DOCTYPE html>
<html><head><title></title></head>
	
	<body>
		<?php 
		
		
		
	$servername = "localhost";
	$username = "anna";
	$password = "karenina";
	$dbname = "users";

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 

	$e = mysqli_real_escape_string($conn, $_POST['e']);
	$p1 = $_POST['p1'];
	$p2 = $_POST['p2'];

	$match = false;
	$sql = "SELECT email FROM users";

	$result = $conn->query($sql);
	if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		if($row["email"]==$e){
			$match = true;
			break;
		}
    }
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
} else  if (strlen($p1) < 3 || strlen($p2) > 16) {
	echo "The passwords must be between 3 and sixteen characters.";
	exit();
} else{
	//$cryptpass = crypt($p1);
	$sql = "INSERT INTO users (email, pass1) VALUES('$e', '$p1')";
	if ($conn->query($sql) === TRUE) {
    echo "true";
	} else {
		echo "Error: " . $sql . "<br>" . $conn->error;
	}
}
		?>
	</body>
</html>