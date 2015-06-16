<!DOCTYPE html>
<html><head><title></title></head>
	
	<body>
		<?php 
		
		$wname = strval($_POST['wname']);
		$wwname = $_POST['wname'];
		
		$servername = "localhost";
	$username = "anna";
	$password = "karenina";
	$dbname = "detail";

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
	
	$sql = "SELECT * FROM " . $wwname;

	$result = $conn->query($sql);
	$columns = array();
	
	while ($row = $result->fetch_assoc()) {
    if (empty($columns)) {
        $columns = array_keys($row);
      	}
   	}
	echo json_encode($columns);
	// echo $sql;
		?>
	</body>
</html>