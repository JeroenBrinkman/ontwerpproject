
		<?php 
		include_once('session.php');
		include_once('includeGraph.php');
		$wname = strval($_POST['wname']);
		$wwname = $_POST['wname'];
		
	/*This PHP script retrieves all the components in the database and echoes it back to the client. */

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);
	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 
	
	$sql = "SELECT * FROM " . $wwname;		//SQL query to retrieve all components

	$result = $conn->query($sql);
	$columns = array();						//An array to store the the component names
	
	while ($row = $result->fetch_assoc()) {
    if (empty($columns)) {
        $columns = array_keys($row);
      	}
   	}
	echo json_encode($columns);				//Echo back the results to the client
		?>
	</body>
</html>
