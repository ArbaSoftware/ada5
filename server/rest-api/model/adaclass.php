<?php
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
?>