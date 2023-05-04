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
?>