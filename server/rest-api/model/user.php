<?php
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

        public function getEmail() {
            return $this->email;
        }

        public function getFirstName() {
            return $this->firstname;
        }

        public function getLastName() {
            return $this->lastname;
        }

        public function toJson() {
            return '{"id":"' . $this->getId() . '","email":"' . $this->getEmail() . '","firstname":"' . $this->getFirstName() . '","lastname": "' . $this->getLastName() . '"}';
        }

    }
?>