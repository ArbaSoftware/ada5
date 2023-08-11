<?php
    class IdentityProvider {
        private $id;
        private $name;
        private $type;
        private $settings;

        public function __construct($id, $name, $type) {
            $this->id = $id;
            $this->name = $name;
            $this->type = $type;
            $this->settings = array();
        }

        public function addSetting($name, $value) {
            $this->settings[$name] = $value;
        }

        public function getSetting($name) {
            return $this->settings[$name];
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

        public static function toJson($input) {
            if (is_array($input)) {
                $json = '[';
                foreach($input as $provider) {
                    $json .= ($json == '[' ? '': ',');
                    $json .= IdentityProvider::toJson($provider);
                }
                $json .= ']';
                return $json;
            }
            else {
                return '{' . 
                    '"id":"' . $input->id . '",' .
                    '"name":"' . $input->name . '",' .
                    '"type":"' . $input->type . '"' .
                    '}';
            }
        }

        public function searchUsers($search) {
            //Get api token
            $postdata = http_build_query(
                array(
                    'client_secret' => $this->getSetting('client.secret'),
                    'client_id' => $this->getSetting('client.id'),
                    'username' => $this->getSetting('api.user'),
                    'password' => $this->getSetting('api.password'),
                    'grant_type' => 'password'
                )
            );
            
            $opts = array('http' =>
                array(
                    'method'  => 'POST',
                    'header'  => 'Content-Type: application/x-www-form-urlencoded',
                    'content' => $postdata
                )
            );
            $context = stream_context_create($opts);
            $tokenJson = file_get_contents($this->getSetting('token.url'), false, $context);
            $token = json_decode($tokenJson)->access_token;

            //search users
            $opts = array('http' =>
                array(
                    'method' => 'GET',
                    'header' => 'Authorization: Bearer ' . $token
                )
            );
            $context = stream_context_create($opts);
            $adaUsersGroupMembers = json_decode(file_get_contents($this->getSetting('api.url') . '/groups/'. $this->getSetting('ada.users.group.id') . '/members', false, $context));
            $memberIds = [];
            foreach($adaUsersGroupMembers as $member) {
                $memberIds[sizeof($memberIds)] = $member->id;
            }
            $usersJson = file_get_contents($this->getSetting('api.url') . '/users?search=' . $search, false, $context);
            $oauthUsers = json_decode($usersJson);
            $users = [];
            foreach($oauthUsers as $user) {
                if (in_array($user->id, $memberIds)) {
                    $newUser = new User($user->id, $user->email, $user->firstName, $user->lastName, $this->getId());
                    $users[sizeof($users)] = $newUser;
                }
            }
            return $users;
        }

        public function getRoles() {
            //Get api token
            $postdata = http_build_query(
                array(
                    'client_secret' => $this->getSetting('client.secret'),
                    'client_id' => $this->getSetting('client.id'),
                    'username' => $this->getSetting('api.user'),
                    'password' => $this->getSetting('api.password'),
                    'grant_type' => 'password'
                )
            );
            
            $opts = array('http' =>
                array(
                    'method'  => 'POST',
                    'header'  => 'Content-Type: application/x-www-form-urlencoded',
                    'content' => $postdata
                )
            );
            $context = stream_context_create($opts);
            $tokenJson = file_get_contents($this->getSetting('token.url'), false, $context);
            $token = json_decode($tokenJson)->access_token;

            //get roles
            $opts = array('http' =>
                array(
                    'method' => 'GET',
                    'header' => 'Authorization: Bearer ' . $token
                )
            );
            $context = stream_context_create($opts);
            $roles = json_decode(file_get_contents($this->getSetting('api.url') . '/roles', false, $context));
            $results = [];
            $json = '[';
            $first = true;
            foreach($roles as $role) {
                $json .= ($first ? '': ',');
                $json .= '{"id":"' . $role->id. '","name":"' . $role->name . '"}';
                $first = false;
            }
            $json .= ']';
            return $json;
      }
    }
?>