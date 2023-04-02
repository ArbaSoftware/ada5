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
    if ($_SERVER['REQUEST_METHOD'] == 'GET') {
        if ($url == '/ada/store') {
            echo Store::toJsons($db->getStores());
            exit;
        }
        else if (sizeof($urlparts) == 4 && substr($url, 0, strlen('/ada/store/')) == '/ada/store/') {
            try {
                $store = $db->getStore($urlparts[3]);
                if ($store) {
                    echo $store->toJson();
                    exit;
                }
            }
            catch (Exception $exception) {
                header("HTTP/1.1 500 ". $exception->getMessage());
                exit;
            }
        }
    }
    else if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if ($url == '/ada/store') {
            try {
                $request = json_decode(file_get_contents('php://input'));
                if ($request && validateAddRequest($request)) {
                    if ($db->isStoreNameUnique($request->name)) {
                        if ($db->canRead('domain')) {
                            echo 'Jo';
                        }
                        else
                            sendState(500, "Insufficient rights");
                        exit;
                    }
                    else {
                        sendState(500, "Store name not unique");
                        exit;
                    }
                }
                else {
                    header("HTTP/1.1 500 Invalid request");
                    exit;
                }
            }
            catch (Exception $err) {
                header("HTTP/1.1 500 " . $err->getMessage());
                exit;
            }
        }
    }
    sendState(404, "Not found");

    function validateAddRequest($request) {
        if (isset($request->name) && !isset($request->id)) {
            return true;
        }
        else
            return false;
    }

    function sendState($code, $message) {
        header("HTTP/1.1 " . $code . " " . $message);
    }
?>