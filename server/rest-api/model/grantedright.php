<?php
    class GrantedRight {
        private $granteeId;
        private $identityProviderId;
        private $level;
        private $weight;

        public function __construct($granteeid, $identityproviderid, $level, $weight) {
            $this->granteeId = $granteeid;
            $this->identityProviderId = $identityproviderid;
            $this->level = $level;
            $this->weight = $weight;
        }

        public function getGranteeId() {
            return $this->granteeId;
        }

        public function getIdentityProviderId() {
            return $this->identityProviderId;
        }

        public function getLevel() {
            return $this->level;
        }

        public function getWeight() {
            return $this->weight;
        }

        public static function isValidJson($json) {
            $properties = array_keys(get_object_vars($json));
            if (in_array('grantee', $properties)) {
                if ($json->grantee == 'everyone') 
                    return (in_array('level', $properties) && sizeof($properties) == 2);
                else
                    return (in_array('level', $properties) && in_array('identityprovider', $properties) && sizeof($properties) == 3);
            }
            else
                return false;
        }

        public static function fromJson($json) {
            if (GrantedRight::isValidJson($json)) {
                return new GrantedRight($json->grantee, $json->identityprovider, $json->level, ($json->grantee == 'everyone' ? 0: 1));
            }
            else
                throw new Exception("Invalid right json");
        }
    }
?>