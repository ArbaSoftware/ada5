<?php
    class AdaObject {
        private $id;
        private $classid;
        private $properties;

        public function __construct($id, $classid) {
            $this->id = $id;
            $this->classid = $classid;
            $this->properties = [];
        }

        public function getId() {
            return $this->id;
        }

        public function getClassId() {
            return $this->classid;
        }

        public function toJson() {
            $json = "{\"id\":\"". $this->getId() . "\",\"classid\":\"" . $this->getClassId() . "\",properties:[";
            $prefix = '';
            foreach($this->properties as $property) {
                $json .= $prefix . "{\"id\":\"" . $property["id"] . "\",\"name\":\"" . $property['name']. "\",\"type\":\"". $property['type'] . "\"";
                if ($property['type'] == "string")
                    $json .= ",\"value\":\"". $property['value'] . "\"";
                else if ($property['type'] == "date") {
                    $json .= ",\"value\":{\"day\":" . $property['value']['day'] . ",";
                    $json .= "\"month\":" . $property['value']['month'] . ",";
                    $json .= "\"year\":" . $property['value']['year'] . "}";
                }
                $prefix = ",";
            }
            $json .= "]}";
            return $json;
        }

        public function addProperty($id, $name, $type, $value) {
            $this->properties[sizeof($this->properties)] = [
                "id" => $id,
                "name" => $name,
                "type" => $type,
                "value"=> $value
            ];
        }

    }
?>