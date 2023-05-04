<?php
    class Right {
        private $id;
        private $name;
        private $systemRight;
        private $level;
        private $domainRight;
        private $storeRight;
        private $classRight;
        private $objectRight;

        public function getName() {
            return $this->name;
        }

        public function getSystemRight() {
            return $this->systemRight;
        }

        public function getLevel() {
            return $this->level;
        }

        public function __construct($id, $name, $systemright, $level, $domainright, $storeright, $classright, $objectright) {
            $this->id = $id;
            $this->name = $name;
            $this->systemRight = $systemright;
            $this->level = $level;
            $this->domainRight = $domainright;
            $this->storeRight = $storeright;
            $this->classRight = $classright;
            $this->objectRight = $objectright;
        }

        public static function toJson($input) {
            if (is_array($input)) {
                $json = '[';
                foreach($input as $right) {
                    $json .= ($json == '[' ? '' : ',');
                    $json .= Right::toJson($right);
                }
                $json .= ']';
                return $json;
            }
            else {
                $right = $input;
                return '{' .
                        '"id":"' . $right->id . '",' .
                        '"name":"' . $right->name . '",' .
                        '"systemright":"' . $right->systemRight . '",' .
                        '"level":"' . $right->level . '",' .
                        '"domainright":' . ($right->domainRight == 1 ? "true" : "false") . ',' .
                        '"storeright":' . ($right->storeRight == 1 ? "true" : "false")  . ',' .
                        '"classright":' . ($right->classRight == 1 ? "true" : "false")  . ',' .
                        '"objectright":' . ($right->objectRight == 1 ? "true" : "false") .
                        '}';
            }
        }
    }
?>