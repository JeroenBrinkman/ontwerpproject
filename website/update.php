		<?php 
		session_start();
if(!isset($_SESSION['email'])){
	echo "Not logged in.";
//	header("location: login.php");
	exit();
}
	$email = $_SESSION['email'];	
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

	//$e = mysqli_real_escape_string($conn, $_POST['e']);
	$p1 = $_POST['p1'];
	$p2 = $_POST['p2'];
	$e1 = $_POST['e1'];
	$e2 = $_POST['e2'];

	$match = false;
	$sql = "SELECT email FROM users";

	$result = $conn->query($sql);
	if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		if($row["email"]==$e1){
			$match = true;
			break;
		}
    }
} else {
    echo "0 results";
}

if($p1 != $p2){
	echo false;
	exit();
} else if($e1 == ""){
	echo false;
} else{
	//$cryptpass = crypt($p1);
	
	if($p1 == ""){
		$sql = "UPDATE users SET email='$e1' WHERE email='$email'";
		
	}else{
		$sql = "UPDATE users SET email='$e1', pass1='$p1' WHERE email='$email'";	
	}
	
	if ($conn->query($sql) === TRUE) {
		$_SESSION['email'] = $e1;
    echo true;
	} else {
		echo "Error: " . $sql . "<br>" . $conn->error;
	}
}
		?>