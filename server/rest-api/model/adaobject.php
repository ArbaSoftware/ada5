<?php
    class AdaObject {
        private $id;
        private $class;

        public function __construct($id, $class) {
            $this->id = $id;
            $this->class = $class;
        }

        public function getId() {
            return $this->id;
        }

        public function getClass() {
            return $this->class;
        }

    }
?>