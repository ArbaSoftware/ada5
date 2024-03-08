<?php
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('auth.php');
    include('settings.php');

    $auth = new Auth($_settings);
    if (!$user = $auth->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }
    $db = new MySql($_settings['dbhost'], $_settings['dbuser'], $_settings['dbpassword'], $_settings['dbname'], $user->getEmail(), $user->getIdentifyProviderId(), $_settings['contentdir']);

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
    else if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if ($url == '/ada/addon') {
            if ($db->canGetAddons()) {
                sendState(200, "", $db->getAddOns());
            }
            else {
                sendState(401, "Insufficient rights");
            }
        }
        else {
            sendState(404, "Not found");
        }
    }

    function sendState($code, $message, $json = "") {
        header("HTTP/1.1 " . $code . " " . $message);
        echo $json;
    }
?>        
