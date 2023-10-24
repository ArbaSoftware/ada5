<?php
    class GrantedRight {
        private $granteeId;
        private $granteeType;
        private $identityProviderId;
        private $level;
        private $weight;
        private $user;

        public function __construct($granteeid, $granteetype, $identityproviderid, $level, $weight) {
            $this->granteeId = $granteeid;
            $this->identityProviderId = $identityproviderid;
            $this->level = $level;
            $this->weight = $weight;
            $this->granteeType = $granteetype;
        }

        public function getGranteeId() {
            return $this->granteeId;
        }

        public function getGranteeType() {
            return $this->granteeType;
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
                    return (in_array('level', $properties));
                else
                    return (in_array('level', $properties) && in_array('identityprovider', $properties) && in_array('granteetype', $properties) && sizeof($properties) == 4);
            }
            else
                return false;
        }

        public static function fromJson($json) {
            if (GrantedRight::isValidJson($json)) {
                return new GrantedRight($json->grantee, isset($json->granteetype) ? $json->granteetype: "", $json->identityprovider, $json->level, ($json->grantee == 'everyone' ? 0: 1));
            }
            else {
                throw new Exception("Invalid right json: ");
            }
        }

        public function toJson() {
            $json = '{"granteeid":"' . $this->getGranteeId() . '","granteetype":"' . $this->getGranteeType() . '", "identityproviderid":"' . $this->getIdentityProviderId() . '","level":' . $this->getLevel();
            if ($this->user)
                $json .= ',"user":' . $this->user->toJson();
            $json .= '}';
            return $json;
        }

        public function setUser($user) {
            $this->user = $user;
        }
    }
?>