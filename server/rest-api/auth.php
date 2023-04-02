<?php
    class Auth {
        private $idps;
        private $db;

        public function __construct() {
            $this->db = new MySql('192.168.2.74', 'ada', 'ada', 'ada5');
            $this->idps = $this->db->getIdentifyProviders();
        }

        public function isAuthorized() {
            if (array_key_exists('Authorization', getallheaders())) {
                $auth = getallheaders()['Authorization'];
                $type = substr($auth, 0, strpos($auth, ' '));
                if ($type == 'Basic') {
                    return $this->evaluateBasicAuthorization($auth);
                }
                else
                    return false;
            }
            else
                return false;
        }

        private function evaluateBasicAuthorization($header) {
            $base64 = substr($header, strlen('Basic '));
            $credentials = explode(':', base64_decode($base64));
            $email = $credentials[0];
            $passwordhash = base64_encode(hash("sha256", $credentials[1]));

            foreach($this->idps as $idp) {
                if ($idp->getType() == 'internal') {
                    if ($user = $this->db->getInternalUser($email, $passwordhash, $idp->getId())) {
                        return $user;
                    }
                }
            }
            return null;
        }
    }
?>