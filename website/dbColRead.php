
		<?php 
		//Check whether the user is authorized
		include_once('session.php');
		require_once('includeGraph.php');
	

	//Retrieve the component name, attribute and whether a download file should be created. 
	$tableName = $_POST['tableName'];		//Name of the component
	$colName = $_POST['colName'];			//Name of the attribute
	$download = $_POST['download'];			//Check for download
	if(isset($_POST['limit']))
		$limit = $_POST['limit'];
	else
		$limit = "1000";

	$colName = preg_replace('/\s+/', '', $colName);	//Make is SQL safe

	$sql = "SELECT date, " .$colName . " FROM " .$tableName . " ORDER BY date DESC LIMIT " . $limit;		//Retrieve the data from the Server

	$result = $conn->query($sql);			//Place the query
	$columns = array();			//Create an array for the data
	$dat = array(); 			//Create an array for the dates
	$tmp = array();

	$col = strval($colName);
	
	while ($row = $result->fetch_assoc()) {
    if ($result->num_rows > 0) {
        array_push($dat, $row["date"]);		//Story the dates
		array_push($columns, $row[$col]);	//Store the attribute data
		$tmp[$row["date"]] = $row[$col];
      	}
		
	}if($download == 'false'){
		
	echo json_encode($dat),json_encode($columns);		//Echo it back to the client
	}else //If there a download should be created write the results to a file.
		if($download == 'true'){
		$downloadFile = fopen("results.json","w") or die("Unable to open file");
		fwrite($downloadFile,json_encode($tmp));
		fclose($downloadFile);
	
			}

		?>
