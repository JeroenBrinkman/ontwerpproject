<?php
	include_once('session.php');

	require_once('includeGraph.php');
	
	#Retrieve data to construct sql query.
	#Name of table to retrieve.
	$tableName = mysqli_real_escape_string($conn, $_POST['tableName']);
	#Columns to retrieve, date is always retrieved.
	$columns = "date,".mysqli_real_escape_string($conn, $_POST['getColumns']);

	#End start from which to retrieve (in milliseconds since epoch). 
	$firstDate = mysqli_real_escape_string($conn, $_POST['startDate']);
	#End date from which to retrieve (in milliseconds since epoch). 
	$secondDate = mysqli_real_escape_string($conn, $_POST['endDate']);

	#Create sql query.
	$sql = "SELECT " .$columns ." FROM " .$tableName;

	if(is_numeric($firstDate) && is_numeric($secondDate)){
		$sql = "SELECT " .$columns ." FROM " .$tableName ." WHERE date >= " .$firstDate ." AND date <= " .$secondDate;
	}

	#Execute query.
	$result = $conn->query($sql);

	#Create array of column names.
	$fields = explode(",", $columns);
	#Size of fields.
	$numberOfFields = count($fields);
	#Create data array.
	$dataArray = array();

	#Loop over all retrieved database entries.
	while($row = $result->fetch_assoc()) {
		#Check whether the row is not empty.
		if($result->num_rows > 0) {
			#Create temporary array.
			$tmpArray = array();
			#Set all values of row entry except for the date entry.
			for($index = 1; $index < $numberOfFields; $index++) {
				$tmpArray[$fields[$index]] = $row[$fields[$index]];			
			}
			#Add $date => $values to dataArray.
			$dataArray[date("Y-m-d H:i:s", substr($row[$fields[0]], 0, -3))] = $tmpArray;
		}
	}

	#Create return array.
	$returnArray = array();
	#Add $componentName => $dataArray to return array.
	$returnArray[$tableName] = $dataArray;

	#Create download file results.json.
	$downloadFile = fopen("results.json","w") or die("Unable to open file");
	#Write to downloadFile.
	fwrite($downloadFile,json_encode($returnArray));
	#Close downloadFile.
	fclose($downloadFile);
	#Echo true.
	echo true;
	#Download if download is set to true.
?>
