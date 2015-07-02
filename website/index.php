<?php
	include_once('session.php');
?>
<html>
	
	<!--This webpage shows any notifications and their graphs-->
	<head><title>DNS - Jedi</title> 
		
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
		  
	
		
		
	<script>
		var current = [];
		// Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
		google.load("visualization", "1", {packages:["table"]});

      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(drawChart);
		
		/*This function generates a JSON file which can then be download. Also does it append a download button to the website*/
		function createDownload(){
				var xmlhttp;
				if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
				  xmlhttp=new XMLHttpRequest();
				  }
				else {// code for IE6, IE5
				  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				  }
				xmlhttp.onreadystatechange=function()  {
				  if (xmlhttp.readyState==4 && xmlhttp.status==200)	{
					var button = "<li class='list-group-item'><a href='results.json' download><button class='btn btn-default'>Download</button></a></li>";
					  $(document).ready(function(){
								$("#download_button").append(button);
						});
					}
				  }
				xmlhttp.open("POST","dbDownloadTable.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("getColumns=" + current[1] + "&tableName="+current[0]);
		}
		
		function createGraph(component, attribute){
			
						 if (window.XMLHttpRequest) {
							// code for IE7+, Firefox, Chrome, Opera, Safari
						var	xmlhttp = new XMLHttpRequest();
						} else {
							// code for IE6, IE5
						var	xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
						}
					xmlhttp.onreadystatechange = function() {
            			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
							current[0] = component;
							current[1] = attribute;
							
								var resp = xmlhttp.responseText;
								var index = resp.indexOf("][");

								var str1 = resp.substr(0,index + 1);
								var str2 = resp.substr(index+1);

								str1 = str1.replace("[",",");
								str1 = str1.replace("]",",");
								str1 = str1.replace(/"/g,'');
								str1.trim();
								var arg1 = str1.split(',');

								str2 = str2.replace("[",",");
								str2 = str2.replace("]",",");
								str2 = str2.replace(/"/g,'');
								str2.trim();
								var arg2 = str2.split(',');

								arg1.pop(); arg2.pop(); arg1.shift(); arg2.shift();
								for(var i =0; i < arg1.length; i++){
								//	col[i] = parseInt(col[i]);
								//	dat[i] = parseInt(dat[i]);
									arg1[i] = parseInt(arg1[i]);
									arg2[i] = parseInt(arg2[i]);
								}
								document.getElementById('download_div').innerHTML = "<ul id='download_button' class='list-group'><li class='list-group-item'><button class='btn btn-link' onclick='createDownload()'>Generate JSON File</button></li></ul>";
								drawChart(arg1,arg2, component, attribute);
							
						}
					}
					
					xmlhttp.open("POST","dbColRead.php",true);
					xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				//	param.trim();
					
					xmlhttp.send("colName=" + attribute + "&tableName="+component + "&download=false");
		}
		
			function drawChart(arg1, arg2, component, attribute){
		
			var data = new google.visualization.DataTable();
			data.addColumn('datetime', 'Date');
			data.addColumn('number', attribute);
			
			var dateFormatter = new google.visualization.DateFormat({formatType: 'short'});
			
			//	data.addRow([new Date(0),5]);\
			//	document.getElementById('tmp').innerHTML = result;
				for(var i = 0; i < arg2.length; i++){
					//var tmp = new Date(columnDate[i]);
					data.addRow([new Date(arg1[i]) , arg2[i]]);
				}
		
			var options = {'title':component,
						'width':600,
                       'height':500,
					explorer:{}};
			
			var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
			chart.draw(data,options);
			
		
			var table = new google.visualization.Table(document.getElementById('table_div'));
        	table.draw(data, {showRowNumber: true, height:300, sort:'enable'});
		

		}
		
		
		/*When the user click the x, the corresponding notification is permanently removed from the database.*/
		function removeNoti(index, component, attribute, message,date){
			
			var noti = "#noti" + index;
			$(document).ready(function(){
					$(noti).remove();
			});
				
			var xmlhttp;
			if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
			  }
			else {// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  }
			xmlhttp.onreadystatechange=function() {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200){
			//	document.getElementById("test").innerHTML=xmlhttp.responseText;
				}
			  }
			xmlhttp.open("POST","notifications.php",true);
			xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xmlhttp.send("comp=" + component + "&attr=" + attribute + "&msg=" + message + "&date=" + date);
			
		}
		
		/*This function pastes the notifications on the page. The code for this is generated by the web server.*/
		function createNoti(){
			var xmlhttp;
			if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
			  xmlhttp=new XMLHttpRequest();
			  }
			else {// code for IE6, IE5
			  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
			  }
			xmlhttp.onreadystatechange=function() {
			  if (xmlhttp.readyState==4 && xmlhttp.status==200){
				document.getElementById("notifications").innerHTML=xmlhttp.responseText;
				}
			  }
			xmlhttp.open("POST","notifications.php",true);
			xmlhttp.send();
		}
	</script>	
		
		
	</head>
	<body>
		<!--Includes the NAV bar -->
		<script src="globalLayout.js">
			
		</script>
		
		<div class="container">
			<div class="jumbotron"><img src="yoda.png" style="width:220px;height:170px;" class="pull-right">
		<h1>DNS-Jedi </h1>
		<p>Welcome to the website of DNS-Jedi </p>
		</div>
			<div class="row">
				<div class="col-sm-4">
					<span><h4>Notifications</h4></span>
					<div id="notifications">Notifcations</div>
					<script> createNoti()</script>
				</div>
				<div class="col-sm-6" >
					<ul class="nav nav-tabs">
						<li class="active"><a href="#chart_div" data-toggle="tab">Graph</a></li>
						<li><a href="#table_div" data-toggle="tab">Table</a></li>
						<li><a href="#download_div" data-toggle="tab">Export</a></li>
					</ul>
					
					<div class="tab-content">
						<div class="tab-pane fade active in" id="chart_div">Graph data</div>
						<div class="tab-pane fade" id="table_div">Table data</div>
						<div class="tab-pane fade" id="download_div">Download data</div> 

					</div>
					
				</div>
			</div>
			<div id="test"></div>
		</div>
	</body>
</html>
