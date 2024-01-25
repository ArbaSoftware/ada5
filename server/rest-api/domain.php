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
        if ($url == '/ada/domain/rights') {
            if ($db->canGetDomainRights()) {
                echo $db->getDomainRights();
            }
            else
                sendState(401, "Insufficient rights");
        }
        else if ($url == '/ada/domain/mimetypes') {
            if ($db->canGetMimetypes()) {
                echo $db->getMimetypes();
            }
            else 
                sendState(401, "Insufficient rights");
        }
        else {
            sendState(404, "Invalid request");
        }
    }
    else if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if ($url == '/ada/domain/mimetype') {
            $request = file_get_contents("php://input");
            $validationerrors = JsonUtils::validate($request, "addmimetype");
            if ($validationerrors && gettype($validationerrors) == 'boolean') {
                if ($db->canCreateMimetype()) {
                    if ($db->createMimetype(json_decode($request))) {
                        sendState(200, "");
                    }
                    else {
                        sendState(500, "");
                    }
                }
                else {
                    sendState(401, "Insufficient rights");
                }
            }
            else {
                sendState(500, "");
            }
        }
        else {
            sendState(404, "");
        }
    }
    else {
        sendState(404,"");
    }

    function sendState($code, $message) {
        header("HTTP/1.1 " . $code . " " . $message);
    }
?>
