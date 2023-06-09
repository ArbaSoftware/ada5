<?php
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('auth.php');

    $auth = new Auth();
    if (!$user = $auth->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }
    $db = new MySql('192.168.2.74', 'ada', 'ada', 'ada5', $user->getEmail(), $user->getIdentifyProviderId());

    $url = $_SERVER['REQUEST_URI'];
    $urlparts = explode('/', $url);
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if ($url == '/ada/addon') {
            if ($db->canAddAddon()) {
                $json = file_get_contents('php://input');
                $errors = Addon::validateJson($json, $db);
                if ($errors && gettype($errors) == "boolean") {
                    $addon = json_decode($json);
                    try {
                        $db->addAddOn($addon->id, $addon->name, $json);
                        sendState(200, 'OK');
                    }
                    catch (Exception $err) {
                        sendState(500, $err->getMessage());
                    }
                }
                else {
                    sendState(500, "Invalid request", JsonUtils::createErrorJson($errors));
                }
            }
            else {
                sendState(401, 'Insufficient rights');
            }
        }
        else
            sendState(404, '');
    }
    else if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $url = $_SERVER['REQUEST_URI'];
        $urlparts = explode('/', $url);
        if ($url = '/ada/addon') {
            if ($db->canUpdateAddOn()){
                $json = file_get_contents('php://input');
                $errors = Addon::validateJson($json, $db);
                if ($errors && gettype($errors) == "boolean") {
                    $addon = json_decode($json);
                    try {
                        $db->updateAddon($addon->id, $addon->name, $json);
                        sendState(200, 'OK');
                    }
                    catch (Exception $err) {
                        sendState(500, $err->getMessage());
                    }
                }
                else {
                    sendState(500, "Invalid request", JsonUtils::createErrorJson($errors));
                }
            }
            else
                sendState(401, 'Insufficient rights');
        }
    }

    function sendState($code, $message, $json = "") {
        header("HTTP/1.1 " . $code . " " . $message);
        echo $json;
    }
?>        
