<html><head><title>General Information</title>
	
	<!-- This page displays information on the latest entries of components -->
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
	</head>
<script>
		var tableNames = [];
		var columnNames = [];
		
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
	
		/*
			This function generates a dropdown menu listing all the components.
		*/
		function selectComponentGeneral(){
				var xmlhttp;
				if (window.XMLHttpRequest)
				  {// code for IE7+, Firefox, Chrome, Opera, Safari
				  xmlhttp=new XMLHttpRequest();
				  }
				else
				  {// code for IE6, IE5
				  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				  }
				xmlhttp.onreadystatechange=function()
				  {
				  if (xmlhttp.readyState==4 && xmlhttp.status==200)
					{
					document.getElementById("main").innerHTML=xmlhttp.responseText;
					}
				  }
				xmlhttp.open("GET","generalInfo.php?t=" + Math.random(),true);
				xmlhttp.send();
		}
		
		/*
			This function takes a component and retrieves its latest entries. 
		*/
		function generateGeneralInfo(){
				var tname = document.getElementById('generalBox').value;
			
			if(tname != 'null'){
				var xmlhttp;
				if (window.XMLHttpRequest)
				  {// code for IE7+, Firefox, Chrome, Opera, Safari
				  xmlhttp=new XMLHttpRequest();
				  }
				else
				  {// code for IE6, IE5
				  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				  }
				xmlhttp.onreadystatechange=function()
				  {
				  if (xmlhttp.readyState==4 && xmlhttp.status==200)
					{
						parseInformation(xmlhttp.responseText);			//Sends the respons to be parsed to a table
					}
				  }
				xmlhttp.open("POST","generalInfo.php",true);
				xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xmlhttp.send("tname="+tname);
				setColumn("cpu");
				setColumn("mem");
				setColumn("hdd");
			} else {document.getElementById('information').innerHTML = "" ;}
				
		}
		
		/*
				This function takes the information retrieved from the server and puts it into a table.
		*/
		function parseInformation(information){
				var info = information.toString();
				info = info.replace(/['" /{ /} /[ ]+/g, '');
			info = info.replace("]","");
			info = info.split(',');
			document.getElementById("information").innerHTML= info + "   type of info: "  + typeof(info);
			
			var result = "<table class='table table-hover'> <thead><tr><th>Attribute</th><th>Value</th> </tr></thead><tbody>";
			
			var tmp = info[0].split(':');
			result= result + "<tr><td> Date </td><td>"+ new Date(parseInt(tmp[1])) + "</td></tr>";
			
			for(var i  = 1; i < info.length; i++){
				tmp = info[i].split(':');
				result= result + "<tr><td>"+ tmp[0] +"</td><td>"+ tmp[1] + "</td></tr>";
			}
			
			
			result = result + "</tbody></table>";
			document.getElementById('information').innerHTML = result;
		}
	
	
		function setColumn(colName){
			var tName = document.getElementById("generalBox").value;
			setTable(tName);
			setColumnName(colName);
			var param= "colName=" + colName + "&tableName="+tName+ "&download=" + "false";
			var col;
			var dat;
			
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
					drawChart(colName);
				}
			}
					
		xmlhttp.open("POST","dbColRead.php",true);
		xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");					
		xmlhttp.send(param);	
	}

	function drawChart(colName){
		var data = new google.visualization.DataTable();
		data.addColumn('datetime', 'Date');
		data.addColumn('number', colName);
			
		var dateFormatter = new google.visualization.DateFormat({formatType: 'short'});
		var result = dateFormatter.formatValue(new Date(columnDate[0]));
			
		for(var i = 0; i < columnInfo.length; i++){
			data.addRow([new Date(columnDate[i]) , columnInfo[i]]);
		}
		
		var options = {'title':currentTable, explorer:{}};
			
		var tmp = "chart_div_" + colName;
		var chart = new google.visualization.LineChart(document.getElementById(tmp));
		chart.draw(data,options);
	}
	
	</script>

<body>
	
	
<script src="globalLayout.js"></script>	
	
	<div class="container">
		<div class="jumbotron">
		
		<h1>General Information</h1>
		</div>
	
		<script>selectComponentGeneral();	</script>
		<h3>Please select a component</h3>
		
		
		
		<div id="main">
		
		</div>
		
		<div id="container">
			<div class="row">
				<div class="col-xs-4">
					<div class="tab-pane fade active in" id="chart_div_cpu" style="width:400; height:300"></div> 
				</div>
				<div class="col-xs-4">
					<div class="tab-pane fade active in" id="chart_div_mem" style="width:400; height:300"></div> 
				</div>
				<div class="col-xs-4">
					<div class="tab-pane fade active in" id="chart_div_hdd" style="width:400; height:300"></div> 
				</div>
			</div>
		</div>
		
		<div id="information">
		
		</div>

		
		<div id="test">
			</div>
	</div>
	
</body>
</html>
