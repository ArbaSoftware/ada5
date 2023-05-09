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
        else if (sizeof($urlparts) == 5 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class') {
            try {
                $classes = $db->getClasses($urlparts[3]);
                if (sizeof($classes) == 0)
                    echo '[]';
                else
                    echo AdaClass::toJson($classes);
                exit;
            }
            catch (Exception $exception) {
                sendState(500, "");
                exit;
            }
        }
        else if (sizeof($urlparts) == 6 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class') {
            try {
                $class = $db->getClass($urlparts[3], $urlparts[5]);
                if ($class) {
                    echo AdaClass::toJson($class);
                }
                else {
                    sendState(404, "Class not found");
                }
                exit;
            }
            catch (Exception $exception) {
                sendState(500, "");
                exit;
            }
        }
        else if (sizeof($urlparts) == 7 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class' && $urlparts[6] == 'schema') {
            $class = $db->getClass($urlparts[3], $urlparts[5]);
            if ($class) {
                echo $class->createObjectSchema();
            }
            else {
                sendState(404, "Class not found");
            }
            exit;
        }
    }
    else if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        if ($url == '/ada/store') {
            try {
                $json = file_get_contents('php://input');
                $request = json_decode($json);
                if ($errors = JsonUtils::validate($json, 'addstorerequest')) {
                    if ($db->isStoreNameUnique($request->name)) {
                        if ($db->canCreateStore()) {
                            $newStoreId = $db->createStore($request->name, $request->grantedrights, $request->addons);
                            echo $newStoreId;
                        }
                        else
                            sendState(401, "Insufficient rights");
                        exit;
                    }
                    else {
                        sendState(500, "Store name not unique");
                        exit;
                    }
                }
                else {
                    $errorJson = "{\"error\": \"Invalid request\", \"messages\": [";
                    $prefix = "";
                    foreach($errors as $error) {
                        $errorJson .= $prefix . '"'. $error . '"';
                        $prefix = ',';
                    }
                    $errorJson .= "]}";
                    sendState(500, $errorJson);
                    exit;
                }
            }
            catch (Exception $err) {
                header("HTTP/1.1 500 " . $err->getMessage());
                exit;
            }
        }
        else if (sizeof($urlparts) == 5 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class') {
            $storeId = $urlparts[3];
            if ($db->getStore($storeId)) {
                $json = file_get_contents("php://input");
                if (JsonUtils::validate($json, "addclassrequest")) {
                    $request = json_decode($json);
                    if ($db->areValidRights($request->rights)) {
                        if ($db->isUniqueClassName($storeId, $request->name)) {
                            if ($db->canCreateClass($storeId)) {
                                $class = AdaClass::fromJson($request);
                                $newClassId = $db->createClass($storeId, $class);
                                echo $newClassId;
                                exit;
                            }
                            else {
                                sendState(401, "Insufficient rights");
                                exit;
                            }
                        }
                        else {
                            sendState(500, "Class name not unique");
                            exit;
                        }
                    }
                }
            }
            header("HTTP/1.1 500 Invalid request");
            exit;
        }
        else if (sizeof($urlparts) == 7 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class' && $urlparts[6] == 'object') {
            $class = $db->getClass($urlparts[3], $urlparts[5]);
            if ($class) {
                $json = file_get_contents("php://input");
                $validationErrors = Jsonutils::validate($json, $class->createObjectSchema());
                if ($validationErrors && gettype($validationErrors) == 'boolean') {
                    echo "valid request";
                }
                else {
                    echo "Invalid request";
                }
                exit;
            }
            else {
                sendState(404, "Class not found");
            }
            exit;
        }
    }
    else if ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
        if (sizeof($urlparts) == 4 && substr($url, 0, strlen('/ada/store/')) == '/ada/store/') {
            $storeId = $urlparts[3];
            if ($db->canDeleteStore($storeId)) {
                if ($db->deleteStore($storeId)) {
                    sendState(200, "Store deleted");
                }
                else {
                    sendState(500, "");
                }
                exit();
            }
            else {
                sendState(401, "Unsufficient rights");
                exit;
            }
        }
    }
    sendState(404, "Not found");

    function sendState($code, $message) {
        header("HTTP/1.1 " . $code . " " . $message);
    }
?>