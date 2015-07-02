<?php
include_once('session.php');
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
	
	<title>Graphing tools</title>
	<link rel="icon" 
      type="image/ico" 
      href="favicon.ico">
	 <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
 <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="custom.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
	
	<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
	
	
	<!--The java script containing the functions -->
	<script type="text/javascript">
		
		/* Used to store the name of the components and the attributes*/
				
		var tableNames = [];
		var columnNames = [];
		
		//Count number of graphs loaded
		var graphCounter = 0;
		
		//Set variables
		
		var columnName = "My Graph";	// The name of the attribue
		var columnDate = [0];			// The array which stores the dates
		var currentTable = "";			// The name of the current table being edited
		var columnInfo = [0];			// The data retrieved for one attribute
		
		//Setter functions to set these variables.
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
		
		// Load the Visualization API and the linechart package.
		google.load('visualization', '1.0', {'packages':['corechart']});		//This is the package to load the line chart
		google.load("visualization", "1", {packages:["table"]});				//Package to load the table

      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(setColumn);

		
		/*	The function to create the dropdown menu which lists all the components.
			This function takes as the argument the number of graphs already on the page.
			This number is used to create a unique ID for every element of the Graph module.
			
			The function sends a POST request to the server, which echoes back the code for the first drop down menu
			
		*/
		function primaryMenu(count){
			var tmp = "selectBox" + count; 
			var selectBox = document.getElementById(tmp);
    		var str = selectBox.options[selectBox.selectedIndex].value;
			var tableName = selectBox.options[selectBox.selectedIndex].value;
			tableNames[count] = tableName;
			
			var param = "wname="+str;			//This variable is sent in the POST request
		
				if(str=="empty"){				//Don't do anything when the string is empty
				}else{
		
					if(str ==""){
						document.getElementById('main').innerHTML = "";
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
							var resp = xmlhttp.responseText;				//Retrieve the respons
							resp.trim();									//Remove any white spaces surrounding the string
							secondMenu(resp, tableName, count);				//Pass it to the secondMenu() which will create a dropdown menu of attributes
						}
					}
					
					}
					xmlhttp.open("POST","dbRead.php",true);
					xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
					xmlhttp.send(param);									//Send the param with the request
				}
		}
		
		/*
			This function is part of the download tab. Upon selecting the 'select all', all checkboxes must be selected. 
		*/
		function selectCheckBox(count){
			
			var selector = ".checkbox" + count;
			if($('#checkboxSelector'+count).prop('checked')){
				
				$(selector).each(function() { 		//loop through each checkbox
                this.checked = true;  				//select all checkboxes with class               
            	});
			}else{									
				$(selector).prop('checked',false);	//Otherwise uncheck all selectboxes
			}
		}
		
		/*
			This function creates a dropdown menu containing all the attributes. 
			The respons from the HTTP POST from primaryMenu() is removed of [ and ] and split into an array. For every element of the array an option in the dropdown menu is created.
			Also in this function all the checkboxes for the export tab are created. 
		*/
		function secondMenu(resp, tableName, count){
		
			//The code which creates the dropdown menu. 
			var downl = "<ul class='list-group'><li class='list-group-item'>Check the following box to select all attributes: <br><br><label class=\"checkbox-inline\"><input id=\"checkboxSelector"+count+"\" type=\"checkbox\" onclick=\"selectCheckBox("+count+")\">Select all</label></li>";
			downl = downl + "<div id='c_b'><li class='list-group-item'>Select the attributes you would like to download: <br><br>";
			
				//Prepare to make the respons into an array. 
				resp = resp.replace("[","");			//Remove [
				resp = resp.replace("]",",");			//Remove ]
				resp = resp.replace(/"/g,'');			//Remove all other stuff which should not be there. 
		
				resp.trim();
				var ar = resp.split(',');				//Create an array by splitting the respons text by commas. 
				var tmp = "selectBoxSecondary" + count;
				output = output + "<ul class='list-group'>";
				var output = "<li class='list-group-item'>Please select the desired attribute you would like to display: <br><br><select id = \"" + tmp +  "\" onchange= \"setColumn(" + count + ",false) \" name=\"" + tableName + "\"></li>";
					output = output+ "<option>Please select an attribute: </option>";
				
			//Loop over all attributes and create a checkox/dropdown option
			for(var i = 2; i < ar.length - 1; i++){
				ar[i].trim();
				output = output + "<option value=\"" + ar[i] + "\">" + ar[i] + "</option>";		//Options for the dropdown menu
				
				downl = downl + "<label class=\"checkbox-inline\"><input class=\"checkbox"+count+"\" type=\"checkbox\" value=\"" + ar[i] + "\">" + ar[i] + "</label>";		//Checkboxes for the export page. 
			}
			output = output + "</select>";		
			output = output + "</ul><br><br>";
			tmp = "secondMenu" + count;
			document.getElementById(tmp).innerHTML = output; 		//Create the dropdown menu
			
			//Create an input option for starting and ending date for export
			downl = downl + "</li></div></ul>";
			downl = downl + "<li class='list-group-item'> <p>Please enter a starting and ending date. It is possible to select rows in the table to automatically fill in these fields. </p> ";
			downl = downl + "<br> <ul><li>Start date: <input id ='beginDate"+ count + "' type='text'> <button class=\"btn  btn-primary pull-right btn-xs\" onclick=\"document.getElementById('beginDate"+count+"').value=''\">Clear</button></li> <br> <li>End date: <input id ='endDate" +count + "' type='text'><button class=\"btn  btn-primary pull-right btn-xs\" onclick=\"document.getElementById('endDate"+count+"').value=''\">Clear</button ></li></ul> <p id='exportStatus"+count+ "' class=\"text-warning\"></p> <br></li>";
			
			var tmp2 = "advancedOptions" + count;	
			document.getElementById(tmp2).innerHTML = downl;		//Create export page
			
			return;
		}
		
		/*
			When a user selects an attribute and component, those names are sent to the server to retrieve the data corresponding to these names. That happens in this function
			
		*/
		
		function setColumn(count, download){
			var tmp = "selectBoxSecondary" + count;
			var selectBox = document.getElementById(tmp);
			var tName = document.getElementById(tmp).name;				//The name of the attribute
			setTable(tName);
    		var colName = selectBox.options[selectBox.selectedIndex].value;
			setColumnName(colName);
			columnNames[count] = colName;
			
			//The parameter which is sent with the POST request. This contains, the attribute name, and whether it should download anything. 
			var param= "colName=" + columnNames[count] + "&tableName="+tableNames[count]+ "&download=" + download.toString();
			
			var lim = "limit" + count;
			var limCount = 1000;
			if(document.getElementById(lim).value != "")
				limCount = document.getElementById(lim).value;
				
			param = param + "&limit=" + limCount;
			
			var col;
			var dat;
			
			tmp = "thirdMenu" +count; 
			if(colName=="empty"){
					document.getElementById(tmp).innerHTML = "";
				}else{
					if(colName ==""){
						document.getElementById(tmp).innerHTML = /*"Empty stuff"*/"";
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
								
								/*
									Upon receiving the respons, it is parsed to an array and flushed to the drawChart() function. 
								*/
								
								var resp = xmlhttp.responseText;
								var index = resp.indexOf("][");

								var str1 = resp.substr(0,index + 1);
								var str2 = resp.substr(index+1);

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
									columnDate[i] = parseInt(columnDate[i]);
									columnInfo[i] = parseInt(columnInfo[i]);

								}
								drawChart(count);			//Draw the chart and pass the ID, so that the function knowns where to place the chart. 
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
					xmlhttp.send(param);
					
				}
			
		}
		
		/*
			This function takes the arrays created in the previous functions and draws graph and table. The count is used to identify the ID where the charts should be drawn.
		*/
		function drawChart(count){
			var data = new google.visualization.DataTable();
			data.addColumn('datetime', 'Date');
			data.addColumn('number', columnName);
			
			var dateFormatter = new google.visualization.DateFormat({formatType: 'short'});
			var result = dateFormatter.formatValue(new Date(columnDate[0]));
			
				for(var i = 0; i < columnInfo.length; i++){
					data.addRow([new Date(columnDate[i]) , columnInfo[i]]);
				}
		
			var options = {'title':currentTable,
					explorer:{}};
			
			var tmp = "chart_div" + count;
			var chart = new google.visualization.LineChart(document.getElementById(tmp));
			chart.draw(data,options);
			
			tmp  = "table_div"  + count;
			var table = new google.visualization.Table(document.getElementById(tmp));
        	table.draw(data, {'showRowNumber': true, 'height': 300, sort: 'enable'});
			google.visualization.events.addListener(table, 'select', function(){
				var beginDate = "beginDate" + count; 
				var endDate = "endDate" + count;
				var row = table.getSelection()[0].row;
				if(document.getElementById(beginDate).value === ""){
					document.getElementById(beginDate).value = data.getValue(row, 0).toString();
				} else if(document.getElementById(endDate).value === ""){
						var msg_div = 'exportStatus' + count;
						if(new Date(document.getElementById(beginDate).value).getTime() > new Date(data.getValue(row, 0).toString()).getTime()){	//If the endDate is smaller than beginDate, don't fill in field
							document.getElementById(msg_div).innerHTML = "The end date should not be smaller than the begin date.";
						} else
						document.getElementById(endDate).value = data.getValue(row, 0).toString();
				}
			});

		}
		
		function selectHandler(e){
			document.getElementById('table_info').innerHTML = 'Hey' + e['Date'];
			var selection = table.getSelection().length;
		}
		
		//Checks how many components are in the database, and lists them in a drop down menu. When a user clicks on them it calls the primaryMenu() function. 
		function generateListOfComponents(){
			if (window.XMLHttpRequest) {
				// code for IE7+, Firefox, Chrome, Opera, Safarit
				xmlhttp=new XMLHttpRequest();
			  } else {  // code for IE6, IE5
				xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  	}	
			xmlhttp.onreadystatechange=function() {
				if (xmlhttp.readyState==4 && xmlhttp.status==200) {
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
		
		/*
			Sends a download request to the server. 
		*/
		function downloadGraph(count){
			var cols = "";
			var chckbx = "checkbox" + count;
			$.each($("input[class='"+ chckbx + "']:checked"), function(){ //loop through each checkbox
                if(this.checked){
					cols = cols + $(this).val() + ",";	
				}              
            });
			cols = cols.substring(0,cols.lastIndexOf(','));
			
			var tmp = "selectBox" + count; 
			var selectBox = document.getElementById(tmp);
			var tname = selectBox.options[selectBox.selectedIndex].value;
			
			var selector = "beginDate" + count; 
			var beginDate = document.getElementById(selector).value;
			beginDate = new Date(beginDate).getTime();
			
			var selector2 = "endDate" + count; 
			var endDate = document.getElementById(selector2).value;
			endDate = new Date(endDate).getTime();
			
			
			var xmlhttp;
			if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
			  }
			else  {// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  }
			xmlhttp.onreadystatechange=function()  {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200){
				  var tmp = "downloadButton" + count;
				  document.getElementById(tmp).innerHTML = "<a href='results.json' download><button class='btn btn-success'>Download</button></a>";
				if(Boolean(xmlhttp.responseText)){
					
						
					}
				}
			  }
			xmlhttp.open("POST","dbDownloadTable.php",true);
			xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xmlhttp.send("tableName="+ tname + "&getColumns=" + cols + "&startDate=" + beginDate + "&endDate=" + endDate);
			
		}
		
		
	</script>
	</head>
	<body>
			<!--Adds the NAV bar --> 
	<script src="globalLayout.js"></script>
		
<div class="container">
	<div class="jumbotron">
		<h1>Graphing tools</h1>
		<p>Click on +Graph to open a new module. Select the desired components to display their basic information.</p>
	</div>
	
	<div id ="detailedInfo">
		<button type="button" class="btn btn-primary" onclick="generateListOfComponents()">+Graph</button>
		<div id="main">
		</div>
	</div>	
	
	
		<!--Testing div-->
		<div id="tmp"></div>
		<!--<div id="table_info">Test</div>-->
</div>
		
	</body>
</html>

