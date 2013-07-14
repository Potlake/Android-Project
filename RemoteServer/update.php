<?php
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['number']) && isset($_POST['completed'])) {
 
    $number = $_POST['number'];
    $completed = $_POST['completed'];
 
    // include db connect class
    require_once __DIR__ . '/connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched number
    $result = mysql_query("UPDATE products SET completed = '$completed' WHERE number = $number");
 
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
