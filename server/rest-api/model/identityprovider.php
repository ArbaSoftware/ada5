<?php
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
    }
?>