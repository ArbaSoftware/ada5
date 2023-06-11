<?php
    class AdaObject {
        private $id;
        private $classid;
        private $properties;
        private $content;

        public function __construct($id, $classid) {
            $this->id = $id;
            $this->classid = $classid;
            $this->properties = [];
            $this->content = null;
        }

        public function getId() {
            return $this->id;
        }

        public function getClassId() {
            return $this->classid;
        }

        public function toJson() {
            $json = "{\"id\":\"". $this->getId() . "\",\"classid\":\"" . $this->getClassId() . "\",\"properties\":[";
            $prefix = '';
            foreach($this->properties as $property) {
                $json .= $prefix . "{\"id\":\"" . $property["id"] . "\",\"name\":\"" . $property['name']. "\",\"type\":\"". $property['type'] . "\"";
                if ($property['type'] == "string")
                    $json .= ",\"value\":\"". $property['value'] . "\"}";
                else if ($property['type'] == "date") {
                    $json .= ",\"value\":{\"day\":" . $property['value']['day'] . ",";
                    $json .= "\"month\":" . $property['value']['month'] . ",";
                    $json .= "\"year\":" . $property['value']['year'] . "}";
                }
                else if ($property['type'] == "object") {
                    $json .= ",\"value\":\"" . $property['value'] . "\"}";
                }
                $prefix = ",";
            }
            $json .= "]";
            if (!is_null($this->content)) {
                if ($this->content["checkedout"])
                    $json .= ",\"content\":{\"majorversion\":" . $this->content['majorversion'] . ",\"minorversion\":" . $this->content['minorversion'] . ",\"mimetype\":\"" . $this->content['mimetype'] . "\",\"checkedout\":true, \"checkedoutuser\":\"" . $this->content['checkedoutuser'] . "\",\"checkedoutidentityproviderid\":\"" . $this->content['checkedoutidentityproviderid'] . "\"}";
                else
                    $json .= ",\"content\":{\"majorversion\":" . $this->content['majorversion'] . ",\"minorversion\":" . $this->content['minorversion'] . ",\"mimetype\":\"" . $this->content['mimetype'] . "\",\"checkedout\":false}";
            }
            $json .="}";
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

        public function setContent($majorversion, $minorversion, $mimetype, $checkedout, $checkoutuser = null, $checkoutidentityproviderid = null) {
            $this->content = [];
            $this->content["majorversion"] = $majorversion;
            $this->content["minorversion"] = $minorversion;
            $this->content["mimetype"] = $mimetype;
            $this->content["checkedout"] = $checkedout;
            $this->content['checkedoutuser'] = $checkoutuser;
            $this->content['checkedoutidentityproviderid'] = $checkoutidentityproviderid;
        }

    }
?>