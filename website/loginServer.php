<?php session_start(); ?>
<!DOCTYPE html>
<html><head><title></title></head>
	
	<body>
		<?php 
		
		$e = $_POST['e'];
		$p = $_POST['p'];
		
		
		
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
	$match = false;
	$sql = "SELECT email,pass1 FROM users";

	$result = $conn->query($sql);
	if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		if($row["email"]==$e && $row["pass1"] == $p){
			$e = $row["email"];
			$p = $row["pass1"];
			$match = true;
			break;
		}
    }
} else {
    echo "0 results";
}

if($match){	//exists
	$_SESSION['email']  = $e;
	//header("location: dbaccess.php");
	echo true;
} else{
	echo "Failed to login";
}
		?>
	</body>
</html>