<?php

$servername = "localhost";
$username = "u910989898_propertypal";
$password = "Thedrunkfox67";
$database = "u910989898_propertypal";
 
 
//creating a new connection object using mysqli 
$conn = new mysqli($servername, $username, $password, $database);
 
//if there is some error connecting to the database
//with die we will stop the further execution by displaying a message causing the error 
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
 
//if everything is fine
 
//creating an array for storing the data 
$addresses = array(); 
 
//this is our sql query 
$sql = "SELECT * FROM markers";
 
//creating an statment with the query
$stmt = $conn->prepare($sql);
 
//executing that statment
$stmt->execute();
 
//binding results for that statment 
$stmt->bind_result($id, $name, $address, $lat, $lng, $type, $urlString, $information, $imgUrl);
 
//looping through all the records
while($stmt->fetch()){
	
	//pushing fetched data in an array 
	$temp = [
		'id'=>$id,
        'name'=>$name,
        'address' =>$address,
        'lat' =>$lat,
        'lng' =>$lng,
        'type' =>$type,
        'urlString' =>$urlString,
        'information' => $information,
        'imgUrl' => $imgUrl
	];
	
	//pushing the array inside the addresses array 
	array_push($addresses, $temp);
}
 
//displaying the data in json format 
echo json_encode($addresses);