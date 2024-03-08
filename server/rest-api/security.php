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
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if ($url == '/ada/security/right') {
            echo Right::toJson($db->getRights());
        }
        else if ($url == '/ada/security/identityprovider') {
            echo IdentityProvider::toJson($db->getIdentifyProviders());
        }
        else if ($url == '/ada/security/domainrights') {
            if ($db->canGetDomainRights()) {
                echo $db->getDomainRights();
            }
            else
                sendState(401, "Insufficient rights");
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
