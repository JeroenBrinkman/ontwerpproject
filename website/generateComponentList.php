<!DOCTYPE html>
<html>
<head>

</head>
<body>

<?php
						$count=$_GET["count"];
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
								mysql_connect($servername, $username, $password);
								$res = mysql_query("SHOW TABLES FROM $dbname");
								$tables = array();

								while($row = mysql_fetch_array($res, MYSQL_NUM)) {
									array_push($tables,"$row[0]");
								}
							$conn->close();

echo "	<div class= \"col-md-6\" id=\"graph{$count}\" style=\"background-color:lavenderblush;\">
  		<div class=\"well well-lg\">" ;
echo "<button class=\"btn btn-danger pull-right\" onclick=\"removeGraph({$count})\">Close</button>";
	
//Generate nav tabs
echo "<ul class = \"nav nav-tabs\">
	<li class= \"active\"><a href=\"#chart_div{$count}\" data-toggle=\"tab\">Graph</a></li>
	<li><a href=\"#table_div{$count}\" data-toggle=\"tab\">Table</a></li>
	<li><a href=\"#download_div{$count}\" data-toggle=\"tab\">Export</a></li>
</ul>" ; 

							echo "<select id= \"selectBox{$count}\" onchange=\"primaryMenu({$count});\">" ;
							echo "<option value='empty'></option>";
								for($i = 0; $i < count($tables); $i++){
										echo "<option value=\"$tables[$i]\">Worker $i - $tables[$i] </option>";
								}
							echo "</select>";
				echo "<div id=\"secondMenu{$count}\">Second menu</div>
				<div id=\"thirdMenu{$count}\">Third Menu </div>
			<div class=\"tab-content\">	
				<div class=\"tab-pane fade active in\" id=\"chart_div{$count}\" style=\"width:400; height:300\"></div> 
				<div class=\"tab-pane fade\" id=\"table_div{$count}\" style=\"width:400; height:300; overflow-x:hidden;overflow-y: scroll; \">Table data will be displayed on this side of the column</div>
				<div class=\"tab-pane fade\" id=\"download_div{$count}\"><button class=\"btn btn-primary\" onclick=\"downloadGraph({$count})\">Generate JSON File</button><div id=\"downloadButton{$count}\"></div></div>
			</div>	
				<div id=\"extra{$count}\"> </div> " ; 

		echo "</div> </div>";
	//	</div>" ; 

	// echo "<div class= \"col-md-6\" style=\"background-color:lavenderblush;\">
	//echo "";
		
	//	</div>
//	</div>" ; 
	
	?>
</body>
</html>