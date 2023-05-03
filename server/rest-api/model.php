<?php
    class Store {
        private $id;
        private $name;

        public function __construct($id, $name) {
            $this->id = $id;
            $this->name = $name;
        }

        public function getId() {
            return $this->id;
        }

        public function getName() {
            return $this->name;
        }

        public function toJson() {
            return json_encode(["id" => $this->getId(), "name"=>$this->getName()]);
        }

        public static function toJsons($stores) {
            $json = "[";
            foreach($stores as $store) {
                if ($json != '[')
                    $json .= ',';
                $json .= $store->toJson();
            }
            $json .= ']';
            return $json;
        }
    }

    class IdentityProvider {
        private $id;
        private $name;
        private $type;

        public function __construct($id, $name, $type) {
            $this->id = $id;
            $this->name = $name;
            $this->type = $type;
        }

        public function getId() {
            return $this->id;
        }

        public function getName() {
            return $this->name;
        }

        public function getType() {
            return $this->type;
        }

        public static function toJson($input) {
            if (is_array($input)) {
                $json = '[';
                foreach($input as $provider) {
                    $json .= ($json == '[' ? '': ',');
                    $json .= IdentityProvider::toJson($provider);
                }
                $json .= ']';
                return $json;
            }
            else {
                return '{' . 
                    '"id":"' . $input->id . '",' .
                    '"name":"' . $input->name . '",' .
                    '"type":"' . $input->type . '"' .
                    '}';
            }
        }
    }

    class User {
        private $id;
        private $email;
        private $firstname;
        private $lastname;
        private $identityproviderid;

        public function __construct($id, $email, $firstname, $lastname, $identityproviderid) {
            $this->id = $id;
            $this->email = $email;
            $this->firstname = $firstname;
            $this->lastname = $lastname;
            $this->identityproviderid = $identityproviderid;
        }

        public function getId() {
            return $this->id;
        }

        public function getIdentifyProviderId() {
            return $this->identityproviderid;
        }
    }

    class AdaClass {
        private $id;
        private $name;
        private $isFolderClass = false;
        private $isDocumentClass = false;
        private $description;
        private $properties;
        private $rights;

        public function __construct($id, $name) {
            $this->id = $id;
            $this->name = $name;
            $this->properties = [];
            $this->rights = [];
        }

        public function getId() {
            return $this->id;
        }

        public function getName() {
            return $this->name;
        }

        public function setDescription($description) {
            $this->description = $description;
        }

        public function getDescription() {
            return $this->description;
        }

        public function setIsFolderClass($value) {
            $this->isFolderClass = $value;
        }

        public function isFolderClass() {
            return $this->isFolderClass==1;
        }

        public function setIsDocumentClass($value) {
            $this->isDocumentClass = $value;
        }

        public function isDocumentClass() {
            return $this->isDocumentClass==1;
        }

        public function addProperty($property) {
            $this->properties[sizeof($this->properties)] = $property;
        }

        public function getProperties() {
            return isset($this->properties) ? $this->properties: false;
        }

        public static function fromJson($json) {
            $result = new AdaClass(null, $json->name);
            if (isset($json->documentclass))
                $result->setIsDocumentClass($json->documentclass);
            if (isset($json->folderclass))
                $result->setIsFolderClass($json->folderclass);
            if (isset($result->properties)) {
                foreach($json->properties as $property)
                    $result->addProperty(Property::fromJson($property));
            }
            if (isset($json->rights)) {
                foreach($json->rights as $right) 
                    $result->addRight(GrantedRight::fromJson($right));
            }
            return $result;
        }

        public static function fromAddon($json) {
            $result = new AdaClass(null, $json->name);
            if (isset($json->documentclass))
                $result->setIsDocumentClass($json->documentclass);
            if (isset($json->folderclass))
                $result->setIsFolderClass($json->folderclass);
            if (isset($json->description))
                $result->setDescription($json->description);
            if (isset($result->properties)) {
                foreach($json->properties as $property)
                    $result->addProperty(Property::fromJson($property));
            }
            return $result;
        }

        public static function validateJson($input) {
            $properties = array_keys(get_object_vars($input));
            $validProperties = ["name", "description", "folderclass", "documentclass", "properties", "rights"];
            $propertyTypes = ["name" =>'string', "description"=>"string", "folderclass"=>"boolean", "documentclass"=>"boolean"];
            $requiredProperties = ["name"];
            $valid = true;
            foreach($properties as $property) {
                if ($property == 'properties') {
                    if (is_array($input->properties)) {
                        $invalidPropertyFound = false;
                        foreach($input->properties as $property) {
                            if (!Property::validJson($property)) {
                                $invalidPropertyFound = true;
                            }
                        }
                        if ($invalidPropertyFound) {
                            $valid = false;
                            break;
                        }
                    }
                    else {
                        $valid = false;
                        break;
                    }
                }
                else if ($property == 'rights') {
                    $invalidRightFound = false;
                    foreach($input->rights as $right) {
                        if (!GrantedRight::isValidJson($right)) {
                            $invalidRightFound = true;
                            break;
                        }
                    }
                    if ($invalidRightFound) {
                        $valid = false;
                        break;
                    }
                }
                else if (!in_array($property, $validProperties)) {
                    $valid = false;
                    break;
                }
                else if (gettype($input->$property) != $propertyTypes[$property]) {
                    $valid = false;
                    break;
                }
            }
            if ($valid) {
                foreach($requiredProperties as $property) {
                    if (!in_array($property, $properties)) {
                        $valid = false;
                        break;
                    }
                }
            }
            if (isset($input->documentclass) && isset($input->folderclass) && $input->documentclass && $input->folderclass)
                $valid = false;
            return $valid;
        }

        public function getRights() {
            return $this->rights;
        }

        public function addRight($right) {
            $this->rights[sizeof($this->rights)] = $right;
        }

        public static function toJson($classes) {
            if (is_array($classes)) {
                $json = '[';
                foreach($classes as $class) {
                    if ($json != '[')
                        $json .= ',';
                    $json .= AdaClass::toJson($class);
                }
                $json .= ']';
                return $json;
            }
            else {
                $json = '{';
                $json .= '"id":"' . $classes->getId() . '",';
                $json .= '"name":"' . $classes->getName() . '",';
                $json .= '"folderclass":' . ($classes->isFolderClass() ? 'true':'false') . ',';
                $json .= '"documentclass":' . ($classes->isDocumentClass() ? 'true': 'false');
                if ($props = $classes->getProperties()) {
                    $json .= ',"properties":[';
                    $first = true;
                    foreach($props as $prop) {
                        $json .= ($first? '' : ',');
                        $first = false;
                        $json .= $prop->toJson();
                    }
                    $json .= ']';
                }
                $json .= '}';
                return $json;
            }
        }
    }

    class Property {
        private $name;
        private $type;
        private $required = false;
        private $multiple = false;
        private $id;

        public static function validJson($input) {
            $properties = array_keys(get_object_vars($input));
            if (in_array("name", $properties) && in_array("type", $properties)) {
                $allowedProperties = ["name", "type", "required", "multiple"];
                $invalidPropertyFound = false;
                foreach($properties as $property) {
                    if (!in_array($property, $allowedProperties)) {
                        $invalidPropertyFound = true;
                    }
                }
                return !$invalidPropertyFound;
            }
            else
                return false;
        }

        public function __construct($id, $name, $type) {
            $this->name = $name;
            $this->type = $type;
            $this->id = $id;
        }

        public function getId() {
            return $this->id;
        }

        public function getName() {
            return $this->name;
        }

        public function getType() {
            return $this->type;
        }

        public function isRequired() {
            return $this->required;
        }

        public function setRequired($value) {
            $this->required = $value;
        }

        public function isMultiple() {
            return $this->multiple;
        }

        public function setMultiple($value) {
            $this->multiple = $value;
        }

        public static function fromJson($json) {
            if (Property::validJson($json)) {
                $result = new Property($json->id, $json->name, $json->type);
                if (isset($json->json->required))
                    $result->required = $required;
                if (isset($json->json->multiple))
                    $result->multiple = $multiple;
                return $result;
            }
            else
                throw new Exception("Invalid property json");
        }

        public function toJson() {
            $json = '{"id":"' . $this->getId() . '","name":"' . $this->getName() . '","type":"' . $this->getType() .'"';
            $json .= ',"required":' . ($this->isRequired() ? "true" : "false");
            $json .= ',"multiple":' . ($this->isMultiple() ? "true" : "false");
            $json .= '}';
            return $json;
        }
    }

    class GrantedRight {
        private $granteeId;
        private $identityProviderId;
        private $level;
        private $weight;

        public function __construct($granteeid, $identityproviderid, $level, $weight) {
            $this->granteeId = $granteeid;
            $this->identityProviderId = $identityproviderid;
            $this->level = $level;
            $this->weight = $weight;
        }

        public function getGranteeId() {
            return $this->granteeId;
        }

        public function getIdentityProviderId() {
            return $this->identityProviderId;
        }

        public function getLevel() {
            return $this->level;
        }

        public function getWeight() {
            return $this->weight;
        }

        public static function isValidJson($json) {
            $properties = array_keys(get_object_vars($json));
            if (in_array('grantee', $properties)) {
                if ($json->grantee == 'everyone') 
                    return (in_array('level', $properties) && sizeof($properties) == 2);
                else
                    return (in_array('level', $properties) && in_array('identityprovider', $properties) && sizeof($properties) == 3);
            }
            else
                return false;
        }

        public static function fromJson($json) {
            if (GrantedRight::isValidJson($json)) {
                return new GrantedRight($json->grantee, $json->identityprovider, $json->level, ($json->grantee == 'everyone' ? 0: 1));
            }
            else
                throw new Exception("Invalid right json");
        }
    }

    class Right {
        private $id;
        private $name;
        private $systemRight;
        private $level;
        private $domainRight;
        private $storeRight;
        private $classRight;
        private $objectRight;

        public function getName() {
            return $this->name;
        }

        public function getSystemRight() {
            return $this->systemRight;
        }

        public function getLevel() {
            return $this->level;
        }

        public function __construct($id, $name, $systemright, $level, $domainright, $storeright, $classright, $objectright) {
            $this->id = $id;
            $this->name = $name;
            $this->systemRight = $systemright;
            $this->level = $level;
            $this->domainRight = $domainright;
            $this->storeRight = $storeright;
            $this->classRight = $classright;
            $this->objectRight = $objectright;
        }

        public static function toJson($input) {
            if (is_array($input)) {
                $json = '[';
                foreach($input as $right) {
                    $json .= ($json == '[' ? '' : ',');
                    $json .= Right::toJson($right);
                }
                $json .= ']';
                return $json;
            }
            else {
                $right = $input;
                return '{' .
                        '"id":"' . $right->id . '",' .
                        '"name":"' . $right->name . '",' .
                        '"systemright":"' . $right->systemRight . '",' .
                        '"level":"' . $right->level . '",' .
                        '"domainright":' . ($right->domainRight == 1 ? "true" : "false") . ',' .
                        '"storeright":' . ($right->storeRight == 1 ? "true" : "false")  . ',' .
                        '"classright":' . ($right->classRight == 1 ? "true" : "false")  . ',' .
                        '"objectright":' . ($right->objectRight == 1 ? "true" : "false") .
                        '}';
            }
        }
    }

    class Addon {
        public static function validateJson($json, $db) {
            $input = json_decode($json);
            if ($input) {
                if (!$input->classes)
                    $input->classes = [];
                if (strlen($input->id) > 0 && strlen($input->name) > 0 && is_array($input->classes)) {
                    $validClasses = true;
                    foreach($input->classes as $class) {
                        if (!Addon::validateClass($class, $db)) {
                            $validClasses = false;
                            break;
                        }
                    }
                    return $validClasses;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        private static function validateClass($class, $db) {
            $allowedproperties = ['name', 'description', 'folderclass', 'documentclass', 'properties', 'security'];
            $propertytypes = ["name"=>"string", "description"=>"string", "folderclass"=>"boolean","documentclass"=>"boolean","properties"=>"array","security"=>"array"];
            $properties = array_keys(get_object_vars($class));
            $invalidProperty= false;
            foreach($properties as $property) {
                if (!in_array($property, $allowedproperties)) {
                    $invalidProperty = true;
                    break;
                }
                else if (gettype($class->$property) != $propertytypes[$property]) {
                    $invalidProperty = true;
                    break;
                }
                else if ($property == 'properties' && !Addon::validateProperties($class->properties)) {
                    $invalidProperty = true;
                    break;
                }
                else if ($property == 'security' && !Addon::validateSecurity($class->security, $db)) {
                    $invalidProperty = true;
                    break;
                }
            }
            return !$invalidProperty;
        }

        private static function validateProperties($properties) {
            $foundInvalid = false;
            foreach($properties as $property) {
                if (!Addon::validateProperty($property)) {
                    $foundInvalid = true;
                    echo $property->name . ' invalid';
                    break;
                }
            }
            return !$foundInvalid;
        }

        private static function validateProperty($property) {
            $allowedproperties = ['name', 'type', 'required', 'multiple'];
            $propertytypes = ["name"=>"string", "type"=>"string", "required"=>"boolean","multiple"=>"boolean"];
            $properties = array_keys(get_object_vars($property));
            $invalidProperty= false;
            foreach($properties as $propertyproperty) {
                if (!in_array($propertyproperty, $allowedproperties)) {
                    $invalidProperty = true;
                    break;
                }
                else if (gettype($property->$propertyproperty) != $propertytypes[$propertyproperty]) {
                    $invalidProperty = true;
                    break;
                }
            }
            return !$invalidProperty;
        }

        private static function validateSecurity($security, $db) {
            $invalid = false;
            $rights = $db->getRights();
            foreach($security as $current) {
                $properties = array_keys(get_object_vars($current));
                if (sizeof($properties) != 2 || !in_array('grantee', $properties) || !in_array('rights', $properties)) {
                    $invalid = true;
                    break;
                }
                else {
                    foreach($current->rights as $right) {
                        $foundRight = false;
                        foreach($rights as $checkright) {
                            if ($checkright->getName() == $right || $checkright->getSystemRight() == $right) {
                                $foundRight = true;
                                break;
                            }
                        }
                        if (!$foundRight) {
                            $invalid = true;
                            break;
                        }
                    }
                }
            }
            return !$invalid;
        }
    }
?>