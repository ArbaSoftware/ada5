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
            return $this->isFolderClass;
        }

        public function setIsDocumentClass($value) {
            $this->isDocumentClass = $value;
        }

        public function isDocumentClass() {
            return $this->isDocumentClass;
        }

        public function addProperty($property) {
            $this->properties[sizeof($this->properties)] = $property;
        }

        public function getProperties() {
            return $this->properties;
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
    }

    class Property {
        private $name;
        private $type;
        private $required = false;
        private $multiple = false;

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

        private function __construct($name, $type) {
            $this->name = $name;
            $this->type = $type;
        }

        public function getName() {
            return $this->name;
        }

        public function getType() {
            return $type;
        }

        public function isRequired() {
            return $this->required;
        }

        public function isMultiple() {
            return $this->multiple;
        }

        public static function fromJson($json) {
            if (Property::validJson($json)) {
                $result = new Property($json->name, $json->type);
                if (isset($json->json->required))
                    $result->required = $required;
                if (isset($json->json->multiple))
                    $result->multiple = $multiple;
                return $result;
            }
            else
                throw new Exception("Invalid property json");
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
                if ($json->grantee == 'everybody') 
                    return (in_array('level', $properties) && sizeof($properties) == 2);
                else
                    return (in_array('level', $properties) && in_array('identityprovider', $properties) && sizeof($properties) == 3);
            }
            else
                return false;
        }

        public static function fromJson($json) {
            if (GrantedRight::isValidJson($json)) {
                return new GrantedRight($json->grantee, $json->identityprovider, $json->level, ($json->grantee == 'everybody' ? 0: 1));
            }
            else
                throw new Exception("Invalid right json");
        }
    }
?>