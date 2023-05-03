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
    $db = new MySql('192.168.2.74', 'ada', 'ada', 'ada5', $user->getId(), $user->getIdentifyProviderId());

    $url = $_SERVER['REQUEST_URI'];
    $urlparts = explode('/', $url);
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if ($url == '/ada/addon') {
            if ($db->canAddAddon()) {
                $json = file_get_contents('php://input');
                if (Addon::validateJson($json, $db)) {
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
                    sendState(500, 'Invalid request');
                }
            }
            else {
                sendState(401, 'Insufficient rights');
            }
        }
        else
            sendState(404, '');
    }
    function sendState($code, $message) {
        header("HTTP/1.1 " . $code . " " . $message);
    }
?>        
