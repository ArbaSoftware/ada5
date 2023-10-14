<?php
    class User {
        private $id;
        private $email;
        private $firstname;
        private $lastname;
        private $identityproviderid;
        private $roles;

        public function __construct($id, $email, $firstname, $lastname, $identityproviderid) {
            $this->id = $id;
            $this->email = $email;
            $this->firstname = $firstname;
            $this->lastname = $lastname;
            $this->identityproviderid = $identityproviderid;
            $this->roles = [];
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

        public function addRole($role) {
            $this->roles[sizeof($this->roles)] = $role;
        }

        public function hasRoles() {
            return sizeof($this->roles) > 0;
        }

        public function getRoles() {
            return $this->roles;
        }

        public function toJson() {
            return '{"id":"' . $this->getId() . '","email":"' . $this->getEmail() . '","firstname":"' . $this->getFirstName() . '","lastname": "' . $this->getLastName() . '"}';
        }

    }
?>