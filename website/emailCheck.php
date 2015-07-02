	<?php 
		include_once('session.php');
		include_once('includeUser.php');
		$e = $_POST['e'];				//Email as sent by the client
	
	/*Check whether a given email is already in the database*/

	$match = false;
	$sql = "SELECT email FROM users";

	$result = $conn->query($sql);
	if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
		if($row["email"]==$e){			//Is the email already in the databse?
			$match = true;				//Email is in the database
			break;						//Exit, return to the client
		}
    }
} else {
    echo "0 results";				//If there are no entries in the databse
}

if($match){	//Email is already in the database.
	echo false;
} else{
	echo true;
}
		?>
	</body>
</html>
