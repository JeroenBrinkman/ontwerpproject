<!DOCTYPE html>
<html><head><title></title></head>
	
	<body>
		<?php 
		
		$e = $_POST['e'];
		
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
	echo false;
} else{
	echo true;
}
		?>
	</body>
</html>