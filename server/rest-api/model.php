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
