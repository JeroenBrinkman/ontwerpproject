<?php
session_start();
if(!isset($_SESSION["email"])){
	header("location: login.php");
	exit();
}
?>
<!DOCTYPE html>
<html>
<head>
	<!--Scrollbar for the table pane 
	<style>.tab-pane{
  height:300px;
  overflow-y:scroll;
  width:100%;
}</style>-->
	
	<title>My webpage</title>
	 <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
	<script type="text/javascript">
		var tableNames = [];
		var columnNames = [];
		
		//Count number of graphs loaded
		var graphCounter = 0;
		//Set variables
		var columnName = "My Graph";
		var columnDate = [0];
		var currentTable = "";
		var columnInfo = [0];
		
		function setColumnInfo(datInfo){
			columnInfo.push(datInfo);
		}
		function setColumnName(nameInfo){
			columnName = nameInfo;	
		}
		function setDate(datInfo){
			columnDate.push(datInfo);
		}
		function setTable(tabName){
			currentTable = tabName;
		} 
		
		// Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
		google.load("visualization", "1", {packages:["table"]});

      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(setColumn);

		
		function primaryMenu(count){
			var tmp = "selectBox" + count; 
			var selectBox = document.getElementById(tmp);
    		var str = selectBox.options[selectBox.selectedIndex].value;
			var tableName = selectBox.options[selectBox.selectedIndex].value;
			tableNames[count] = tableName;
			document.getElementById('tmp').innerHTML = tableNames[count]; //TODO: can be deleted
			
			var param = "wname="+str;
		//	param = param.concat(str);
		//	document.getElementById('main').innerHTML = "Upper param test: "+param + "<br>";
				if(str=="empty"){
					document.getElementById('main').innerHTML = "";
				}else{
		//	document.getElementById('main').innerHTML = str + "<br>";
					if(str ==""){
						document.getElementById('main').innerHTML = "Empty stuff";
						return;
					} else{
						 if (window.XMLHttpRequest) {
							// code for IE7+, Firefox, Chrome, Opera, Safari
						var	xmlhttp = new XMLHttpRequest();
						} else {
							// code for IE6, IE5
						var	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
						}
					xmlhttp.onreadystatechange = function() {
            			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
							var resp = xmlhttp.responseText;
							resp.trim();
						//	document.getElementById('main').innerHTML = resp;
							secondMenu(resp, tableName, count);
						}
					}
					
					}
					xmlhttp.open("POST","dbRead.php",true);
					xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
					xmlhttp.send(param);
				}
		}
		
		function secondMenu(resp, tableName, count){
	
			resp = resp.replace("[","");
				resp = resp.replace("]",",");
				resp = resp.replace(/"/g,'');
		
				resp.trim();
				var ar = resp.split(',');
				var tmp = "selectBoxSecondary" + count;
				var output = "<select id = \"" + tmp +  "\" onchange= \"setColumn(" + count + ",false) \" name=\"" + tableName + "\">";
					output = output+ "<option></option>";
			for(var i = 2; i < ar.length - 1; i++){
				ar[i].trim();
				output = output + "<option value=\"" + ar[i] + "\">" + ar[i] + "</option>";
			}
			output = output + "</select>";
			tmp = "secondMenu" + count; 
			document.getElementById(tmp).innerHTML = output; 
			
			return;
		}
		
		
		function setColumn(count, download){
			var tmp = "selectBoxSecondary" + count;
			var selectBox = document.getElementById(tmp);
			var tName = document.getElementById(tmp).name;
				setTable(tName);
    		var colName = selectBox.options[selectBox.selectedIndex].value;
				setColumnName(colName);
			columnNames[count] = colName;
		//	document.getElementById('tmp').innerHTML = columnNames[count]; //TODO: can be deleted
			document.getElementById('tmp').innerHTML = "Type of download: " + download  + typeof(download.toString()); //TODO: can be deleted
			
			var param= "colName=" + columnNames[count] + "&tableName="+tableNames[count]+ "&download=" + download.toString();
			
			var col;
			var dat;
			//TODO
			tmp = "thirdMenu" +count; 
			if(colName=="empty"){
					document.getElementById(tmp).innerHTML = "";
				}else{
		//	document.getElementById('thirdMenu').innerHTML = colName + "<br>";
					if(colName ==""){
						document.getElementById(tmp).innerHTML = "Empty stuff";
						return;
					} else{
						 if (window.XMLHttpRequest) {
							// code for IE7+, Firefox, Chrome, Opera, Safari
						var	xmlhttp = new XMLHttpRequest();
						} else {
							// code for IE6, IE5
						var	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
						}
					xmlhttp.onreadystatechange = function() {
            			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
							if(download == false){
								var resp = xmlhttp.responseText;
								var index = resp.indexOf("][");

								var str1 = resp.substr(0,index + 1);
								var str2 = resp.substr(index+1);
							//	document.getElementById('thirdMenu').innerHTML = "str1: " + str1 + "<br> str 2: " + str2;

								str1 = str1.replace("[",",");
								str1 = str1.replace("]",",");
								str1 = str1.replace(/"/g,'');
								str1.trim();
								 dat = str1.split(',');
								columnDate = str1.split(',');

								str2 = str2.replace("[",",");
								str2 = str2.replace("]",",");
								str2 = str2.replace(/"/g,'');
								str2.trim();
								col = str2.split(',');
								columnInfo = str2.split(',');

								dat.pop(); col.pop(); dat.shift(); col.shift();
								columnDate.pop(); columnInfo.pop(); columnDate.shift(); columnInfo.shift();
								for(var i =0; i < col.length; i++){
								//	col[i] = parseInt(col[i]);
								//	dat[i] = parseInt(dat[i]);
									columnDate[i] = parseInt(columnDate[i]);
									columnInfo[i] = parseInt(columnInfo[i]);

								}
								drawChart(count);
							} else{
							var dButton = "<a href=\"results.json\" download><button class=\"btn btn-primary\">Download File</button></a>";
							var tmp = "downloadButton" + count;
							document.getElementById(tmp).innerHTML = dButton;
								} //Else Close
						}
					}
					
					}
					xmlhttp.open("POST","dbColRead.php",true);
					xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				//	param.trim();
					
					xmlhttp.send(param);
					
				}
			
		}
		
		function drawChart(count){
			var data = new google.visualization.DataTable();
			data.addColumn('datetime', 'Date');
			data.addColumn('number', columnName);
			
			var dateFormatter = new google.visualization.DateFormat({formatType: 'short'});
			var result = dateFormatter.formatValue(new Date(columnDate[0]));
			
			//	data.addRow([new Date(0),5]);\
			//	document.getElementById('tmp').innerHTML = result;
				for(var i = 0; i < columnInfo.length; i++){
					
					//var tmp = new Date(columnDate[i]);
					data.addRow([new Date(columnDate[i]) , columnInfo[i]]);
				}
		
			var options = {'title':currentTable,
					explorer:{}};
			
			var tmp = "chart_div" + count;
			var chart = new google.visualization.LineChart(document.getElementById(tmp));
			chart.draw(data,options);
			
			tmp  = "table_div"  + count;
			var table = new google.visualization.Table(document.getElementById(tmp));
        	table.draw(data, {showRowNumber: true});

		}
		//Checks how many components are in the database, and lists them in a drop down menu. When a user clicks on them it calls the primaryMenu() function. 
		function generateListOfComponents(){
			if (window.XMLHttpRequest) {
				// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlhttp=new XMLHttpRequest();
			  } else {  // code for IE6, IE5
				xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  	}	
			xmlhttp.onreadystatechange=function() {
				if (xmlhttp.readyState==4 && xmlhttp.status==200) {
				//  document.getElementById("main").innerHTML=xmlhttp.responseText;
				//	document.getElementById('main').appendChild(document.createTextNode(xmlhttp.responseText)); 
					$(document).ready(function(){
							$("#main").append(xmlhttp.responseText);
					});
				}
		  	}
		  xmlhttp.open("GET","generateComponentList.php?count=" + graphCounter,true);
		  xmlhttp.send();
			graphCounter++;
		}
		
		function removeGraph(count){
			$(document).ready(function(){
				$("#graph"+count).remove();
			});
		}
		
		function downloadGraph(count){
			var colNum = parseInt(count);
			var tableNum = parseInt(count);
			
			setColumn(count,true);
			
		}
	</script>
	</head>
	<body>
			<!--Adds the NAV bar --> 
	<script src="globalLayout.js"></script>
		
<div class="container">
	<div class="jumbotron">
		<h1>DNS-Jedi</h1>
		<p>Welcome to the website of DNS-Jedi. This website is still under construction and can only view basic information.</p>
	</div>
<button type="button" class="btn btn-primary" onclick="generateListOfComponents()">+Graph</button>
	
<div id="main"></div>	
			
				
			
		<!--Div that will hold the pie chart-->
		<div id="tmp"></div>
</div>
	</body>
</html>

