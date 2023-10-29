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
                else if ($type == 'Bearer') {
                    return $this->evaluateOAuthAuthorization($auth);
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

        private function evaluateOAuthAuthorization($header) {
            $token = substr($header, strlen('Bearer '));

            foreach($this->idps as $idp) {
                if ($idp->getType() == 'oauth') {
                    return $this->validateToken($idp, $token);
                }
            }
        }

        private function validateToken($idp, $token) {
            $tokenItems = explode('.', $token);
            $tokenInfo = json_decode(base64_decode($tokenItems[1]));
            $postdata = http_build_query(
                array(
                    'client_secret' => $idp->getSetting('client.secret'),
                    'client_id' => $idp->getSetting('client.id'),
                    'username' => $tokenInfo->preferred_username,
                    'token' => $token                
                )
            );
            
            $opts = array('http' =>
                array(
                    'method'  => 'POST',
                    'header'  => 'Content-Type: application/x-www-form-urlencoded',
                    'content' => $postdata
                )
            );
            
            $context  = stream_context_create($opts);
            
            $info = file_get_contents($idp->getSetting('validate.endpoint.url'), false, $context);
            $infoJson = json_decode($info);
            //{"exp":1688300297,"iat":1688299997,"jti":"35e8f20a-77b8-44b0-8dca-1dc65977fe97","iss":"http://192.168.2.74:9443/auth/realms/arba","sub":"43bbb23a-07a2-4726-af26-32ba43570898","typ":"Bearer","azp":"intranet","session_state":"cbd77599-28b8-407d-8e15-d5be416651a6","acr":"1","realm_access":{"roles":["christenunie-beheerder"]},"scope":"email profile","sid":"cbd77599-28b8-407d-8e15-d5be416651a6","email_verified":false,"name":"Arjan Bas","preferred_username":"arjan","given_name":"Arjan","family_name":"Bas","email":"dev@arjanbas.nl"}
            $claimUserId = $idp->getSetting('claim.user.id');
            $claimUserMail = $idp->getSetting('claim.user.mail');
            $claimUserFirstName = $idp->getSetting('claim.user.first.name');
            $claimLastName = $idp->getSetting('claim.user.last.name');
            $user = new User($infoJson->$claimUserId, $infoJson->$claimUserMail, $infoJson->$claimUserFirstName, $infoJson->$claimLastName, $idp->getId());

            $clientId = $idp->getSetting('client.id');
            if (isset($infoJson->resource_access->$clientId)) {
                foreach($infoJson->resource_access->$clientId->roles as $role) {
                    $user->addRole($role);
                }
            }
            return $user;
        }
    }
?>