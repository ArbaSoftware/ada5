<?php
    class Store {
        private $id;
        private $name;
        private $datecreated;
        private $creator;
        private $creatoridentityproviderid;
        private $lastmodifiedon;
        private $lastmodifier;
        private $lastmodifieridentityproviderid;

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
            return json_encode(["id" => $this->getId(), "name"=>$this->getName(), "datecreated" => $this->parseDate($this->getDateCreated()), "creator" => $this->getCreator(), "creatoridentityproviderid" => $this->getCreatorIdentityProviderId(), "lastmodified" => $this->parseDate($this->getLastModified()), "lastmodifier" => $this->getLastModifier(), "lastmodifieridentityproviderid" => $this->getCreatorIdentityProviderId()]);
        }

        private function parseDate($value) {
            if (is_null($value)) {
                return null;
            }
            else if (gettype($value) == "string") {
                //2023-06-22 20:32:28
                $parts = explode(" ", $value);
                $dateparts = explode("-", $parts[0]);
                $timeparts = explode(":", $parts[1]);
                return ["day" => intval($dateparts[2]), "month" => intval($dateparts[1]), "year" => intval($dateparts[0]), "hour" => intval($timeparts[0]), "minute" => intval($timeparts[1]), "second" => intval($timeparts[2])];
            }
            else
                return null;
        }

        public function setDateCreated($value) {
            $this->datecreated = $value;
        }

        public function getDateCreated() {
            return $this->datecreated;
        }

        public function setCreator($creator) {
            $this->creator = $creator;
        }

        public function getCreator() {
            return $this->creator;
        }

        public function setCreatorIdentityProviderId($id) {
            $this->creatoridentityproviderid = $id;
        }

        public function getCreatorIdentityProviderId() {
            return $this->creatoridentityproviderid;
        }

        public function setLastModified($value) {
            $this->lastmodifiedon = $value;
        }

        public function getLastModified() {
            return $this->lastmodifiedon;
        }

        public function setLastModifier($value) {
            $this->lastmodifier = $value;
        }

        public function getLastModifier() {
            return $this->lastmodifier;
        }

        public function setLastModifierIdentityProviderId($id) {
            $this->lastmodifieridentityproviderid = $id;
        }

        public function getLastModifierIdentityProviderId() {
            return $this->lastmodifieridentityproviderid;
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