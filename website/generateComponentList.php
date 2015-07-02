<?php
include_once('session.php');

	/*
		This script generates a dropdown menu containing all the components. 
	
	*/
						$count=$_GET["count"];
						include_once('includeGraph.php');
								
								//Connect to the database and retrieve the data
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

							echo "<ul class=\"list-group\"><li class=\"list-group-item\">Please select the desired component: <br><br><select id=\"selectBox{$count}\" onchange=\"primaryMenu({$count});\">" ;
							echo "<option value='empty'>Please select a component: </option>";
								for($i = 0; $i < count($tables); $i++){
										echo "<option value=\"$tables[$i]\">$i - $tables[$i] </option>";
								}
							echo "</select><br><br></li><li class=\"list-group-item\">Please select the number of entries you would like to display: <br><br><input type=\"text\" id=\"limit{$count}\" value=\"1000\"><br><br></li></ul>";
				echo "<div id=\"secondMenu{$count}\"></div>
				<div id=\"thirdMenu{$count}\"></div>
			<div class=\"tab-content\">	
				<div class=\"tab-pane fade active in\" id=\"chart_div{$count}\" style=\"width:700; height:400\"></div>
				<div class=\"tab-pane fade\" id=\"table_div{$count}\">Table data will be displayed on this side of the column</div>
				<div class=\"tab-pane fade\" id=\"download_div{$count}\"><ul class='list-group'><li class='list-group-item'><div id=\"advancedOptions{$count}\"></div></li><li class='list-group-item'><button class=\"btn btn-link\" onclick=\"downloadGraph({$count})\">Generate JSON File</button></li><li class='list-group-item'><div id=\"downloadButton{$count}\">Download button will appear here</div></li></ul></div>
			</div>	
				<div id=\"extra{$count}\"> </div> " ; 

		echo "</div> </div>";
	?>
</body>
</html>
