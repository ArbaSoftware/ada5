<?php
    error_reporting(E_ALL);
    ini_set('display_errors', '1');
    include('db.php');
    include('mysql.php');
    include('model.php');
    include('logger.php');
    include('auth.php');
    include('model/httprequest.php');
    include('model/httpresponse.php');
    define('CHUNK_SIZE', 1024*1024);
    include('settings.php');

    $start = microtime(true);
    $request = new HttpRequest($_settings);

    if (!$request->isAuthorized()) {
        header("HTTP/1.1 401 Unauthorized");
        exit;
    }
    $handler = new StoreHandler($request, $_settings);

    $db = $handler->getDb();
    $db->updateUser($request->getUser());

    if ($request->matches("GET", "/ada/store")) {
        $handler->getStores();
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", "/ada/store/*")) {
        $handler->getStore($request->getUrlPart(3));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", "/ada/store/*/class")) {
        $handler->getClasses($request->getUrlPart(3));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", "/ada/store/*/class/*")) {
        $handler->getClass($request->getUrlPart(3), $request->getUrlPart(5));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", '/ada/store/*/object/*')) {
        $handler->getObject($request->getUrlPart(3), $request->getUrlPart(5));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", '/ada/store/*/object/*/content/*')) {
        $handler->getContent($request->getUrlPart(3), $request->getUrlPart(5), $request->getUrlPart(7));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", '/ada/store/*/object/*/path')) {
        $handler->getObjectPath($request->getUrlPart(3), $request->getUrlPart(5));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("GET", "/ada/store/*/object/*/checkout")) {
        $handler->checkoutObject($request->getUrlPart(3), $request->getUrlPart(5));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/object/*/relatedobjects")) {
        $handler->getRelatedObjects($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store")) {
        $handler->createStore(file_get_contents("php://input"));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/search")) {
        $handler->search($request->getUrlPart(3), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/class")) {
        $handler->createClass($request->getUrlPart(3), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/class/*/object")) {
        $handler->createObject($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents("php://input"));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/object/*/checkin")) {
        $handler->checkin($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/class/*/property")) {
        $handler->addProperty($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("POST", "/ada/store/*/relate")) {
        $handler->relateObjects($request->getUrlPart(3), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("DELETE", "/ada/store/*")) {
        $handler->deleteStore($request->getUrlPart(3));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("DELETE", "/ada/store/*/class/*/property/*")) {
        $handler->deleteProperty($request->getUrlPart(3), $request->getUrlPart(5), $request->getUrlPart(7));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("DELETE", "/ada/store/*/object/*")) {
        $handler->deleteObject($request->getUrlPart(3), $request->getUrlPart(5));
        exit;
    }
    else if ($request->matches("PUT", "/ada/store/*/class/*/property/*")) {
        $handler->updateProperty($request->getUrlPart(3), $request->getUrlPart(5), $request->getUrlPart(7), file_get_contents('php://input'));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("PUT", "/ada/store/*/class/*")) {
        $handler->updateClass($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents("php://input"));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("PUT", "/ada/store/*/object/*")) {
        $handler->updateObject($request->getUrlPart(3), $request->getUrlPart(5), file_get_contents("php://input"));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else if ($request->matches("PUT", "/ada/store/*")) {
        $handler->updateStore($request->getUrlPart(3), file_get_contents("php://input"));
        $db->addRequestPerformance($request->getUrl(), strtolower($request->getMethod()), microtime(true)-$start);
        exit;
    }
    else {
        HttpResponse::createErrorResponse(404, "Not found")->expose();
        exit;
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
    }

    class StoreHandler {
        public function __construct($request, $_settings) {
            $this->db = new MySql($_settings['dbhost'], $_settings['dbuser'], $_settings['dbpassword'], $_settings['dbname'], $request->getUser()->getId(), $request->getUser()->getIdentifyProviderId(), $_settings['contentdir']);
        }

        public function getDb() {
            return $this->db;
        }

        public function getStores() {
            try {
                HttpResponse::createResponse(200, "text/json", Store::toJsons($this->db->getStores()))->expose();
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->message)->expose();
            }
        }

        public function getStore($id) {
            try {
                if ($this->db->canGetStore($id)) {
                    $store = $this->db->getStore($id);
                    if ($store) {
                        HttpResponse::createResponse(200, "text/json", $store->toJson())->expose();
                    }
                    else {
                        HttpResponse::createErrorResponse(404,"Store not found (" . $id . ")")->expose();
                    }
                }
                else {
                    HttpResponse::createErrorResponse(401,"Unsufficient rights")->expose();
                }
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->message)->expose();
            }
        }

        public function getClasses($storeid) {
            try {
                $classes = $this->db->getClasses($storeid);
                HttpResponse::createResponse(200, "text/json", sizeof($classes) == 0 ? "[]": AdaClass::toJson($classes))->expose();
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->message)->expose();
            }
        }

        public function getClass($storeid, $classid) {
            try {
                if ($this->db->canGetClass($storeid, $classid)) {
                    $class = $this->db->getClass($storeid, $classid);
                    if ($class) {
                        $json = AdaClass::toJson($class);
                        HttpResponse::createResponse(200, "text/json", $json)->expose();
                    }
                    else {
                        HttpResponse::createErrorResponse(404, "Class not found");
                    }
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights");
                }
            }
            catch (Exception $exception) {
                HttpResponse::createErrorResponse(500, $exception->message)->expose();
            }
        }

        public function getObject($storeid, $objectid) {
            try {
                if ($this->db->canGetObject($storeid, $objectid)) {
                    $object = $this->db->getObject($storeid, $objectid);

                    if ($object) {
                        HttpResponse::createResponse(200, 'text/json', $object->toJson())->expose();
                    }
                    else {
                        HttpResponse::createErrorResponse(400, "Object not found")->expose();
                    }
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->message)->expose();
            }
        }

        public function getContent($storeid, $objectid, $version) {
            if ($this->db->canGetContent($storeid, $objectid)) {
                $content = $this->db->getContent($storeid, $objectid, $version);
                if ($content) {
                    header("Content-Type: " . $content->getMimetype());
                    header("Content-Length: " . $content->getSize());
                    header('Content-Disposition: attachment; filename="' . $content->getFileName() . "'");
                    streamfile_chunked($content->getContentFile());
                }
                else {
                    HttpResponse::createErrorResponse(404, "Content not found")->expose();
                }
            }
            else
                HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
    
        }

        public function getObjectPath($storeid, $objectid) {
            if ($this->db->canGetObject($storeid, $objectid)) {
                if ($path = $this->db->getObjectPath($storeid, $objectid)) {
                    $json = json_encode($path);
                    HttpResponse::createResponse(200, "text/json", $json)->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(404, "Object not found")->expose();
            }
        }

        public function checkoutObject($storeid, $objectid) {
            if ($this->db->canCheckout($storeid, $objectid)) {
                if ($this->db->checkout($storeid, $objectid)) {
                    HttpResponse::createResponse(200, "text/text", "OK")->expose();
                }
                else 
                    HttpResponse::createErrorResponse(500, "Checkout failed")->expose();
            }
            else
                HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
        }

        public function createStore($json) {
            try {
                $request = json_decode($json);
                if ($errors = JsonUtils::validate($json, 'addstorerequest')) {
                    if ($this->db->isStoreNameUnique($request->name)) {
                        if ($this->db->canCreateStore()) {
                            if ($newStoreId = $this->db->createStore($request->name, $request->grantedrights, $request->addons)) {
                                HttpResponse::createResponse(200, "text/text", $newStoreId)->expose();
                            }
                            else
                                HttpResponse::createErrorResponse(500, "Store creation failed")->expose();
                        }
                        else
                            HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                    }
                    else {
                        HttpResponse::createErrorResponse(500, "Store name not unique")->expose();
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
                    HttpResponse::createErrorResponse(500, $errorJson)->expose();
                    exit;
                }
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->getMessage())->expose();
            }
        }

        public function search($storeid, $search) {
            $validationerrors = JsonUtils::validate($search, "search");
            if ($validationerrors && gettype($validationerrors) == 'boolean') {
                $searchresults = $this->db->search($storeid, json_decode($search));
                if (gettype($searchresults) == "string") {
                    HttpResponse::createResponse(200, "text/json", $searchresults)->expose();
                }
                else {
                    HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson($searchresults));
                }
            }
            else {
                HttpResponse::createErrorResponse(500,JsonUtils::createErrorJson($validationerrors));
            }
        }

        public function createClass($storeId, $json) {
            if ($this->db->getStore($storeId)) {
                $validationErrors = JsonUtils::validate($json, "addclassrequest");
                if ($validationErrors && gettype($validationErrors) == "boolean") {
                    $request = json_decode($json);
                    if ($this->db->areValidRights($request->rights)) {
                        if ($this->db->isUniqueClassName($storeId, $request->name)) {
                            if ($this->db->canCreateClass($storeId)) {
                                $class = AdaClass::fromJson($request);
                                $newClassId = $this->db->createClass($storeId, $class);
                                if (gettype($newClassId) == 'array') {
                                    HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson($newClassId))->expose();
                                }
                                else {
                                    HttpResponse::createResponse(200, "text/text", $newClassId)->expose();
                                }
                            }
                            else {
                                Logger::log('No rights to create class');
                                HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                            }
                        }
                        else {
                            HttpResponse::createErrorResponse(500, "Class name not unique")->expose();
                        }
                    }
                    else {
                        HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson(["Invalid rights specification"]))->expose();
                    }
                }
                else {
                    HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson($validationErrors))->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson(["Invalid storeid (" . $storeId . ")"]))->expose();
            }
        }

        public function createObject($storeId, $classid, $json) {
            try {
                $class = $this->db->getClass($storeId, $classid);
                if ($class) {
                    //$validationErrors = Jsonutils::validate($json, $class->createObjectSchema());
                    $validationErrors = true;
                    if (isset($validationErrors) && gettype($validationErrors) == 'boolean') {
                        if ($this->db->canCreateObject($classid)) {
                            try {
                                $newObject = $this->db->createObject($storeId, $class, json_decode($json));
                                HttpResponse::createResponse(200, "text/text", $newObject->getId())->expose();
                            }
                            catch (Exception $err) {
                                HttpResponse::createErrorResponse(500, JsonUtils::createErrorJson([$err->getMessage()]))->expose();
                            }
                        }
                        else {
                            HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                        }
                    }
                    else {
                        HttpResponse::createErrorResponse(500, "Invalid request")->expose();
                    }
                }
                else {
                    HttpResponse::createErrorResponse(404, "Class not found")->expose();
                }
            }
            catch (Exception $err) {
                HttpResponse::createErrorResponse(500, $err->getMessage())->expose();
            }
        }

        public function checkin($storeid, $objectid, $content) {
            if ($this->db->canCheckin($storeid, $objectid)) {
                if ($this->db->checkin($storeid, $objectid, json_decode($content))) {
                    HttpResponse::createResponse(200, "text/text", "OK")->expose();
                }
                else 
                    HttpResponse::createErrorResponse(500, "Checkin failed")->expose();
            }
            else
                HttpResponse::createErrorResponse(401, "Insufficient rights");
        }

        public function addProperty($storeid, $classid, $json) {
            try {
                if ($this->db->canEditClass($storeid, $classid)) {
                    if ($propertyId = $this->db->addProperty($classid, json_decode($json)))
                        HttpResponse::createResponse(200, "text/json", "{\"id\":\"" . $propertyId . "\"}")->expose();
                    else
                        HttpResponse::createErrorResponse(500, "")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
            }
            catch (Exception $exception) {
                HttpResponse::createErrorResponse(500, "")->expose();
            }

        }

        public function deleteStore($storeId) {
            if ($this->db->canDeleteStore($storeId)) {
                if ($this->db->deleteStore($storeId)) {
                    HttpResponse::createResponse(200, "text/text", "Store deleted")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(500, "")->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(401, "Unsufficient rights")->expose();
            }
        }

        public function deleteObject($storeId, $objectid) {
            if ($this->db->canDeleteObject($storeId, $objectid)) {
                if ($this->db->deleteObject($storeId, $objectid)) {
                    HttpResponse::createResponse(200, "text/text", "Object deleted")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(500, "")->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(401, "Unsufficient rights")->expose();
            }
        }

        public function deleteProperty($storeid, $classid, $propertyid) {
            try {
                if ($this->db->canEditClass($storeid, $classid)) {
                    if ($this->db->deleteProperty($propertyid))
                        HttpResponse::createResponse(200, "text/text", "OK")->expose();
                    else
                        HttpResponse::createErrorResponse(500, "")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
            }
            catch (Exception $exception) {
                HttpResponse::createErrorResponse(500, $exception->getMessage())->expose();
            }

        }

        public function updateProperty($storeid, $classid, $propertyid, $json) {
            try {
                if ($this->db->canEditClass($storeid, $classid)) {
                    if ($this->db->editProperty($classid, $propertyid, json_decode($json)))
                        HttpResponse::createResponse(200, "text/text", "OK")->expose();
                    else
                        HttpResponse::createErrorResponse(500, "")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
            }
            catch (Exception $exception) {
                HttpResponse::createErrorResponse(500, $exception->getMessage())->expose();
            }
    
        }

        public function updateClass($storeid, $classid, $json) {
            try {
                if ($this->db->canEditClass($storeid, $classid)) {
                    if ($this->db->updateClass($classid, json_decode($json)))
                        HttpResponse::createResponse(200, "text/text", "OK")->expose();
                    else
                        HttpResponse::createErrorResponse(500, "")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
            }
            catch (Exception $exception) {
                HttpResponse::createErrorResponse(500, $exception->getMessage())->expose();
            }
        }

        public function updateStore($storeid, $request) {
            $validationerrors = JsonUtils::validate($request, "updatestore");
            if (isset($validationerrors) && gettype($validationerrors) == 'boolean') {
                if ($this->db->canEditStore($storeid)) {
                    echo "Updating store";
                    if ($this->db->updateStore($storeid, json_decode($request)))
                        HttpResponse::createResponse(200, "text/text", "OK")->expose();
                    else
                        HttpResponse::createErrorResponse(500, "")->expose();
                }
                else {
                    HttpResponse::createErrorResponse(401, "")->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(500, "")->expose();
            }
        }

        public function updateObject($storeid, $objectid, $request) {
            Logger::log($request);
            $classId = $this->db->getObjectClassId($objectid);
            $objectClass = $this->db->getClass($storeid, $classId);
            Logger::log($objectClass->updateObjectSchema());
            /*
            $validationerrors = JsonUtils::validate($request, $objectClass->updateObjectSchema());
            if (isset($validationerrors) && gettype($validationerrors) == 'boolean') {
                */
                if ($this->db->canEditObject($objectid)) {
                    if ($this->db->updateObject($objectid, json_decode($request))) {
                        HttpResponse::createResponse(200, "text/text", "OK")->expose();
                    }
                    else {
                        HttpResponse::createErrorResponse(500, "")->expose();
                    }
                }
                else {
                    Logger::log("Insufficient rights");
                    HttpResponse::createErrorResponse(401, "Insufficient rights")->expose();
                }
                /*
            }
            else {
                $errorJson = "{\"error\": \"Invalid request\", \"messages\": [";
                $prefix = "";
                foreach($errors as $error) {
                    $errorJson .= $prefix . '"'. $error . '"';
                    $prefix = ',';
                }
                $errorJson .= "]}";
                Logger::log($errorJson);
                HttpResponse::createErrorResponse(500, $errorJson)->expose();
            }
            */
    
        }

        public function relateObjects($storeid, $json) {
            $validationErrors = JsonUtils::validate($json, "relateobjects");
            if (isset($validationErrors) && gettype($validationErrors) == 'boolean') {
                $request = json_decode($json);
                if ($this->db->relateObjects($storeid, $request->object1, $request->object2, $request->type)) {
                    HttpResponse::createResponse(200, "text/text", "OK")->expose();
                }
                else {
                    $response = HttpResponse::createErrorResponse(500, "Could not create relation");
                    $response->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(500, "Invalid request")->expose();
            }
        }

        public function getRelatedObjects($storeid, $objectid, $json) {
            $validationErrors = JsonUtils::validate($json, "relatedobjectsrequest");
            if (isset($validationErrors) && gettype($validationErrors) == 'boolean') {
                $relatedObjects = $this->db->getRelatedObjects($objectid, json_decode($json));
                if (gettype($relatedObjects) == "string")
                    HttpResponse::createResponse(200, "text/json", $relatedObjects)->expose();
                else {
                    HttpResponse::createErrorResponse(404, "Related objects not found")->expose();
                }
            }
            else {
                HttpResponse::createErrorResponse(500, "Invalid request")->expose();
            }
        }
    }
?>