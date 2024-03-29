<?php
    class AdaClass {
        private $id;
        private $name;
        private $isFolderClass = false;
        private $isDocumentClass = false;
        private $description;
        private $properties;
        private $rights;
        private $parentClass;

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

        public function setParentClass($id) {
            $this->parentClass = $id;
        }

        public function getParentClass() {
            return $this->parentClass;
        }

        public static function fromJson($json) {
            $result = new AdaClass(null, $json->name);
            if (isset($json->documentclass))
                $result->setIsDocumentClass($json->documentclass);
            if (isset($json->folderclass))
                $result->setIsFolderClass($json->folderclass);
            if (isset($json->parentclass))
                $result->setParentClass($json->parentclass);
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
            if (isset($json->parentclass))
                $result->setParentClass($json->parentclassid);
            if (isset($json->description))
                $result->setDescription($json->description);
            if (isset($result->properties)) {
                foreach($json->properties as $property) {
                    $result->addProperty(Property::fromJson($property));
                }
            }
            return $result;
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
                if (!is_null($classes->getParentClass())) {
                    $json .= ',"parentclass":"' . $classes->getParentClass() . '"';
                }
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
                if ($rights = $classes->getRights()) {
                    $json .= ',"grantedrights":[';
                    $first = true;
                    foreach($rights as $right) {
                        $json .= ($first? '': ',');
                        $first = false;
                        $json .= $right->toJson();
                    }
                    $json.=']';
                }
                $json .= '}';
                return $json;
            }
        }

        public function createObjectSchema() {
            $schema = "{";
            if ($this->getProperties() || $this->isDocumentClass()) {
                $schema .= "\"properties\": {\"type\": \"arrayofobjects\", \"schema\":{";
                $prefix = "";
                foreach($this->getProperties() as $property) {
                    $schema .= $prefix . "\"" . $property->getName() . "\": {";
                    $schema .= "\"type\":\"" . $property->getType() . "\",";
                    $schema .= "\"required\":" . ($property->isRequired() ? "true": "false");
                    $schema .= "}";
                    $prefix = ",";
                }
                $schema .= "}},";
            }
            if ($this->isDocumentClass()) {
                $schema .= "\"content\": {";
                $schema .= "\"schema\":\"content\",";
                $schema .= "\"required\": false";
                $schema .= "},";
            }
            $schema .= "\"rights\":{";
            $schema .= "\"type\":\"arrayofobjects\",";
            $schema .= "\"required\":false,";
            $schema .= "\"schema\": \"right\"";
            $schema .= "}";
            $schema .= "}";
            return $schema;
        }


        public function updateObjectSchema() {
            $schema = "{";
            $schema .= '"id": {"type": "string", "required":true},';
            $schema .= '"classid": {"type": "string", "required":false},';
            if ($this->getProperties() || $this->isDocumentClass()) {
                $schema .= "\"properties\": {\"arrayofobjects\", \"schema\":{";
                $prefix = "";
                foreach($this->getProperties() as $property) {
                    $schema .= $prefix . "\"" . $property->getName() . "\": {";
                    $schema .= "\"type\":\"" . $property->getType() . "\",";
                    $schema .= "\"required\":" . ($property->isRequired() ? "true": "false");
                    $schema .= "}";
                    $prefix = ",";
                }
                $schema .= "}},";
            }
            if ($this->isDocumentClass()) {
                $schema .= "\"content\": {";
                $schema .= "\"schema\":\"content\",";
                $schema .= "\"required\": false";
                $schema .= "},";
            }
            $schema .= "\"rights\":{";
            $schema .= "\"type\":\"arrayofobjects\",";
            $schema .= "\"required\":false,";
            $schema .= "\"schema\": \"right\"";
            $schema .= "}";
            $schema .= "}";
            return $schema;
        }

    }
?>