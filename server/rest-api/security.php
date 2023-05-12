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
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if ($url == '/ada/security/right') {
            echo Right::toJson($db->getRights());
        }
        else if ($url == '/ada/security/identityprovider') {
            echo IdentityProvider::toJson($db->getIdentifyProviders());
        }
        else {
            sendState(404, "Invalid request");
        }
    }
    else {
        sendState(404,"");
    }

    function sendState($code, $message) {
        header("HTTP/1.1 " . $code . " " . $message);
    }
?>
