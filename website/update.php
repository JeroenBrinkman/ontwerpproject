		<?php 
		include_once('session.php');
/*
	This script updates user information upon receiving the data through a HTTP POST Request.
*/
if(isset($_POST['fields'])){
	//Retrieves the emails of the user. The emails are then used in the email fields of the 'my account' page. 
	$email = $_SESSION['email'];

	include_once('includeUser.php');
	
	$sql = "SELECT email, email2 FROM users";
	
	$result = $conn->query($sql);
	if ($result->num_rows > 0) {
    // output data of each row
		while($row = $result->fetch_assoc()) {
			if($row['email'] == $email){
				$e1 = $row["email"];
				$e2 = $row["email2"];
				break;
			}
			
		}
	} else {
		echo "0 results";
	}
	
	$result = array();
	array_push($result,$e1);
	array_push($result,$e2);
	echo json_encode($result);
	exit();
}else {
	
	
	
	$email = $_SESSION['email'];	
	include_once('includeUser.php');

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
	
	//Inserts the user information into the database.
	
	if($p1 == ""){
		$sql = "UPDATE users SET email='$e1', email2='$e2' WHERE email='$email'";
		
	}else{
		$p1 = password_hash($p1, PASSWORD_DEFAULT);
		$sql = "UPDATE users SET email='$e1', pass1='$p1', email2='$e2' WHERE email='$email'";	
	}
	
	if ($conn->query($sql) === TRUE) {
		$_SESSION['email'] = $e1;
		$_SESSION['pass1'] = $p1;
    echo true;
	} else {
		echo "Error: " . $sql . "<br>" . $conn->error;
	}
}
}	?>
