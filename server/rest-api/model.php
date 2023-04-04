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

        public static function fromJson($json) {
            $result = new AdaClass(null, $json->name);
            if (isset($json->documentclass))
                $result->setIsDocumentClass($json->documentclass);
            if (isset($json->folderclass))
                $result->setIsFolderClass($json->folderclass);
            return $result;
        }

        public static function validateJson($input) {
            $properties = array_keys(get_object_vars($input));
            $validProperties = ["name", "description", "folderclass", "documentclass"];
            $propertyTypes = ["name" =>'string', "description"=>"string", "folderclass"=>"boolean", "documentclass"=>"boolean"];
            $requiredProperties = ["name"];
            $valid = true;
            foreach($properties as $property) {
                if (!in_array($property, $validProperties)) {
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
    }
?>