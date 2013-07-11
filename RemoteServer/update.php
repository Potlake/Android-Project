<?php
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['pid']) && isset($_POST['name']) && isset($_POST['receiver']) && isset($_POST['address']) && isset($_POST['completion'])) {
 
    $pid = $_POST['pid'];
    $name = $_POST['name'];
    $receiver = $_POST['receiver'];
    $address = $_POST['address'];
    $completion = $_POST['completion'];
 
    // include db connect class
    require_once __DIR__ . '/connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("UPDATE products SET name = '$name', receiver = '$receiver', address = '$address', completion = '$completion' WHERE pid = $pid");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Product successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
