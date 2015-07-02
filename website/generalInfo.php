<?php
include_once('session.php');
require_once('includeGraph.php');

	/*
		When the user selects a component on the GI page, this script will select the latest entry and echo back an array containing the data. 
	*/
					if(!isset($_POST['tname'])){
								mysql_connect($servername, $username, $password);
								$res = mysql_query("SHOW TABLES FROM $dbname");
								$tables = array();

								while($row = mysql_fetch_array($res, MYSQL_NUM)) {
									array_push($tables,"$row[0]");
								}
							$conn->close();

							echo "<select id= \"generalBox\" onchange=\"generateGeneralInfo();\">" ;		//Generate a dropdown menu containing all the components
							echo "<option value='null'>Please select a component: </option>";
								for($i = 0; $i < count($tables); $i++){
										echo "<option value=\"$tables[$i]\">Component $i - $tables[$i] </option>";
								}
							echo "</select>";
					} elseif(isset($_POST['tname'])){
						
							
						$sql = "SELECT * FROM " . $_POST['tname'] . " ORDER BY date DESC";			//Select the latest entry
			
				
						$result = $conn->query($sql);
						$columns = array();
						$test = array();

						while ($row = $result->fetch_assoc()) {
						if (empty($columns)) {
							$columns = array_keys($row);
							array_push($test, $row);
							}
						}
					//	echo json_encode($columns);
						echo json_encode($test);													//Echo it to the client
						
						
					}

?>
