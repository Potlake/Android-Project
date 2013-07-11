<?php
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["pid"])) {
    $pid = $_GET['pid'];
 
    // get a product from products table
    $result = mysql_query("SELECT * FROM products WHERE pid = $pid");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $product = array();
            $product["pid"] = $result["pid"];
            $product["name"] = $result["name"];
            $product["receiver"] = $result["receiver"];
            $product["address"] = $result["address"];
            $product["completion"] = $result["completion"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["product"] = array();
            array_push($response["product"], $product);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>
