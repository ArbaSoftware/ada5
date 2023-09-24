<?php
    class Property {
        private $name;
        private $type;
        private $required = false;
        private $multiple = false;
        private $id;
        private $valueclass = null;

        public static function validJson($input) {
            $properties = array_keys(get_object_vars($input));
            if (in_array("name", $properties) && in_array("type", $properties)) {
                $allowedProperties = ["name", "type", "required", "multiple","class"];
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
                $result = new Property(isset($json->id) ? $json->id : "", $json->name, $json->type);
                if (isset($json->json->required))
                    $result->required = $json->required;
                if (isset($json->json->multiple))
                    $result->multiple = $json->multiple;
                if (isset($json->class))
                    $result->valueclass = $json->class;
                return $result;
            }
            else
                throw new Exception("Invalid property json");
        }

        public function getValueClass() {
            if ($this->getType() == "object" && isset($this->valueclass))
                return $this->valueclass;
            else
                return null;
        }

        public function toJson() {
            $json = '{"id":"' . $this->getId() . '","name":"' . $this->getName() . '","type":"' . $this->getType() .'"';
            $json .= ',"required":' . ($this->isRequired() ? "true" : "false");
            $json .= ',"multiple":' . ($this->isMultiple() ? "true" : "false");
            $json .= '}';
            return $json;
        }
    }
?>