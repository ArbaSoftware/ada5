<?php
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('auth.php');
    define('CHUNK_SIZE', 1024*1024);

    $auth = new Auth();
    if (!$user = $auth->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }

    $db = new MySql('192.168.2.74', 'ada', 'ada', 'ada5', $user->getEmail(), $user->getIdentifyProviderId());

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
        else if (sizeof($urlparts) == 6 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'object') {
            $object = $db->getObject($urlparts[3], $urlparts[5]);
            if ($object) {
                echo $object->toJson();
                exit;
            }
            else {
                sendState(404, "Object not found");
            }

        }
        else if (sizeof($urlparts) == 8 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'object' && $urlparts[6] == 'content') {
            if ($db->canGetContent($urlparts[3], $urlparts[5])) {
                $content = $db->getContent($urlparts[3], $urlparts[5], $urlparts[7]);
                if ($content) {
                    header("Content-Type: " . $content->getMimetype());
                    header("Content-Length: " . $content->getSize());
                    header('Content-Disposition: attachment; filename="' . $content->getFileName() . "'");
                    streamfile_chunked($content->getContentFile());
                }
                else {
                    sendState(404, "Content not found");
                }
            }
            else
                sendState(401, "Insufficient rights");
        }
        else if (sizeof($urlparts) == 7 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'object' && $urlparts[6] == 'checkout') {
            if ($db->canCheckout($urlparts[3], $urlparts[5])) {
                if ($db->checkout($urlparts[3], $urlparts[5])) {
                    sendState(200, "Checked out");
                    echo "OK";
                }
                else 
                    sendState(500, "Checkout failed");
            }
            else
                sendState(401, "Insufficient rights");
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
                            if ($newStoreId = $db->createStore($request->name, $request->grantedrights, $request->addons)) {
                                echo $newStoreId;
                            }
                            else
                                sendState(500, "Store creation failed");
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
        else if (sizeof($urlparts) == 5 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'search') {
            $storeid = $urlparts[3];
            $search = file_get_contents('php://input');
            $validationerrors = JsonUtils::validate($search, "search");
            if ($validationerrors && gettype($validationerrors) == 'boolean') {
                $searchresults = $db->search($storeid, json_decode($search));
                if (gettype($searchresults) == "string") {
                    sendState(200, "");
                    echo $searchresults;
                }
                else {
                    sendState(500, "No searchresults");
                    echo JsonUtils::createErrorJson($searchresults);
                }
            }
            else {
                sendState(500, "Invalid request");
                echo JsonUtils::createErrorJson($validationerrors);
            }
            exit;
        }
        else if (sizeof($urlparts) == 5 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class') {
            $storeId = $urlparts[3];
            if ($db->getStore($storeId)) {
                $json = file_get_contents("php://input");
                $validationErrors = JsonUtils::validate($json, "addclassrequest");
                if ($validationErrors && gettype($validationErrors) == "boolean") {
                    $request = json_decode($json);
                    if ($db->areValidRights($request->rights)) {
                        if ($db->isUniqueClassName($storeId, $request->name)) {
                            if ($db->canCreateClass($storeId)) {
                                $class = AdaClass::fromJson($request);
                                $newClassId = $db->createClass($storeId, $class);
                                if (gettype($newClassId) == 'array') {
                                    sendState(500, "Class not added");
                                    echo JsonUtils::createErrorJson($newClassId);
                                }
                                else {
                                    echo $newClassId;
                                    exit;
                                }
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
                    else {
                        sendState(500, "Invalid request");
                        echo JsonUtils::createErrorJson(["Invalid rights specification"]);
                    }
                }
                else {
                    sendState(500, "Invalid request");
                    echo JsonUtils::createErrorJson($validationErrors);
                    exit;
                }
            }
            else {
                header("HTTP/1.1 500 Invalid request");
                echo JsonUtils::createErrorJson(["Invalid storeid (" . $storeId . ")"]);
                exit;
            }
        }
        else if (sizeof($urlparts) == 7 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'class' && $urlparts[6] == 'object') {
            $class = $db->getClass($urlparts[3], $urlparts[5]);
            if ($class) {
                $json = file_get_contents("php://input");
                $validationErrors = Jsonutils::validate($json, $class->createObjectSchema());
                if ($validationErrors && gettype($validationErrors) == 'boolean') {
                    $storeId = $urlparts[3];
                    if ($db->canCreateObject($class->getId())) {
                        try {
                            $newObject = $db->createObject($storeId, $class, json_decode($json));
                            sendState(200, "Object created");
                            echo $newObject->getId();
                        }
                        catch (Exception $err) {
                            sendState(500, "Object not created");
                            echo JsonUtils::createErrorJson([$err->getMessage()]);
                        }
                    }
                    else {
                        sendState(401, "Insufficient rights");
                    }
                }
                else {
                    sendState(500, "Invalid request");
                    echo JsonUtils::createErrorJson($validationErrors);
                }
                exit;
            }
            else {
                sendState(404, "Class not found");
            }
            exit;
        }
        else if (sizeof($urlparts) == 7 && $urlparts[1] == 'ada' && $urlparts[2] == 'store' && $urlparts[4] == 'object' && $urlparts[6] == 'checkin') {
            if ($db->canCheckin($urlparts[3], $urlparts[5])) {
                if ($db->checkin($urlparts[3], $urlparts[5], json_decode(file_get_contents('php://input')))) {
                    sendState(200, "Checked in");
                    echo "OK";
                }
                else 
                    sendState(500, "Checkin failed");
            }
            else
                sendState(401, "Insufficient rights");
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

    function streamfile_chunked($filename, $retbytes = TRUE) {
        $buffer = '';
        $cnt    = 0;
        $handle = fopen($filename, 'rb');
    
        if ($handle === false) {
            return false;
        }
    
        while (!feof($handle)) {
            $buffer = fread($handle, CHUNK_SIZE);
            echo $buffer;
            ob_flush();
            flush();
    
            if ($retbytes) {
                $cnt += strlen($buffer);
            }
        }
    
        $status = fclose($handle);
    
        if ($retbytes && $status) {
            return $cnt; // return num. bytes delivered like readfile() does.
        }
    
        return $status;
    }?>